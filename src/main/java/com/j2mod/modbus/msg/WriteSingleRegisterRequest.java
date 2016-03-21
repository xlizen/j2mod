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
import com.j2mod.modbus.procimg.SimpleRegister;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>WriteSingleRegisterRequest</tt>. The implementation
 * directly correlates with the class 0 function <i>write single register (FC
 * 6)</i>. It encapsulates the corresponding request message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public final class WriteSingleRegisterRequest extends ModbusRequest {

    // instance attributes
    private int m_Reference;
    private Register m_Register;

    /**
     * Constructs a new <tt>WriteSingleRegisterRequest</tt> instance.
     */
    public WriteSingleRegisterRequest() {
        super();

        setFunctionCode(Modbus.WRITE_SINGLE_REGISTER);
        setDataLength(4);
    }

    /**
     * Constructs a new <tt>WriteSingleRegisterRequest</tt> instance with a
     * given reference and value to be written.
     *
     * @param ref the reference number of the register to read from.
     * @param reg the register containing the data to be written.
     */
    public WriteSingleRegisterRequest(int ref, Register reg) {
        super();

        setFunctionCode(Modbus.WRITE_SINGLE_REGISTER);
        setDataLength(4);

        m_Reference = ref;
        m_Register = reg;
    }

    public ModbusResponse getResponse() {
        WriteSingleRegisterResponse response = new WriteSingleRegisterResponse();

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
        WriteSingleRegisterResponse response;
        Register reg;

        // 1. get process image
        ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
        // 2. get register
        try {
            reg = procimg.getRegister(m_Reference);
            // 3. set Register
            reg.setValue(m_Register.toBytes());
        }
        catch (IllegalAddressException iaex) {
            return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = new WriteSingleRegisterResponse(this.getReference(), reg.getValue());
        // transfer header data
        if (!isHeadless()) {
            response.setTransactionID(this.getTransactionID());
            response.setProtocolID(this.getProtocolID());
        }
        else {
            response.setHeadless();
        }
        response.setUnitID(this.getUnitID());
        response.setFunctionCode(this.getFunctionCode());

        return response;
    }

    /**
     * Returns the reference of the register to be written to with this
     * <tt>WriteSingleRegisterRequest</tt>.
     *
     * @return the reference of the register to be written to.
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * Sets the reference of the register to be written to with this
     * <tt>WriteSingleRegisterRequest</tt>.
     *
     * @param ref the reference of the register to be written to.
     */
    public void setReference(int ref) {
        m_Reference = ref;
    }

    /**
     * Returns the register to be written with this
     * <tt>WriteSingleRegisterRequest</tt>.
     *
     * @return the value to be written to the register.
     */
    public Register getRegister() {
        return m_Register;
    }

    /**
     * Sets the value that should be written to the register with this
     * <tt>WriteSingleRegisterRequest</tt>.
     *
     * @param reg the register to be written.
     */
    public void setRegister(Register reg) {
        m_Register = reg;
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.writeShort(m_Reference);
        dout.write(m_Register.toBytes());
    }

    public void readData(DataInput din) throws IOException {
        m_Reference = din.readUnsignedShort();
        m_Register = new SimpleRegister(din.readByte(), din.readByte());
    }

    public byte[] getMessage() {
        byte result[] = new byte[4];

        result[0] = (byte)((m_Reference >> 8) & 0xff);
        result[1] = (byte)(m_Reference & 0xff);
        result[2] = (byte)((m_Register.getValue() >> 8) & 0xff);
        result[3] = (byte)(m_Register.getValue() & 0xff);

        return result;
    }
}