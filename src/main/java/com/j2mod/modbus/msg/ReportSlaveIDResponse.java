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
public final class ReportSlaveIDResponse extends ModbusResponse {

    /*
     * Message fields.
     */
    int m_length;
    byte m_data[];
    int m_status;
    int m_slaveId;

    /**
     * Constructs a new <tt>ReportSlaveIDResponse</tt>
     * instance.
     */
    public ReportSlaveIDResponse() {
        super();
        setFunctionCode(Modbus.REPORT_SLAVE_ID);
    }

    /**
     * getSlaveID -- return the slave identifier field.
     */
    public int getSlaveID() {
        return m_slaveId;
    }

    /**
     * setSlaveID -- initialize the slave identifier when constructing
     * 		a response message.
     */
    public void setSlaveID(int i) {
        m_slaveId = i;
    }

    /**
     * getStatus -- get the slave's "run" status.
     * @return boolean
     */
    public boolean getStatus() {
        return m_status != 0;
    }

    /**
     * setStatus -- initialize the slave's "run" status when constructing
     * 		a response message.
     *
     * @param b
     */
    public void setStatus(boolean b) {
        m_status = b ? 0xff : 0x00;
    }

    /**
     * getData -- get the device-depending data for the slave.
     *
     * @return byte array
     */
    public byte[] getData() {
        byte[] result = new byte[m_length - 2];
        System.arraycopy(m_data, 0, result, 0, m_length - 2);

        return result;
    }

    /**
     * setData -- initialize the slave's device dependent data when
     * 		initializing a response.
     * @param data byte array
     */
    public void setData(byte[] data) {
        /*
         * There are always two bytes of payload in the message -- the
		 * slave ID and the run status indicator.
		 */
        if (data == null) {
            m_length = 2;
            m_data = new byte[0];

            return;
        }

        if (data.length > 249) {
            throw new IllegalArgumentException("data length limit exceeded");
        }

        m_length = data.length + 2;

        m_data = new byte[data.length];
        System.arraycopy(data, 0, m_data, 0, data.length);
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

		/*
         * Get the size of any device-specific data.
		 */
        m_length = din.readUnsignedByte();
        if (m_length < 2 || m_length > 255) {
            return;
        }

		/*
		 * Get the run status and device identifier.
		 */
        m_slaveId = din.readUnsignedByte();
        m_status = din.readUnsignedByte();

		/*
		 * The device-specific data is two bytes shorter than the
		 * length read previously.  That length includes the run status
		 * and slave ID.
		 */
        m_data = new byte[m_length - 2];
        if (m_length > 2) {
            din.readFully(m_data, 0, m_length - 2);
        }
    }

    /**
     * getMessage -- format the message into a byte array.
     */
    public byte[] getMessage() {
        byte result[] = new byte[3 + m_length];
        int offset = 0;

        result[offset++] = (byte)(m_length + 2);
        result[offset++] = (byte)m_slaveId;
        result[offset++] = (byte)m_status;
        if (m_length > 0) {
            System.arraycopy(m_data, 0, result, offset, m_length - 2);
        }

        return result;
    }
}
