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
 * Class implementing a <tt>WriteCoilResponse</tt>. The implementation directly
 * correlates with the class 0 function <i>write coil (FC 5)</i>. It
 * encapsulates the corresponding response message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public final class WriteCoilResponse extends ModbusResponse {
    private boolean m_Coil = false;
    private int m_Reference;

    /**
     * Constructs a new <tt>WriteCoilResponse</tt> instance.
     */
    public WriteCoilResponse() {
        super();

        setFunctionCode(Modbus.WRITE_COIL);
        setDataLength(4);
    }

    /**
     * Constructs a new <tt>WriteCoilResponse</tt> instance.
     *
     * @param reference the offset were writing was started from.
     * @param b         the state of the coil; true set, false reset.
     */
    public WriteCoilResponse(int reference, boolean b) {
        super();

        setFunctionCode(Modbus.WRITE_COIL);
        setDataLength(4);

        setReference(reference);
        setCoil(b);
    }

    /**
     * Sets the state that has been returned in the raw response.
     *
     * @param b true if the coil should be set of false if it should be unset.
     */
    public void setCoil(boolean b) {
        m_Coil = b;
    }

    /**
     * Gets the state that has been returned in this <tt>WriteCoilRequest</tt>.
     *
     * @return true if the coil is set, false if unset.
     */
    public boolean getCoil() {
        return m_Coil;
    }

    /**
     * Returns the reference of the register of the coil that has been written
     * to with the request.
     * <p>
     *
     * @return the reference of the coil's register.
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * Sets the reference of the register of the coil that has been written to
     * with the request.
     * <p>
     *
     * @param ref the reference of the coil's register.
     */
    public void setReference(int ref) {
        m_Reference = ref;
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    public void readData(DataInput din) throws IOException {
        byte data[] = new byte[4];
        din.readFully(data);

        setReference(((data[0] << 8) | (data[1] & 0xff)));
        setCoil(data[2] == Modbus.COIL_ON);

        setDataLength(4);
    }

    public byte[] getMessage() {
        byte result[] = new byte[4];

        result[0] = (byte)((m_Reference >> 8) & 0xff);
        result[1] = (byte)(m_Reference & 0xff);
        if (m_Coil) {
            result[2] = Modbus.COIL_ON_BYTES[0];
            result[3] = Modbus.COIL_ON_BYTES[1];
        }
        else {
            result[2] = Modbus.COIL_OFF_BYTES[0];
            result[3] = Modbus.COIL_OFF_BYTES[1];
        }
        return result;
    }
}