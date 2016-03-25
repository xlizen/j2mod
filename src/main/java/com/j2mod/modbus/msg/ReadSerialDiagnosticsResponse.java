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
 * Class implementing a <tt>ReadSerialDiagnosticsResponse</tt>.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public final class ReadSerialDiagnosticsResponse extends ModbusResponse {

    /*
     * Message fields.
     */
    private int m_Function;
    private short m_Data;

    /**
     * Constructs a new <tt>Diagnostics</tt> response
     * instance.
     */
    public ReadSerialDiagnosticsResponse() {
        super();

        setFunctionCode(Modbus.READ_SERIAL_DIAGNOSTICS);
        setDataLength(4);
    }

    /**
     * getFunction -- Get the DIAGNOSTICS sub-function.
     *
     * @return Function code
     */
    public int getFunction() {
        return m_Function;
    }

    /**
     * setFunction - Set the DIAGNOSTICS sub-function.
     *
     * @param function - DIAGNOSTICS command sub-function.
     */
    public void setFunction(int function) {
        m_Function = function;
        m_Data = 0;
    }

    /**
     * getWordCount -- get the number of words in m_Data.
     */
    public int getWordCount() {
        return 1;
    }

    /**
     * getData -- return the first data item.
     */
    public int getData() {
        return m_Data;
    }

    /**
     * setData -- Set the optional data value
     */
    public void setData(int value) {
        m_Data = (short)value;
    }

    /**
     * getData -- Get the data item at the index.
     *
     * @param index - Unused, must be 0.
     *
     * @deprecated
     */
    public int getData(int index) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }

        return m_Data;
    }

    /**
     * setData -- Set the data item at the index
     *
     * @param index - Unused, must be 0.
     * @param value - Optional data value for function.
     *
     * @deprecated
     */
    public void setData(int index, int value) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }

        m_Data = (short)value;
    }

    /**
     * writeData -- output the completed Modbus message to dout
     */
    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    /**
     * readData -- Read the function code and data value
     */
    public void readData(DataInput din) throws IOException {
        m_Function = din.readShort() & 0xFFFF;
        m_Data = (short)(din.readShort() & 0xFFFF);
    }

    /**
     * getMessage -- Create the DIAGNOSTICS message paylaod.
     */
    public byte[] getMessage() {
        byte result[] = new byte[4];

        result[0] = (byte)(m_Function >> 8);
        result[1] = (byte)(m_Function & 0xFF);
        result[2] = (byte)(m_Data >> 8);
        result[3] = (byte)(m_Data & 0xFF);

        return result;
    }
}
