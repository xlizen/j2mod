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
 * Class implementing a <tt>WriteFileRecordResponse</tt>.
 *
 * @author Julie
 * @version 0.96
 */
public final class WriteFileRecordResponse extends ModbusResponse {
    private RecordResponse[] m_Records;

    public static class RecordResponse {
        private int m_FileNumber;
        private int m_RecordNumber;
        private int m_WordCount;
        private byte m_Data[];

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
                throw new IndexOutOfBoundsException("0 <= " + register + " < " + m_WordCount);
            }
            byte b1 = m_Data[register * 2];
            byte b2 = m_Data[register * 2 + 1];

            return new SimpleRegister(b1, b2);
        }

        /**
         * getResponseSize -- return the size of the response in bytes.
         */
        public int getResponseSize() {
            return 7 + m_WordCount * 2;
        }

        public void getResponse(byte[] response, int offset) {
            response[offset++] = 6;
            response[offset++] = (byte)(m_FileNumber >> 8);
            response[offset++] = (byte)(m_FileNumber & 0xFF);
            response[offset++] = (byte)(m_RecordNumber >> 8);
            response[offset++] = (byte)(m_RecordNumber & 0xFF);
            response[offset++] = (byte)(m_WordCount >> 8);
            response[offset++] = (byte)(m_WordCount & 0xFF);

            System.arraycopy(m_Data, 0, response, offset, m_Data.length);
        }

        public byte[] getResponse() {
            byte[] response = new byte[7 + 2 * m_WordCount];

            getResponse(response, 0);

            return response;
        }

        public RecordResponse(int file, int record, short[] values) {
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
    }

    /**
     * getRequestSize -- return the total request size.  This is useful
     * for determining if a new record can be added.
     *
     * @return size in bytes of response.
     */
    public int getResponseSize() {
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
    public RecordResponse getRecord(int index) {
        return m_Records[index];
    }

    /**
     * addResponse -- add a new record response.
     */
    public void addResponse(RecordResponse response) {
        if (response.getResponseSize() + getResponseSize() > 248) {
            throw new IllegalArgumentException();
        }

        if (m_Records == null) {
            m_Records = new RecordResponse[1];
        }
        else {
            RecordResponse old[] = m_Records;
            m_Records = new RecordResponse[old.length + 1];

            System.arraycopy(old, 0, m_Records, 0, old.length);
        }
        m_Records[m_Records.length - 1] = response;

        setDataLength(getResponseSize());
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    public void readData(DataInput din) throws IOException {
        int m_ByteCount = din.readUnsignedByte();

        m_Records = new RecordResponse[0];

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
            RecordResponse dummy[] = new RecordResponse[m_Records.length + 1];
            if (m_Records.length > 0) {
                System.arraycopy(m_Records, 0, dummy, 0, m_Records.length);
            }

            m_Records = dummy;
            m_Records[m_Records.length - 1] = new RecordResponse(file, record, registers);
        }
    }

    public byte[] getMessage() {
        byte results[] = new byte[getResponseSize()];

        results[0] = (byte)(getResponseSize() - 1);

        int offset = 1;
        for (RecordResponse m_Record : m_Records) {
            m_Record.getResponse(results, offset);
            offset += m_Record.getResponseSize();
        }
        return results;
    }

    /**
     * Constructs a new <tt>WriteFileRecordResponse</tt> instance.
     */
    public WriteFileRecordResponse() {
        super();

        setFunctionCode(Modbus.WRITE_FILE_RECORD);
        setDataLength(7);
    }
}