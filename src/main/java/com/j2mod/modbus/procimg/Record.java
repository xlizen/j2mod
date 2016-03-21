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

import com.j2mod.modbus.util.Logger;

/**
 * @author Julie
 *
 *         File -- an abstraction of a Modbus Record, as supported by the
 *         READ FILE RECORD and WRITE FILE RECORD commands.
 */
public class Record {

    private static final Logger logger = Logger.getLogger(Record.class);

    private int m_Record_Number;
    private int m_Register_Count;
    private Register m_Registers[];

    public Record(int recordNumber, int registers) {
        m_Record_Number = recordNumber;
        m_Register_Count = registers;
        m_Registers = new Register[registers];

        for (int i = 0; i < m_Register_Count; i++) {
            m_Registers[i] = new SimpleRegister(0);
        }
    }

    public int getRecordNumber() {
        return m_Record_Number;
    }

    public int getRegisterCount() {
        return m_Register_Count;
    }

    public Register getRegister(int register) {
        if (register < 0 || register >= m_Register_Count) {
            throw new IllegalAddressException();
        }

        return m_Registers[register];
    }

    public Record setRegister(int ref, Register register) {
        if (ref < 0 || ref >= m_Register_Count) {
            throw new IllegalAddressException();
        }

        m_Registers[ref] = register;

        return this;
    }
}
