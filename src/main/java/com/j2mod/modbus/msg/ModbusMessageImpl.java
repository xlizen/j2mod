/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.j2mod.modbus.msg;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.util.ModbusUtil;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Abstract class implementing a <tt>ModbusMessage</tt>. This class provides
 * specialised implementations with the functionality they have in common.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public abstract class ModbusMessageImpl implements ModbusMessage {

    // instance attributes
    private int m_TransactionID = Modbus.DEFAULT_TRANSACTION_ID;
    private int m_ProtocolID = Modbus.DEFAULT_PROTOCOL_ID;
    private int m_DataLength;
    private int m_UnitID = Modbus.DEFAULT_UNIT_ID;
    private int m_FunctionCode;
    private boolean m_Headless = false; // flag for headerless (serial)
    // transport

    /*** Header ******************************************/

    /**
     * Tests if this message instance is headless.
     *
     * @return true if headless, false otherwise.
     */
    public boolean isHeadless() {
        return m_Headless;
    }

    public void setHeadless() {
        m_Headless = true;
    }

    public int getTransactionID() {
        return m_TransactionID & 0x0000FFFF;
    }

    /**
     * Sets the transaction identifier of this <tt>ModbusMessage</tt>.
     *
     * <p>
     * The identifier must be a 2-byte (short) non negative integer value valid
     * in the range of 0-65535.<br>
     *
     * @param tid the transaction identifier as <tt>int</tt>.
     */
    public void setTransactionID(int tid) {
        m_TransactionID = tid & 0x0000FFFF;
    }

    public int getProtocolID() {
        return m_ProtocolID;
    }

    /**
     * Sets the protocol identifier of this <tt>ModbusMessage</tt>.
     * <p>
     * The identifier should be a 2-byte (short) non negative integer value
     * valid in the range of 0-65535.<br>
     * <p>
     *
     * @param pid the protocol identifier as <tt>int</tt>.
     */
    public void setProtocolID(int pid) {
        m_ProtocolID = pid;
    }

    public int getDataLength() {
        return m_DataLength;
    }

    /**
     * Sets the length of the data appended after the protocol header.
     *
     * <p>
     * Note that this library, a bit in contrast to the specification, counts
     * the unit identifier and the function code to the header, because it is
     * part of each and every message. Thus this method will add two (2) to the
     * passed in integer value.
     *
     * <p>
     * This method does not include the length of a final CRC/LRC for those
     * protocols which requirement.
     *
     * @param length the data length as <tt>int</tt>.
     */
    public void setDataLength(int length) {
        if (length < 0 || length + 2 > 255) {
            throw new IllegalArgumentException("Invalid length: " + length);
        }

        m_DataLength = length + 2;
    }

    public int getUnitID() {
        return m_UnitID;
    }

    /**
     * Sets the unit identifier of this <tt>ModbusMessage</tt>.<br>
     * The identifier should be a 1-byte non negative integer value valid in the
     * range of 0-255.
     *
     * @param num the unit identifier number to be set.
     */
    public void setUnitID(int num) {
        m_UnitID = num;
    }

    public int getFunctionCode() {
        return m_FunctionCode;
    }

    /**
     * Sets the function code of this <tt>ModbusMessage</tt>.<br>
     * The function code should be a 1-byte non negative integer value valid in
     * the range of 0-127.<br>
     * Function codes are ordered in conformance classes their values are
     * specified in <tt>com.j2mod.modbus.Modbus</tt>.
     *
     * @param code the code of the function to be set.
     *
     * @see com.j2mod.modbus.Modbus
     */
    protected void setFunctionCode(int code) {
        m_FunctionCode = code;
    }

    /**
     * Returns the this message as hexadecimal string.
     *
     * @return the message as hex encoded string.
     */
    public String getHexMessage() {
        return ModbusUtil.toHex(this);
    }

    /**
     * Sets the headless flag of this message.
     *
     * @param b true if headless, false otherwise.
     */
    public void setHeadless(boolean b) {
        m_Headless = b;
    }

    /**
     * Writes the subclass specific data to the given DataOutput.
     *
     * @param dout the DataOutput to be written to.
     *
     * @throws IOException if an I/O related error occurs.
     */
    public abstract void writeData(DataOutput dout) throws IOException;

    /**
     * Reads the subclass specific data from the given DataInput instance.
     *
     * @param din the DataInput to read from.
     *
     * @throws IOException if an I/O related error occurs.
     */
    public abstract void readData(DataInput din) throws IOException;

    /**
     * getOutputLength -- Return the actual packet size in bytes
     *
     * The actual packet size, plus any CRC or header, will be returned.
     */
    public int getOutputLength() {
        int l = 2 + getDataLength();
        if (!isHeadless()) {
            l = l + 4;
        }
        return l;
    }

    /**
     * Writes this message to the given <tt>DataOutput</tt>.
     *
     * <p>
     * This method must be overridden for any message type which doesn't follow
     * this simple structure.
     *
     * @param dout a <tt>DataOutput</tt> instance.
     *
     * @throws IOException if an I/O related error occurs.
     */
    public void writeTo(DataOutput dout) throws IOException {

        if (!isHeadless()) {
            dout.writeShort(getTransactionID());
            dout.writeShort(getProtocolID());
            dout.writeShort(getDataLength());
        }
        dout.writeByte(getUnitID());
        dout.writeByte(getFunctionCode());

        writeData(dout);
    }

    /*** END Transportable *******************************/

    /**
     * readFrom -- Read the headers and data for a message.  The sub-classes
     * readData() method will then read in the rest of the message.
     *
     * @param din -- Input source
     */
    public void readFrom(DataInput din) throws IOException {
        if (!isHeadless()) {
            setTransactionID(din.readUnsignedShort());
            setProtocolID(din.readUnsignedShort());
            m_DataLength = din.readUnsignedShort();
        }
        setUnitID(din.readUnsignedByte());
        setFunctionCode(din.readUnsignedByte());
        readData(din);
    }

}
