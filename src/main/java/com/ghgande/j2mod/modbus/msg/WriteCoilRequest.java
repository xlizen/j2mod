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
import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.procimg.DigitalOut;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>WriteCoilRequest</tt>. The implementation directly
 * correlates with the class 0 function <i>write coil (FC 5)</i>. It
 * encapsulates the corresponding request message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public final class WriteCoilRequest extends ModbusRequest {

    // instance attributes
    private int m_Reference;
    private boolean m_Coil;

    /**
     * Constructs a new <tt>WriteCoilRequest</tt> instance.
     */
    public WriteCoilRequest() {
        super();

        setFunctionCode(Modbus.WRITE_COIL);
        setDataLength(4);
    }

    /**
     * Constructs a new <tt>WriteCoilRequest</tt> instance with a given
     * reference and state to be written.
     *
     * @param ref the reference number of the register to read from.
     * @param b   true if the coil should be set of false if it should be unset.
     */
    public WriteCoilRequest(int ref, boolean b) {
        super();

        setFunctionCode(Modbus.WRITE_COIL);
        setDataLength(4);

        setReference(ref);
        setCoil(b);
    }

    public ModbusResponse getResponse() {
        WriteCoilResponse response = new WriteCoilResponse();

        response.setHeadless(isHeadless());
        if (!isHeadless()) {
            response.setProtocolID(getProtocolID());
            response.setTransactionID(getTransactionID());
        }
        response.setFunctionCode(getFunctionCode());
        response.setUnitID(getUnitID());

        return response;
    }

    public ModbusResponse createResponse() {
        WriteCoilResponse response;
        DigitalOut dout;

        // 1. get process image
        ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
        // 2. get coil
        try {
            dout = procimg.getDigitalOut(getReference());
            // 3. set coil
            dout.set(getCoil());
        }
        catch (IllegalAddressException iaex) {
            return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = (WriteCoilResponse)getResponse();
        response.setReference(getReference());
        response.setCoil(getCoil());

        return response;
    }

    /**
     * Sets the reference of the register of the coil that should be written to
     * with this <tt>ReadCoilsRequest</tt>.
     * <p>
     *
     * @param ref the reference of the coil's register.
     */
    public void setReference(int ref) {
        m_Reference = ref;
    }

    /**
     * Returns the reference of the register of the coil that should be written
     * to with this <tt>ReadCoilsRequest</tt>.
     *
     * @return the reference of the coil's register.
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * Sets the state that should be written with this <tt>WriteCoilRequest</tt>.
     *
     * @param b true if the coil should be set of false if it should be unset.
     */
    public void setCoil(boolean b) {
        m_Coil = b;
    }

    /**
     * Returns the state that should be written with this
     * <tt>WriteCoilRequest</tt>.
     *
     * @return true if the coil should be set of false if it should be unset.
     */
    public boolean getCoil() {
        return m_Coil;
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.writeShort(m_Reference);

        if (m_Coil) {
            dout.write(Modbus.COIL_ON_BYTES, 0, 2);
        }
        else {
            dout.write(Modbus.COIL_OFF_BYTES, 0, 2);
        }
    }

    public void readData(DataInput din) throws IOException {
        m_Reference = din.readUnsignedShort();

        if (din.readByte() == Modbus.COIL_ON) {
            m_Coil = true;
        }
        else {
            m_Coil = false;
        }

		/*
         * discard the next byte.
		 */
        din.readByte();
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