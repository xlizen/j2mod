/*
 * This file is part of j2mod-steve.
 *
 * j2mod is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * j2mod is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses
 */
package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.msg.ReadFileRecordResponse.RecordResponse;
import com.ghgande.j2mod.modbus.procimg.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>Read File Record</tt> request.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 * @version @version@ (@date@)
 */
public final class ReadFileRecordRequest extends ModbusRequest {
    private RecordRequest[] m_Records;

    /**
     * Constructs a new <tt>Read File Record</tt> request instance.
     */
    public ReadFileRecordRequest() {
        super();

        setFunctionCode(Modbus.READ_FILE_RECORD);

		/*
		 * Request size byte is all that is required.
		 */
        setDataLength(1);
    }

    /**
     * getRequestSize -- return the total request size. This is useful for
     * determining if a new record can be added.
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
     * getRequestCount -- return the number of record requests in this message.
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
     * getResponse -- get an empty response for this message.
     */
    public ModbusResponse getResponse() {
        ReadFileRecordResponse response;

        response = new ReadFileRecordResponse();

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
     * The ModbusCoupler doesn't have a means of reporting the slave state or ID
     * information.
     */
    public ModbusResponse createResponse() {
        ReadFileRecordResponse response;
        response = (ReadFileRecordResponse)getResponse();

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
                if (recordRequest.getFileNumber() < 0
                        || recordRequest.getFileNumber() >= procimg.getFileCount()) {
                    return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
                }

                File file = procimg.getFileByNumber(recordRequest.getFileNumber());

                if (recordRequest.getRecordNumber() < 0
                        || recordRequest.getRecordNumber() >= file.getRecordCount()) {
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

                    data[j] = register.toShort();
                }
                RecordResponse recordResponse = new RecordResponse(data);
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
     * readData -- read all the data for this request.
     */
    public void readData(DataInput din) throws IOException {
        int m_ByteCount = din.readUnsignedByte();

        int recordCount = m_ByteCount / 7;
        m_Records = new RecordRequest[recordCount];

        for (int i = 0; i < recordCount; i++) {
            if (din.readByte() != 6) {
                throw new IOException();
            }

            int file = din.readUnsignedShort();
            int record = din.readUnsignedShort();
            if (record < 0 || record >= 10000) {
                throw new IOException();
            }

            int count = din.readUnsignedShort();

            m_Records[i] = new RecordRequest(file, record, count);
        }
    }

    /**
     * getMessage -- return the PDU message.
     */
    public byte[] getMessage() {
        byte request[] = new byte[1 + 7 * m_Records.length];

        int offset = 0;
        request[offset++] = (byte)(request.length - 1);

        for (RecordRequest m_Record : m_Records) {
            m_Record.getRequest(request, offset);
            offset += 7;
        }
        return request;
    }

    public static class RecordRequest {
        private int m_FileNumber;
        private int m_RecordNumber;
        private int m_WordCount;

        public RecordRequest(int file, int record, int count) {
            m_FileNumber = file;
            m_RecordNumber = record;
            m_WordCount = count;
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

        /**
         * getRequestSize -- return the size of the response in bytes.
         */
        public int getRequestSize() {
            return 7 + m_WordCount * 2;
        }

        public void getRequest(byte[] request, int offset) {
            request[offset] = 6;
            request[offset + 1] = (byte)(m_FileNumber >> 8);
            request[offset + 2] = (byte)(m_FileNumber & 0xFF);
            request[offset + 3] = (byte)(m_RecordNumber >> 8);
            request[offset + 4] = (byte)(m_RecordNumber & 0xFF);
            request[offset + 5] = (byte)(m_WordCount >> 8);
            request[offset + 6] = (byte)(m_WordCount & 0xFF);
        }

        public byte[] getRequest() {
            byte[] request = new byte[7];

            getRequest(request, 0);

            return request;
        }
    }
}