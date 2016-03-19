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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>ReadMEIResponse</tt>.
 *
 * Derived from similar class for Read Coils response.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 * @version 1.2rc1-ghpc (09/27/2010)
 */
public final class ReadMEIResponse
        extends ModbusResponse {

    //instance attributes
    private int m_FieldLevel = 0;
    private int m_Conformity = 1;
    private int m_FieldCount = 0;
    private String m_Fields[] = new String[64];
    private int m_FieldIds[] = new int[64];
    private boolean m_MoreFollows = false;
    private int m_NextFieldId;

    /**
     * Constructs a new <tt>ReadMEIResponse</tt>
     * instance.
     */
    public ReadMEIResponse() {
        super();
        setFunctionCode(Modbus.READ_MEI);
    }

    /**
     * Returns the number of fields
     * read with the request.
     * <p>
     * @return the number of fields that have been read.
     */
    public int getFieldCount() {
        if (m_Fields == null) {
            return 0;
        }
        else {
            return m_Fields.length;
        }
    }

    /**
     * Returns the array of strings that were read
     */
    public synchronized String[] getFields() {
        String[] dest = new String[m_Fields.length];
        System.arraycopy(m_Fields, 0, dest, 0, dest.length);
        return dest;
    }

    /**
     * Convenience method that returns the field
     * at the requested index
     * <p>
     * @param index the index of the field which
     *       should be returned.
     *
     * @return requested field
     *
     * @throws IndexOutOfBoundsException if the
     *         index is out of bounds
     */
    public String getField(int index) throws IndexOutOfBoundsException {
        return m_Fields[index];
    }

    /**
     * Convenience method that returns the field
     * ID at the given index.
     * <p>
     * @param index the index of the field for which
     *        the ID should be returned.
     *
     * @return field ID
     *
     * @throws IndexOutOfBoundsException if the
     *         index is out of bounds
     */
    public int getFieldId(int index) throws IndexOutOfBoundsException {
        return m_FieldIds[index];
    }

    public void setFieldLevel(int level) {
        m_FieldLevel = level;
    }

    public void addField(int id, String text) {
        m_FieldIds[m_FieldCount] = id;
        m_Fields[m_FieldCount] = text;
        m_FieldCount++;
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    public void readData(DataInput din) throws IOException {
        int byteCount;

        int subCode = din.readUnsignedByte();
        if (subCode != 0xE) {
            throw new IOException("Invalid sub code");
        }

        m_FieldLevel = din.readUnsignedByte();
        m_Conformity = din.readUnsignedByte();
        m_MoreFollows = din.readUnsignedByte() == 0xFF;
        m_NextFieldId = din.readUnsignedByte();

        m_FieldCount = din.readUnsignedByte();

        byteCount = 6;

        if (m_FieldCount > 0) {
            m_Fields = new String[m_FieldCount];
            m_FieldIds = new int[m_FieldCount];

            for (int i = 0; i < m_FieldCount; i++) {
                m_FieldIds[i] = din.readUnsignedByte();
                int len = din.readUnsignedByte();
                byte data[] = new byte[len];
                din.readFully(data);
                m_Fields[i] = new String(data, "UTF-8");

                byteCount += 2 + len;
            }
            setDataLength(byteCount);
        }
        else {
            setDataLength(byteCount);
        }
    }

    public byte[] getMessage() {
        int size = 6;

        for (int i = 0; i < m_FieldCount; i++) {
          /*
           * Add the field ID
		   */
            size++;

		  /*
		   * Add the string length byte and the
		   * actual string length.
		   */
            size++;
            size += m_Fields[i].length();
        }

        byte result[] = new byte[size];
        int offset = 0;

        result[offset++] = 0x0E;
        result[offset++] = (byte)m_FieldLevel;
        result[offset++] = (byte)m_Conformity;
        result[offset++] = (byte)(m_MoreFollows ? 0xFF : 0);
        result[offset++] = (byte)m_NextFieldId;
        result[offset++] = (byte)m_FieldCount;

        for (int i = 0; i < m_FieldCount; i++) {
            result[offset++] = (byte)m_FieldIds[i];
            result[offset++] = (byte)m_Fields[i].length();
            System.arraycopy(m_Fields[i].getBytes(), 0, result, offset, m_Fields[i].length());
            offset += m_Fields[i].length();
        }

        return result;
    }

}