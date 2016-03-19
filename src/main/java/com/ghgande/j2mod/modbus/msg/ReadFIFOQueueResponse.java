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
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.SimpleInputRegister;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>ReadFIFOQueueResponse</tt>.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 *
 * @version @version@ (@date@)
 */
public final class ReadFIFOQueueResponse extends ModbusResponse {

    /*
     * Message fields.
     */
    private int m_Count;
    private InputRegister m_Registers[];

    /**
     * Constructs a new <tt>ReadFIFOQueueResponse</tt> instance.
     */
    public ReadFIFOQueueResponse() {
        super();

        setFunctionCode(Modbus.READ_FIFO_QUEUE);

        m_Count = 0;
        m_Registers = new InputRegister[0];

        setDataLength(7);
    }

    /**
     * getWordCount -- get the queue size.
     *
     * @return Word count int
     */
    synchronized public int getWordCount() {
        return m_Count;
    }

    /**
     * getWordCount -- set the queue size.
     *
     * @param ref Register
     */
    public synchronized void setWordCount(int ref) {
        if (ref < 0 || ref > 31) {
            throw new IllegalArgumentException();
        }

        int oldCount = m_Count;
        InputRegister[] newRegisters = new InputRegister[ref];

        m_Count = ref;

        for (int i = 0; i < ref; i++) {
            if (i < oldCount) {
                newRegisters[i] = m_Registers[i];
            }
            else {
                newRegisters[i] = new SimpleRegister(0);
            }
        }
    }

    synchronized public int[] getRegisters() {
        int values[] = new int[m_Count];

        for (int i = 0; i < m_Count; i++) {
            values[i] = getRegister(i);
        }

        return values;
    }

    /**
     * setRegisters -- set the device's status.
     *
     * @param regs Array of registers
     */
    public synchronized void setRegisters(InputRegister[] regs) {
        m_Registers = regs;
        if (regs == null) {
            m_Count = 0;
            return;
        }

        if (regs.length > 31) {
            throw new IllegalArgumentException();
        }

        m_Count = regs.length;
    }

    public int getRegister(int index) {
        return m_Registers[index].getValue();
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

		/*
         * Read and discard the byte count.  There's no way to indicate
		 * the packet was inconsistent, other than throwing an I/O
		 * exception for an invalid packet format ...
		 */
        din.readShort();

		/*
		 * The first register is the number of registers which
		 * follow.  Save that as m_Count, not as a register.
		 */
        m_Count = din.readShort();
        m_Registers = new InputRegister[m_Count];

        for (int i = 0; i < m_Count; i++) {
            m_Registers[i] = new SimpleInputRegister(din.readShort());
        }
    }

    /**
     * getMessage -- format the message into a byte array.
     */
    public byte[] getMessage() {
        byte result[] = new byte[m_Count * 2 + 4];

        int len = m_Count * 2 + 2;
        result[0] = (byte)(len >> 8);
        result[1] = (byte)(len & 0xFF);
        result[2] = (byte)(m_Count >> 8);
        result[3] = (byte)(m_Count & 0xFF);

        for (int i = 0; i < m_Count; i++) {
            byte value[] = m_Registers[i].toBytes();
            result[i * 2 + 4] = value[0];
            result[i * 2 + 5] = value[1];
        }
        return result;
    }
}
