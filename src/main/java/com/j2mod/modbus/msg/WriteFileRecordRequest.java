/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.j2mod.modbus.msg;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.ModbusCoupler;
import com.j2mod.modbus.msg.WriteFileRecordResponse.RecordResponse;
import com.j2mod.modbus.procimg.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>Write File Record</tt> request.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 * @version @version@ (@date@)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public final class WriteFileRecordRequest extends ModbusRequest {
    private RecordRequest[] m_Records;

    /**
     * Constructs a new <tt>Write File Record</tt> request
     * instance.
     */
    public WriteFileRecordRequest() {
        super();

        setFunctionCode(Modbus.WRITE_FILE_RECORD);

		/*
		 * Set up space for the initial header.
		 */
        setDataLength(1);
    }

    /**
     * getRequestSize -- return the total request size.  This is useful
     * for determining if a new record can be added.
     *
     * @return size in bytes of response.
     */
    public int getRequestSize() {
        if (m_Records == null) {
            return 1;
        }

        int size = 1;
        for (RecordRequest m_Record : m_Records) {
            size += m_Record.getRequestSize();
        }

        return size;
    }

    /**
     * getRequestCount -- return the number of record requests in this
     * message.
     */
    public int getRequestCount() {
        if (m_Records == null) {
            return 0;
        }

        return m_Records.length;
    }

    /**
     * getRecord -- return the record request indicated by the reference
     */
    public RecordRequest getRecord(int index) {
        return m_Records[index];
    }

    /**
     * addRequest -- add a new record request.
     */
    public void addRequest(RecordRequest request) {
        if (request.getRequestSize() + getRequestSize() > 248) {
            throw new IllegalArgumentException();
        }

        if (m_Records == null) {
            m_Records = new RecordRequest[1];
        }
        else {
            RecordRequest old[] = m_Records;
            m_Records = new RecordRequest[old.length + 1];

            System.arraycopy(old, 0, m_Records, 0, old.length);
        }
        m_Records[m_Records.length - 1] = request;

        setDataLength(getRequestSize());
    }

    /**
     * createResponse -- create an empty response for this request.
     */
    public ModbusResponse getResponse() {
        WriteFileRecordResponse response;

        response = new WriteFileRecordResponse();

		/*
         * Copy any header data from the request.
		 */
        response.setHeadless(isHeadless());
        if (!isHeadless()) {
            response.setTransactionID(getTransactionID());
            response.setProtocolID(getProtocolID());
        }

		/*
         * Copy the unit ID and function code.
		 */
        response.setUnitID(getUnitID());
        response.setFunctionCode(getFunctionCode());

        return response;
    }

    /**
     * The ModbusCoupler doesn't have a means of writing file records.
     */
    public ModbusResponse createResponse() {
        WriteFileRecordResponse response;
        response = (WriteFileRecordResponse)getResponse();

		/*
		 * Get the process image.
		 */
        ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
		
		/*
		 * There is a list of requests to be resolved.
		 */
        try {
            for (int i = 0; i < getRequestCount(); i++) {
                RecordRequest recordRequest = getRecord(i);
                if (recordRequest.getFileNumber() < 0 ||
                        recordRequest.getFileNumber() >= procimg.getFileCount()) {
                    return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
                }

                File file = procimg.getFileByNumber(recordRequest.getFileNumber());

                if (recordRequest.getRecordNumber() < 0 ||
                        recordRequest.getRecordNumber() >= file.getRecordCount()) {
                    return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
                }

                Record record = file.getRecord(recordRequest.getRecordNumber());
                int registers = recordRequest.getWordCount();
                if (record == null && registers != 0) {
                    return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
                }

                short data[] = new short[registers];
                for (int j = 0; j < registers; j++) {
                    Register register = record.getRegister(j);
                    if (register == null) {
                        return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
                    }

                    register.setValue(recordRequest.getRegister(j).getValue());
                    data[j] = recordRequest.getRegister(j).toShort();
                }
                RecordResponse recordResponse = new RecordResponse(file.getFileNumber(), record == null ? 0 : record.getRecordNumber(), data);
                response.addResponse(recordResponse);
            }
        }
        catch (IllegalAddressException e) {
            return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        return response;
    }

    /**
     * writeData -- output this Modbus message to dout.
     */
    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    /**
     * readData -- convert the byte stream into a request.
     */
    public void readData(DataInput din) throws IOException {
        int m_ByteCount = din.readUnsignedByte();

        m_Records = new RecordRequest[0];

        for (int offset = 1; offset + 7 < m_ByteCount; ) {
            int function = din.readUnsignedByte();
            int file = din.readUnsignedShort();
            int record = din.readUnsignedShort();
            int count = din.readUnsignedShort();

            offset += 7;

            if (function != 6) {
                throw new IOException();
            }

            if (record < 0 || record >= 10000) {
                throw new IOException();
            }

            if (count < 0 || count >= 126) {
                throw new IOException();
            }

            short registers[] = new short[count];
            for (int j = 0; j < count; j++) {
                registers[j] = din.readShort();
                offset += 2;
            }
            RecordRequest dummy[] = new RecordRequest[m_Records.length + 1];
            if (m_Records.length > 0) {
                System.arraycopy(m_Records, 0, dummy, 0, m_Records.length);
            }

            m_Records = dummy;
            m_Records[m_Records.length - 1] = new RecordRequest(file, record, registers);
        }
    }

    /**
     * getMessage -- return the raw binary message.
     */
    public byte[] getMessage() {
        byte results[] = new byte[getRequestSize()];

        results[0] = (byte)(getRequestSize() - 1);

        int offset = 1;
        for (RecordRequest m_Record : m_Records) {
            m_Record.getRequest(results, offset);
            offset += m_Record.getRequestSize();
        }
        return results;
    }

    public static class RecordRequest {
        private int m_FileNumber;
        private int m_RecordNumber;
        private int m_WordCount;
        private byte m_Data[];

        public RecordRequest(int file, int record, short[] values) {
            m_FileNumber = file;
            m_RecordNumber = record;
            m_WordCount = values.length;
            m_Data = new byte[m_WordCount * 2];

            int offset = 0;
            for (int i = 0; i < m_WordCount; i++) {
                m_Data[offset++] = (byte)(values[i] >> 8);
                m_Data[offset++] = (byte)(values[i] & 0xFF);
            }
        }

        public int getFileNumber() {
            return m_FileNumber;
        }

        public int getRecordNumber() {
            return m_RecordNumber;
        }

        public int getWordCount() {
            return m_WordCount;
        }

        public SimpleRegister getRegister(int register) {
            if (register < 0 || register >= m_WordCount) {
                throw new IllegalAddressException("0 <= " + register + " < " + m_WordCount);
            }
            byte b1 = m_Data[register * 2];
            byte b2 = m_Data[register * 2 + 1];

            return new SimpleRegister(b1, b2);
        }

        /**
         * getRequestSize -- return the size of the response in bytes.
         */
        public int getRequestSize() {
            return 7 + m_WordCount * 2;
        }

        public void getRequest(byte[] request, int offset) {
            request[offset++] = 6;
            request[offset++] = (byte)(m_FileNumber >> 8);
            request[offset++] = (byte)(m_FileNumber & 0xFF);
            request[offset++] = (byte)(m_RecordNumber >> 8);
            request[offset++] = (byte)(m_RecordNumber & 0xFF);
            request[offset++] = (byte)(m_WordCount >> 8);
            request[offset++] = (byte)(m_WordCount & 0xFF);

            System.arraycopy(m_Data, 0, request, offset, m_Data.length);
        }

        public byte[] getRequest() {
            byte[] request = new byte[7 + 2 * m_WordCount];

            getRequest(request, 0);

            return request;
        }
    }
}
