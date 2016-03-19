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
package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.io.NonWordDataHandler;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>WriteMultipleRegistersRequest</tt>. The
 * implementation directly correlates with the class 0 function <i>write
 * multiple registers (FC 16)</i>. It encapsulates the corresponding request
 * message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author jfhaugh
 * @version 1.05
 *
 *          20140426 - Refactor and minor bug fix.
 */
public final class WriteMultipleRegistersRequest extends ModbusRequest {
    private int m_Reference;
    private Register[] m_Registers;
    private NonWordDataHandler m_NonWordDataHandler = null;

    /**
     * Constructs a new <tt>WriteMultipleRegistersRequest</tt> instance with a
     * given starting reference and values to be written.
     * <p>
     *
     * @param first
     *            -- the address of the first register to write to.
     * @param registers
     *            -- the registers to be written.
     */
    public WriteMultipleRegistersRequest(int first, Register[] registers) {
        setFunctionCode(Modbus.WRITE_MULTIPLE_REGISTERS);

        setReference(first);
        setRegisters(registers);
    }

    /**
     * Constructs a new <tt>WriteMultipleRegistersRequest</tt> instance.
     */
    public WriteMultipleRegistersRequest() {
        setFunctionCode(Modbus.WRITE_MULTIPLE_REGISTERS);
    }

    public ModbusResponse getResponse() {
        WriteMultipleRegistersResponse response = new WriteMultipleRegistersResponse();

        response.setHeadless(isHeadless());
        if (!isHeadless()) {
            response.setProtocolID(getProtocolID());
            response.setTransactionID(getTransactionID());
        }
        response.setFunctionCode(getFunctionCode());
        response.setUnitID(getUnitID());

        return response;
    }

    /**
     * createResponse - Returns the <tt>WriteMultipleRegistersResponse</tt> that
     * represents the answer to this <tt>WriteMultipleRegistersRequest</tt>.
     *
     * The implementation should take care about assembling the reply to this
     * <tt>WriteMultipleRegistersRequest</tt>.
     *
     * This method is used to create responses from the process image associated
     * with the <tt>ModbusCoupler</tt>. It is commonly used to implement Modbus
     * slave instances.
     *
     * @return the corresponding ModbusResponse.
     *          <p>
     *
     *          createResponse() must be able to handle the case where the word
     *          data that is in the response is actually non-word data. That is,
     *          where the slave device has data which are not actually
     *          <tt>short</tt> values in the range of registers being processed.
     */
    public ModbusResponse createResponse() {
        WriteMultipleRegistersResponse response;

        if (m_NonWordDataHandler == null) {
            Register[] regs;
            // 1. get process image
            ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
            // 2. get registers
            try {
                regs = procimg.getRegisterRange(getReference(), getWordCount());
                // 3. set Register values
                for (int i = 0; i < regs.length; i++) {
                    regs[i].setValue(this.getRegister(i).getValue());
                }
            }
            catch (IllegalAddressException iaex) {
                return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
            }
            response = (WriteMultipleRegistersResponse)getResponse();

            response.setReference(getReference());
            response.setWordCount(getWordCount());
        }
        else {
            int result = m_NonWordDataHandler.commitUpdate();
            if (result > 0) {
                return createExceptionResponse(result);
            }

            response = (WriteMultipleRegistersResponse)getResponse();

            response.setReference(getReference());
            response.setWordCount(m_NonWordDataHandler.getWordCount());
        }

        return response;
    }

    /**
     * setReference - Returns the reference of the register to start writing to
     * with this <tt>WriteMultipleRegistersRequest</tt>.
     * <p>
     *
     * @return the reference of the register to start writing to as <tt>int</tt>
     *         .
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * setReference - Sets the reference of the register to write to with this
     * <tt>WriteMultipleRegistersRequest</tt>.
     * <p>
     *
     * @param ref
     *            the reference of the register to start writing to as an
     *            <tt>int</tt>.
     */
    public void setReference(int ref) {
        m_Reference = ref;
    }

    /**
     * getRegisters - Returns the registers to be written with this
     * <tt>WriteMultipleRegistersRequest</tt>.
     * <p>
     *
     * @return the registers to be written as <tt>Register[]</tt>.
     */
    public synchronized Register[] getRegisters() {
        Register[] dest = new Register[m_Registers.length];
        System.arraycopy(m_Registers, 0, dest, 0, dest.length);
        return dest;
    }

