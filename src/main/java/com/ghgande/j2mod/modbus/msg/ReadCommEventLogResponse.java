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
 * Class implementing a <tt>ReadCommEventCounterResponse</tt>.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 *
 * @version @version@ (@date@)
 */
public final class ReadCommEventLogResponse extends ModbusResponse {

    /*
     * Message fields.
     */
    private int m_ByteCount;
    private int m_Status;
    private int m_EventCount;
    private int m_MessageCount;
    private byte[] m_Events;

    /**
     * Constructs a new <tt>ReadCommEventLogResponse</tt> instance.
     */
    public ReadCommEventLogResponse() {
        super();

        setFunctionCode(Modbus.READ_COMM_EVENT_LOG);
        setDataLength(7);
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
     * getEvents -- get device's event counter.
     */
    public int getEventCount() {
        return m_EventCount;
    }

    /**
     * setEventCount -- set the device's event counter.
     */
    public void setEventCount(int count) {
        m_EventCount = count;
    }

    /**
     * getMessageCount -- get device's message counter.
     */
    public int getMessageCount() {
        return m_MessageCount;
    }

    /**
     * setMessageCount -- set device's message counter.
     */
    public void setMessageCount(int count) {
        m_MessageCount = count;
    }

    /**
     * getEvent -- get an event from the event log.
     */
    public int getEvent(int index) {
        if (m_Events == null || index < 0 || index >= m_Events.length) {
            throw new IndexOutOfBoundsException("index = " + index + ", limit = " + (m_Events == null ? "null" : m_Events.length));
        }

        return m_Events[index] & 0xFF;
    }

    public byte[] getEvents() {
        if (m_Events == null) {
            return null;
        }

        byte[] result = new byte[m_Events.length];
        System.arraycopy(m_Events, 0, result, 0, m_Events.length);

        return result;
    }

    public void setEvents(int count) {
        if (count < 0 || count > 64) {
            throw new IllegalArgumentException("invalid event list size (0 <= count <= 64)");
        }

        m_Events = new byte[count];
    }

    /**
     * setEvent -- store an event number in the event log
     */
    public void setEvent(int index, int event) {
        if (m_Events == null || index < 0 || index >= m_Events.length) {
            throw new IndexOutOfBoundsException("index = " + index + ", limit = " + (m_Events == null ? "null" : m_Events.length));
        }

        m_Events[index] = (byte)event;
    }

    public void setEvents(byte[] events) {
        if (events.length > 64) {
            throw new IllegalArgumentException("events list too big (> 64 bytes)");
        }

        m_Events = new byte[events.length];
        if (m_Events.length > 0) {
            System.arraycopy(events, 0, m_Events, 0, events.length);
        }
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
        m_ByteCount = din.readByte();
        m_Status = din.readShort();
        m_EventCount = din.readShort();
        m_MessageCount = din.readShort();

        m_Events = new byte[m_ByteCount - 6];

        if (m_Events.length > 0) {
            din.readFully(m_Events, 0, m_Events.length);
        }
    }

    /**
     * getMessage -- format the message into a byte array.
     */
    public byte[] getMessage() {
        byte result[] = new byte[m_Events.length + 7];

        result[0] = (byte)(m_ByteCount = m_Events.length + 6);
        result[1] = (byte)(m_Status >> 8);
        result[2] = (byte)(m_Status & 0xFF);
        result[3] = (byte)(m_EventCount >> 8);
        result[4] = (byte)(m_EventCount & 0xFF);
        result[5] = (byte)(m_MessageCount >> 8);
        result[6] = (byte)(m_MessageCount & 0xFF);

        System.arraycopy(m_Events, 0, result, 7, m_Events.length);

        return result;
    }
}
