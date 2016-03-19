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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>WriteSingleRegisterResponse</tt>.
 * The implementation directly correlates with the class 0
 * function <i>write single register (FC 6)</i>. It
 * encapsulates the corresponding response message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public final class WriteSingleRegisterResponse
        extends ModbusResponse {

    //instance attributes
    private int m_Reference;
    private int m_RegisterValue;

    /**
     * Constructs a new <tt>WriteSingleRegisterResponse</tt>
     * instance.
     */
    public WriteSingleRegisterResponse() {
        super();
        setDataLength(4);
        setFunctionCode(Modbus.WRITE_SINGLE_REGISTER);
    }

    /**
     * Constructs a new <tt>WriteSingleRegisterResponse</tt>
     * instance.
     *
     * @param reference the offset of the register written.
     * @param value     the value of the register.
     */
    public WriteSingleRegisterResponse(int reference, int value) {
        super();
        setReference(reference);
        setRegisterValue(value);
        setDataLength(4);
        setFunctionCode(Modbus.WRITE_SINGLE_REGISTER);
    }

    /**
     * Returns the value that has been returned in
     * this <tt>WriteSingleRegisterResponse</tt>.
     * <p>
     *
     * @return the value of the register.
     */
    public int getRegisterValue() {
        return m_RegisterValue;
    }

    /**
     * Sets the value that has been returned in the
     * response message.
     * <p>
     *
     * @param value the returned register value.
     */
    private void setRegisterValue(int value) {
        m_RegisterValue = value;
    }

    /**
     * Returns the reference of the register
     * that has been written to.
     * <p>
     *
     * @return the reference of the written register.
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * Sets the reference of the register that has
     * been written to.
     * <p>
     *
     * @param ref the reference of the written register.
     */
    private void setReference(int ref) {
        m_Reference = ref;
        //setChanged(true);
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    public void readData(DataInput din) throws IOException {
        setReference(din.readUnsignedShort());
        setRegisterValue(din.readUnsignedShort());
        //update data length
        setDataLength(4);
    }

    public byte[] getMessage() {
        byte result[] = new byte[4];

        result[0] = (byte)((m_Reference >> 8) & 0xff);
        result[1] = (byte)(m_Reference & 0xff);
        result[2] = (byte)((m_RegisterValue >> 8) & 0xff);
        result[3] = (byte)(m_RegisterValue & 0xff);

        return result;
    }

}