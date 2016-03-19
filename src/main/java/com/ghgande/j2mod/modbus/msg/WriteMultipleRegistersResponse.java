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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>WriteMultipleRegistersResponse</tt>. The
 * implementation directly correlates with the class 0 function <i>read multiple
 * registers (FC 16)</i>. It encapsulates the corresponding response message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public final class WriteMultipleRegistersResponse extends ModbusResponse {
    // instance attributes
    private int m_WordCount;
    private int m_Reference;

    /**
     * Constructs a new <tt>WriteMultipleRegistersResponse</tt> instance.
     */
    public WriteMultipleRegistersResponse() {
        super();

        setFunctionCode(Modbus.WRITE_MULTIPLE_REGISTERS);
        setDataLength(4);
    }

    /**
     * Constructs a new <tt>WriteMultipleRegistersResponse</tt> instance.
     *
     * @param reference the offset to start reading from.
     * @param wordcount the number of words (registers) to be read.
     */
    public WriteMultipleRegistersResponse(int reference, int wordcount) {
        super();

        setFunctionCode(Modbus.WRITE_MULTIPLE_REGISTERS);
        setDataLength(4);

        m_Reference = reference;
        m_WordCount = wordcount;
    }

    /**
     * Sets the reference of the register to start writing to with this
     * <tt>WriteMultipleRegistersResponse</tt>.
     * <p>
     *
     * @param ref the reference of the register to start writing to as
     *            <tt>int</tt>.
     */
    public void setReference(int ref) {
        m_Reference = ref;
    }

    /**
     * Returns the reference of the register to start writing to with this
     * <tt>WriteMultipleRegistersResponse</tt>.
     * <p>
     *
     * @return the reference of the register to start writing to as <tt>int</tt>
     * .
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * Returns the number of bytes that have been written.
     *
     * @return the number of bytes that have been read as <tt>int</tt>.
     */
    public int getByteCount() {
        return m_WordCount * 2;
    }

    /**
     * Returns the number of words that have been read. The returned value
     * should be half of the byte count of the response.
     * <p>
     *
     * @return the number of words that have been read as <tt>int</tt>.
     */
    public int getWordCount() {
        return m_WordCount;
    }

    /**
     * Sets the number of words that have been returned.
     *
     * @param count the number of words as <tt>int</tt>.
     */
    public void setWordCount(int count) {
        m_WordCount = count;
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    public void readData(DataInput din) throws IOException {
        setReference(din.readUnsignedShort());
        setWordCount(din.readUnsignedShort());

        setDataLength(4);
    }

    public byte[] getMessage() {
        byte result[] = new byte[4];

        result[0] = (byte)((m_Reference >> 8) & 0xff);
        result[1] = (byte)(m_Reference & 0xff);
        result[2] = (byte)((m_WordCount >> 8) & 0xff);
        result[3] = (byte)(m_WordCount & 0xff);

        return result;
    }
}