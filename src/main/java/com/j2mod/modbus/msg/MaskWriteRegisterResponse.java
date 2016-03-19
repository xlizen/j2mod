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
 * along with j2mod.  If not, see <http://www.gnu.org/licenses
 */
package com.j2mod.modbus.msg;

import com.j2mod.modbus.Modbus;

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
public final class MaskWriteRegisterResponse
        extends ModbusResponse {

    /*
     * Message fields.
     */
    private int m_Reference;
    private int m_AndMask;
    private int m_OrMask;

    /**
     * Constructs a new <tt>ReportSlaveIDResponse</tt>
     * instance.
     */
    public MaskWriteRegisterResponse() {
        super();
        setFunctionCode(Modbus.MASK_WRITE_REGISTER);
    }

    /**
     * getReference -- return the reference field.
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * setReference -- set the reference field.
     */
    public void setReference(int ref) {
        m_Reference = ref;
    }

    /**
     * getAndMask -- return the AND mask value;
     * @return int
     */
    public int getAndMask() {
        return m_AndMask;
    }

    /**
     * setAndMask -- set AND mask
     */
    public void setAndMask(int mask) {
        m_AndMask = mask;
    }

    /**
     * getOrMask -- return the OR mask value;
     * @return int
     */
    public int getOrMask() {
        return m_OrMask;
    }

    /**
     * setOrMask -- set OR mask
     */
    public void setOrMask(int mask) {
        m_OrMask = mask;
    }

    /**
     * writeData -- output the completed Modbus message to dout
     */
    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    /**
     * readData -- input the Modbus message from din.  If there was a
     * 		header, such as for Modbus/TCP, it will have been read
     * 		already.
     */
    public void readData(DataInput din) throws IOException {
        m_Reference = din.readShort();
        m_AndMask = din.readShort();
        m_OrMask = din.readShort();
    }

    /**
     * getMessage -- format the message into a byte array.
     */
    public byte[] getMessage() {
        byte results[] = new byte[6];

        results[0] = (byte)(m_Reference >> 8);
        results[1] = (byte)(m_Reference & 0xFF);
        results[2] = (byte)(m_AndMask >> 8);
        results[3] = (byte)(m_AndMask & 0xFF);
        results[4] = (byte)(m_OrMask >> 8);
        results[5] = (byte)(m_OrMask & 0xFF);

        return results;
    }
}
