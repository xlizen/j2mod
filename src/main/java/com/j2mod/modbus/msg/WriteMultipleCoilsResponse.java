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
package com.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>WriteMultipleCoilsResponse</tt>. The implementation
 * directly correlates with the class 1 function <i>write multiple coils (FC
 * 15)</i>. It encapsulates the corresponding response message.
 * <p>
 * Coils are understood as bits that can be manipulated (i.e. set or cleared).
 *
 * @author Dieter Wimberger
 * @author Julie Haugh
 * @version 1.05
 *
 *          20140426 - Refactor and verify the new methods.<br>
 */
public final class WriteMultipleCoilsResponse
        extends ModbusResponse {

    // instance attributes
    private int m_Reference;
    private int m_BitCount;

    /**
     * Constructs a new <tt>WriteMultipleCoilsResponse</tt> instance with a
     * given count of coils and starting reference.
     * <p>
     *
     * @param ref   the offset to begin writing from.
     * @param count the number of coils to be written.
     */
    public WriteMultipleCoilsResponse(int ref, int count) {
        super();

        m_Reference = ref;
        m_BitCount = count;

        setDataLength(4);
    }

    /**
     * Constructs a new <tt>WriteMultipleCoilsResponse</tt> instance.
     */
    public WriteMultipleCoilsResponse() {
        super();

        setDataLength(4);
    }

    /**
     * getReference - Returns the reference of the coil to start reading from
     * with this <tt>WriteMultipleCoilsResponse</tt>.
     * <p>
     *
     * @return the reference of the coil to start reading from as <tt>int</tt>.
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * setReference - Sets the reference to the coil that is the first coil in
     * this response.
     *
     * @param ref
     */
    public void setReference(int ref) {
        m_Reference = ref;
    }

    /**
     * getBitCount - Returns the quantity of coils written with the request.
     * <p>
     *
     * @return the quantity of coils that have been written.
     */
    public int getBitCount() {
        return m_BitCount;
    }

    /**
     * setBitCount - Sets the number of coils that will be in a response.
     *
     * @param count the number of coils in the response.
     */
    public void setBitCount(int count) {
        m_BitCount = count;
    }

    /**
     * writeData - Copy the attribute values for this message to the output
     * buffer.
     */
    public void writeData(DataOutput dout) throws IOException {

        dout.writeShort(m_Reference);
        dout.writeShort(m_BitCount);
    }

    /**
     * readData - Initialize the attribute values for this message from the
     * input buffer.
     */
    public void readData(DataInput din) throws IOException {

        m_Reference = din.readUnsignedShort();
        m_BitCount = din.readUnsignedShort();
    }

    public byte[] getMessage() {
        byte results[] = new byte[4];

        results[0] = (byte)((m_Reference >> 8) & 0xff);
        results[1] = (byte)(m_Reference & 0xff);
        results[2] = (byte)((m_BitCount >> 8) & 0xff);
        results[3] = (byte)(m_BitCount & 0xff);

        return results;
    }
}
