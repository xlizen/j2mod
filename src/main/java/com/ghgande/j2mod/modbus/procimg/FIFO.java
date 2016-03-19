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
package com.ghgande.j2mod.modbus.procimg;

import com.ghgande.j2mod.modbus.util.Logger;

import java.util.Vector;

/**
 * @author Julie
 *
 *         FIFO -- an abstraction of a Modbus FIFO, as supported by the
 *         READ FIFO command.
 *
 *         The FIFO class is only intended to be used for testing purposes and does
 *         not reflect the actual behavior of a FIFO in a real Modbus device.  In an
 *         actual Modbus device, the FIFO is mapped within a fixed address.
 */
public class FIFO {

    private static final Logger logger = Logger.getLogger(FIFO.class);

    private int m_Address;
    private int m_Register_Count;
    private Vector<Register> m_Registers;

    public FIFO(int address) {
        m_Address = address;
        m_Register_Count = 0;
        m_Registers = new Vector<Register>();
    }

    public synchronized int getRegisterCount() {
        return m_Register_Count;
    }

    public synchronized Register[] getRegisters() {
        Register result[] = new Register[m_Register_Count + 1];

        result[0] = new SimpleRegister(m_Register_Count);
        for (int i = 0; i < m_Register_Count; i++) {
            result[i + 1] = m_Registers.get(i);
        }

        return result;
    }

    public synchronized void pushRegister(Register register) {
        if (m_Register_Count == 31) {
            m_Registers.remove(0);
        }
        else {
            m_Register_Count++;
        }

        m_Registers.add(new SimpleRegister(register.getValue()));
    }

    public synchronized void resetRegisters() {
        m_Registers.removeAllElements();
        m_Register_Count = 0;
    }

    public int getAddress() {
        return m_Address;
    }
}
