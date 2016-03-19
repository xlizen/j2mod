/*
 * This file is part of j2mod.
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
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>ReadFileRecordResponse</tt>.
 *
 * @author Julie (jfh@ghgande.com)
 * @version @version@ (@date@)
 */
public final class ReadFileRecordResponse extends ModbusResponse {

    public static class RecordResponse {
        private int m_WordCount;
        private byte[] m_Data;

        public int getWordCount() {
            return m_WordCount;
        }

        public SimpleRegister getRegister(int register) {
            if (register < 0 || register >= m_WordCount) {
                throw new IndexOutOfBoundsException("0 <= " + register + " < " + m_WordCount);
            }
            byte b1 = m_Data[register * 2];
            byte b2 = m_Data[register * 2 + 1];

            return new SimpleRegister(b1, b2);
        }

        /**
         * getResponseSize -- return the size of the response in bytes.
         *
         * The response is a byte count, a function code, then wordCount
         * words (2 bytes).
         */
        public int getResponseSize() {
            return 2 + (m_WordCount * 2);
        }

        /**
         * getResponse - return the response data for this record
         *
         * The response data is the byte size of the response, minus this
         * byte, the function code (6), then the raw byte data for the
         * registers (m_WordCount * 2 bytes).
         *
         * @param request Request message
         * @param offset Offset into buffer
         */
        public void getResponse(byte[] request, int offset) {
            request[offset] = (byte)(1 + (m_WordCount * 2));
            request[offset + 1] = 6;
            System.arraycopy(m_Data, 0, request, offset + 2, m_Data.length);
        }

        public byte[] getResponse() {
            byte[] request = new byte[getResponseSize()];
            getResponse(request, 0);
            return request;
        }

        public RecordResponse(short data[]) {
            m_WordCount = data.length;
            m_Data = new byte[m_WordCount * 2];

            int offset = 0;
            for (int i = 0; i < m_WordCount; i++) {
                m_Data[offset++] = (byte)(data[i] >> 8);
                m_Data[offset++] = (byte)(data[i] & 0xFF);
            }
        }
    }

    private RecordResponse[] m_Records = null;

    /**
     * Returns the number of bytes needed for the response.
     *
     * The response is 1 byte for the total response size, plus
     * the sum of the sizes of all the records in the response.
     *
     * @return the number of bytes in the response.
     */
    public int getByteCount() {
        if (m_Records == null) {
            return 1;
        }

        int size = 1;
        for (RecordResponse m_Record : m_Records) {
            size += m_Record.getResponseSize();
        }

        return size;
    }

    /**
     * getRecordCount -- return the number of records in the response.
     *
     * @return count of records in response.
     */
    public int getRecordCount() {
        if (m_Records == null) {
            return 0;
        }

        return m_Records.length;
    }

    /**
     * getRecord -- return the record response indicated by the reference
     */
    public RecordResponse getRecord(int index) {
        return m_Records[index];
    }

    /**
     * addResponse -- add a new record response.
     */
    public void addResponse(RecordResponse response) {
        if (m_Records == null) {
            m_Records = new RecordResponse[1];
        }
        else {
            RecordResponse old[] = m_Records;
            m_Records = new RecordResponse[old.length + 1];

            System.arraycopy(old, 0, m_Records, 0, old.length);
        }
        m_Records[m_Records.length - 1] = response;
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(getByteCount() - 1);

        if (m_Records == null) {
            return;
        }

        for (RecordResponse m_Record : m_Records) {
            dout.write(m_Record.getResponse());
        }
    }

    public void readData(DataInput din) throws IOException {
        int m_ByteCount = (din.readUnsignedByte() & 0xFF);

        int remainder = m_ByteCount;
        while (remainder > 0) {
            int length = din.readUnsignedByte();
            remainder--;

            int function = din.readByte();
            remainder--;

            if (function != 6 || (length - 1) > remainder) {
                throw new IOException("Invalid response format");
            }
            short[] data = new short[(length - 1) / 2];
            for (int i = 0; i < data.length; i++) {
                data[i] = din.readShort();
                remainder -= 2;
            }
            RecordResponse response = new RecordResponse(data);
            addResponse(response);
        }
        setDataLength(m_ByteCount + 1);
    }

    public byte[] getMessage() {
        byte result[];

        result = new byte[getByteCount()];

        int offset = 0;
        result[offset++] = (byte)(result.length - 1);

        for (RecordResponse m_Record : m_Records) {
            m_Record.getResponse(result, offset);
            offset += m_Record.getWordCount() * 2;
        }
        return result;
    }

    /**
     * Constructs a new <tt>ReadFileRecordResponse</tt> instance.
     */
    public ReadFileRecordResponse() {
        super();

        setFunctionCode(Modbus.READ_FILE_RECORD);
    }
}