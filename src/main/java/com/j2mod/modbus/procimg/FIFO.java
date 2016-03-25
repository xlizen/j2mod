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
package com.j2mod.modbus.procimg;

import com.j2mod.modbus.util.ModbusLogger;

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
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class FIFO {

    private static final ModbusLogger logger = ModbusLogger.getLogger(FIFO.class);

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
