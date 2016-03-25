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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>ReadMEIResponse</tt>.
 *
 * Derived from similar class for Read Coils response.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
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
     *
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
     *
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
     * header, such as for Modbus/TCP, it will have been read
     * already.
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
