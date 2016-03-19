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

import com.j2mod.modbus.Modbus;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>ReadCommEventCounterResponse</tt>.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 *
 * @version @version@ (@date@)
 */
public final class ReadExceptionStatusResponse extends ModbusResponse {

    /*
     * Message fields.
     */
    private int m_Status;

    /**
     * Constructs a new <tt>ReadExceptionStatusResponse</tt> instance.
     */
    public ReadExceptionStatusResponse() {
        super();

        setFunctionCode(Modbus.READ_EXCEPTION_STATUS);
        setDataLength(1);
    }

    /**
     * getStatus -- get the device's status.
     *
     * @return int
     */
    public int getStatus() {
        return m_Status;
    }

    /**
     * setStatus -- set the device's status.
     *
     * @param status Status to set
     */
    public void setStatus(int status) {
        m_Status = status;
    }

    /**
     * writeData -- output the completed Modbus message to dout
     */
    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    /**
     * readData -- input the Modbus message from din. If there was a header,
     * such as for Modbus/TCP, it will have been read already.
     */
    public void readData(DataInput din) throws IOException {
        m_Status = din.readByte() & 0xFF;
    }

    /**
     * getMessage -- format the message into a byte array.
     */
    public byte[] getMessage() {
        byte result[] = new byte[1];

        result[0] = (byte)(m_Status & 0xFF);

        return result;
    }
}
