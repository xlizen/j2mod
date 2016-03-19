/*
 * This file is part of j2mod.
 *
 * j2mod is a fork of the jamod library written by Dieter Wimberger
 * and then further enhanced by Julie Haugh with a new LGPL license
 * and upgraded to Java 1.6
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
import com.ghgande.j2mod.modbus.util.BitVector;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>ReadInputDiscretesResponse</tt>.
 * The implementation directly correlates with the class 1
 * function <i>read input discretes (FC 2)</i>. It encapsulates
 * the corresponding response message.
 * <p>
 * Input Discretes are understood as bits that cannot be
 * manipulated (i.e. set or unset).
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public final class ReadInputDiscretesResponse
        extends ModbusResponse {

    //instance attributes
    private int m_BitCount;
    private BitVector m_Discretes;

    /**
     * Constructs a new <tt>ReadInputDiscretesResponse</tt>
     * instance.
     */
    public ReadInputDiscretesResponse() {
        super();
        setFunctionCode(Modbus.READ_INPUT_DISCRETES);
    }

    /**
     * Constructs a new <tt>ReadInputDiscretesResponse</tt>
     * instance with a given count of input discretes
     * (i.e. bits).
     * <b>
     *
     * @param count the number of bits to be read.
     */
    public ReadInputDiscretesResponse(int count) {
        super();
        setFunctionCode(Modbus.READ_INPUT_DISCRETES);
        setBitCount(count);
    }

    /**
     * Returns the number of bits (i.e. input discretes)
     * read with the request.
     * <p>
     *
     * @return the number of bits that have been read.
     */
    public int getBitCount() {
        return m_BitCount;
    }

    /**
     * Sets the number of bits in this response.
     *
     * @param count the number of response bits as int.
     */
    public void setBitCount(int count) {
        m_BitCount = count;
        m_Discretes = new BitVector(count);
        //set correct length, without counting unitid and fc
        setDataLength(m_Discretes.byteSize() + 1);
    }

    /**
     * Returns the <tt>BitVector</tt> that stores
     * the collection of bits that have been read.
     * <p>
     *
     * @return the <tt>BitVector</tt> holding the
     * bits that have been read.
     */
    public BitVector getDiscretes() {
        return m_Discretes;
    }

    /**
     * Convenience method that returns the state
     * of the bit at the given index.
     * <p>
     *
     * @param index the index of the input discrete
     *              for which the status should be returned.
     *
     * @return true if set, false otherwise.
     *
     * @throws IndexOutOfBoundsException if the
     *                                   index is out of bounds
     */
    public boolean getDiscreteStatus(int index) throws IndexOutOfBoundsException {

        return m_Discretes.getBit(index);
    }

    /**
     * Sets the status of the given input discrete.
     *
     * @param index the index of the input discrete to be set.
     * @param b     true if to be set, false if to be reset.
     *
     * @throws IndexOutOfBoundsException if the given index exceeds bounds.
     */
    public void setDiscreteStatus(int index, boolean b) throws IndexOutOfBoundsException {
        m_Discretes.setBit(index, b);
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_Discretes.byteSize());
        dout.write(m_Discretes.getBytes(), 0, m_Discretes.byteSize());
    }

    public void readData(DataInput din) throws IOException {

        int count = din.readUnsignedByte();
        byte[] data = new byte[count];
        for (int k = 0; k < count; k++) {
            data[k] = din.readByte();
        }

        //decode bytes into bitvector
        m_Discretes = BitVector.createBitVector(data);

        //update data length
        setDataLength(count + 1);
    }

    public byte[] getMessage() {
        byte result[];
        int len = 1 + m_Discretes.byteSize();

        result = new byte[len];
        result[0] = (byte)m_Discretes.byteSize();
        System.arraycopy(m_Discretes.getBytes(), 0, result, 1, m_Discretes.byteSize());

        return result;
    }

}