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
import com.j2mod.modbus.ModbusCoupler;
import com.j2mod.modbus.procimg.IllegalAddressException;
import com.j2mod.modbus.procimg.ProcessImage;
import com.j2mod.modbus.procimg.Register;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>ReadMultipleRegistersRequest</tt>. The
 * implementation directly correlates with the class 0 function <i>read multiple
 * registers (FC 3)</i>. It encapsulates the corresponding request message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public final class ReadMultipleRegistersRequest extends ModbusRequest {

    // instance attributes
    private int m_Reference;
    private int m_WordCount;

    /**
     * Constructs a new <tt>ReadMultipleRegistersRequest</tt> instance.
     */
    public ReadMultipleRegistersRequest() {
        super();

        setFunctionCode(Modbus.READ_MULTIPLE_REGISTERS);
        setDataLength(4);
    }

    /**
     * Constructs a new <tt>ReadMultipleRegistersRequest</tt> instance with a
     * given reference and count of words to be read.  This message reads
     * from holding (r/w) registers.
     *
     * @param ref   the reference number of the register to read from.
     * @param count the number of words to be read.
     *
     * @see ReadInputRegistersRequest
     */
    public ReadMultipleRegistersRequest(int ref, int count) {
        super();

        setFunctionCode(Modbus.READ_MULTIPLE_REGISTERS);
        setDataLength(4);

        setReference(ref);
        setWordCount(count);
    }

    public ModbusResponse getResponse() {
        ReadMultipleRegistersResponse response;

        response = new ReadMultipleRegistersResponse();

        response.setUnitID(getUnitID());
        response.setHeadless(isHeadless());
        if (!isHeadless()) {
            response.setProtocolID(getProtocolID());
            response.setTransactionID(getTransactionID());
        }
        return response;
    }

    public ModbusResponse createResponse() {
        ReadMultipleRegistersResponse response;
        Register[] regs;

        // 1. get process image
        ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
        // 2. get input registers range
        try {
            regs = procimg.getRegisterRange(getReference(), getWordCount());
        }
        catch (IllegalAddressException e) {
            return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = (ReadMultipleRegistersResponse)getResponse();
        response.setRegisters(regs);

        return response;
    }

    /**
     * Returns the reference of the register to to start reading from with this
     * <tt>ReadMultipleRegistersRequest</tt>.
     * <p>
     *
     * @return the reference of the register to start reading from as
     * <tt>int</tt>.
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * Sets the reference of the register to start reading from with this
     * <tt>ReadMultipleRegistersRequest</tt>.
     * <p>
     *
     * @param ref the reference of the register to start reading from.
     */
    public void setReference(int ref) {
        m_Reference = ref;
    }

    /**
     * Returns the number of words to be read with this
     * <tt>ReadMultipleRegistersRequest</tt>.
     * <p>
     *
     * @return the number of words to be read as <tt>int</tt>.
     */
    public int getWordCount() {
        return m_WordCount;
    }

    /**
     * Sets the number of words to be read with this
     * <tt>ReadMultipleRegistersRequest</tt>.
     * <p>
     *
     * @param count the number of words to be read.
     */
    public void setWordCount(int count) {
        m_WordCount = count;
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.writeShort(m_Reference);
        dout.writeShort(m_WordCount);
    }

    public void readData(DataInput din) throws IOException {
        m_Reference = din.readUnsignedShort();
        m_WordCount = din.readUnsignedShort();
    }

    public byte[] getMessage() {
        byte result[] = new byte[4];

        result[0] = (byte)((m_Reference >> 8) & 0xff);
        result[1] = (byte)(m_Reference & 0xff);
        result[2] = (byte)((m_WordCount >> 8) & 0xff);
        result[3] = (byte)(m_WordCount & 0xff);

        return result;
    }
}
