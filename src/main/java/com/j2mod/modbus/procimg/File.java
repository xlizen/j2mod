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
package com.j2mod.modbus.procimg;

import com.j2mod.modbus.util.Logger;

/**
 * @author Julie
 *
 *         File -- an abstraction of a Modbus File, as supported by the
 *         READ FILE RECORD and WRITE FILE RECORD commands.
 */
public class File {

    private static final Logger logger = Logger.getLogger(File.class);

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
