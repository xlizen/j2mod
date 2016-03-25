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

/**
 * @author Julie
 *
 *         File -- an abstraction of a Modbus File, as supported by the
 *         READ FILE RECORD and WRITE FILE RECORD commands.
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class File {

    private static final ModbusLogger logger = ModbusLogger.getLogger(File.class);

    private int m_File_Number;
    private int m_Record_Count;
    private Record m_Records[];

    public File(int fileNumber, int records) {
        m_File_Number = fileNumber;
        m_Record_Count = records;
        m_Records = new Record[records];
    }

    public int getFileNumber() {
        return m_File_Number;
    }

    public int getRecordCount() {
        return m_Record_Count;
    }

    public Record getRecord(int i) {
        if (i < 0 || i >= m_Record_Count) {
            throw new IllegalAddressException();
        }

        return m_Records[i];
    }

    public File setRecord(int i, Record record) {
        if (i < 0 || i >= m_Record_Count) {
            throw new IllegalAddressException();
        }

        m_Records[i] = record;

        return this;
    }
}
