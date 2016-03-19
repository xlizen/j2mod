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
 * Class implementing a <tt>ReadCommEventCounterResponse</tt>.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 *
 * @version @version@ (@date@)
 */
public final class ReadCommEventCounterResponse extends ModbusResponse {

    /*
     * Message fields.
     */
    private int m_Status;
    private int m_Events;

    /**
     * Constructs a new <tt>ReportSlaveIDResponse</tt> instance.
     */
    public ReadCommEventCounterResponse() {
        super();

        setFunctionCode(Modbus.READ_COMM_EVENT_COUNTER);
        setDataLength(4);
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
     * @param status int
     */
    public void setStatus(int status) {
        if (status != 0 && status != 0xFFFF) {
            throw new IllegalArgumentException("Illegal status value: " + status);
        }

        m_Status = status;
    }

    /**
     * getEvents -- get device's event counter.
     */
    public int getEventCount() {
        return m_Events;
    }

    /**
     * setEvents -- set the device's event counter.
     */
    public void setEventCount(int count) {
        m_Events = count;
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
        m_Status = din.readShort();
        m_Events = din.readShort();
    }

    /**
     * getMessage -- format the message into a byte array.
     */
    public byte[] getMessage() {
        byte result[] = new byte[4];

        result[0] = (byte)(m_Status >> 8);
        result[1] = (byte)(m_Status & 0xFF);
        result[2] = (byte)(m_Events >> 8);
        result[3] = (byte)(m_Events & 0xFF);

        return result;
    }
}