    /**
     * setRegisters - Sets the registers to be written with this
     * <tt>WriteMultipleRegistersRequest</tt>.
     * <p>
     *
     * @param registers
     *            the registers to be written as <tt>Register[]</tt>.
     */
    public void setRegisters(Register[] registers) {
        m_Registers = registers;
    }

    /**
     * getRegister - Returns the <tt>Register</tt> at the given position.
     *
     * @param index
     *            the relative index of the <tt>Register</tt>.
     *
     * @return the register as <tt>Register</tt>.
     *
     * @throws IndexOutOfBoundsException
     *             if the index is out of bounds.
     */
    public Register getRegister(int index) throws IndexOutOfBoundsException {
        if (index < 0) {
            throw new IndexOutOfBoundsException(index + " < 0");
        }

        if (index >= getWordCount()) {
            throw new IndexOutOfBoundsException(index + " > " + getWordCount());
        }

        return m_Registers[index];
    }

    /**
     * getRegisterValue - Returns the value of the specified register.
     * <p>
     *
     * @param index
     *            the index of the desired register.
     *
     * @return the value as an <tt>int</tt>.
     *
     * @throws IndexOutOfBoundsException
     *             if the index is out of bounds.
     */
    public int getRegisterValue(int index) throws IndexOutOfBoundsException {
        return getRegister(index).toUnsignedShort();
    }

    /**
     * getByteCount - Returns the number of bytes representing the values to be
     * written.
     * <p>
     *
     * @return the number of bytes to be written as <tt>int</tt>.
     */
    public int getByteCount() {
        return getWordCount() * 2;
    }

    /**
     * getWordCount - Returns the number of words to be written.
     *
     * @return the number of words to be written as <tt>int</tt>.
     */
    public int getWordCount() {
        if (m_Registers == null) {
            return 0;
        }

        return m_Registers.length;
    }

    /**
     * getNonWordDataHandler - Returns the actual non word data handler.
     *
     * @return the actual <tt>NonWordDataHandler</tt>.
     */
    public NonWordDataHandler getNonWordDataHandler() {
        return m_NonWordDataHandler;
    }

    /**
     * setNonWordHandler - Sets a non word data handler. A non-word data handler
     * is responsible for converting words from a Modbus packet into the
     * non-word values associated with the actual device's registers.
     *
     * @param dhandler
     *            a <tt>NonWordDataHandler</tt> instance.
     */
    public void setNonWordDataHandler(NonWordDataHandler dhandler) {
        m_NonWordDataHandler = dhandler;
    }

    public void writeData(DataOutput output) throws IOException {
        output.write(getMessage());
    }

    public void readData(DataInput input) throws IOException {
        m_Reference = input.readShort();
        int registerCount = input.readUnsignedShort();
        int byteCount = input.readUnsignedByte();

        if (m_NonWordDataHandler == null) {
            byte buffer[] = new byte[byteCount];
            input.readFully(buffer, 0, byteCount);

            int offset = 0;
            m_Registers = new Register[registerCount];

            for (int register = 0; register < registerCount; register++) {
                m_Registers[register] = new SimpleRegister(buffer[offset], buffer[offset + 1]);
                offset += 2;
            }
        }
        else {
            m_NonWordDataHandler.readData(input, m_Reference, registerCount);
        }
    }

    public byte[] getMessage() {
        int len = 5;

        if (m_Registers != null) {
            len += m_Registers.length * 2;
        }

        byte result[] = new byte[len];
        int registerCount = m_Registers != null ? m_Registers.length : 0;

        result[0] = (byte)((m_Reference >> 8) & 0xff);
        result[1] = (byte)(m_Reference & 0xff);
        result[2] = (byte)((registerCount >> 8) & 0xff);
        result[3] = (byte)(registerCount & 0xff);
        result[4] = (byte)(registerCount * 2);

        int offset = 5;

        if (m_NonWordDataHandler == null) {
            for (int i = 0; i < registerCount; i++) {
                byte bytes[] = m_Registers[i].toBytes();
                result[offset++] = bytes[0];
                result[offset++] = bytes[1];
            }
        }
        else {
            m_NonWordDataHandler.prepareData(m_Reference, registerCount);
            byte bytes[] = m_NonWordDataHandler.getData();
            if (bytes != null) {
                int nonWordBytes = bytes.length;
                if (nonWordBytes > registerCount * 2) {
                    nonWordBytes = registerCount * 2;
                }

                System.arraycopy(bytes, 0, result, offset, nonWordBytes);
            }
        }
        return result;
    }
}
