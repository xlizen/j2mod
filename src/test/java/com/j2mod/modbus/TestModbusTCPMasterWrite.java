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
 * along with j2mod.  If not, see <http://www.gnu.org/licenses
 */
package com.j2mod.modbus;

import com.j2mod.modbus.msg.ReadCoilsResponse;
import com.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.j2mod.modbus.msg.WriteCoilResponse;
import com.j2mod.modbus.msg.WriteSingleRegisterResponse;
import com.j2mod.modbus.util.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class tests the TCP master write features of the library
 */
@SuppressWarnings("ConstantConditions")
public class TestModbusTCPMasterWrite extends AbstractTestModbusTCPMaster {

    private static final Logger logger = Logger.getLogger(TestModbusTCPMasterWrite.class);

    @Test
    public void testMasterWriteCoils() {
        WriteCoilResponse res = (WriteCoilResponse)writeRequest(Modbus.WRITE_COIL, 1, 1);
        Assert.assertEquals("Incorrect write status for coil 2", true, res.getCoil());
        ReadCoilsResponse res1 = (ReadCoilsResponse)readRequest(Modbus.READ_COILS, 1, 1);
        Assert.assertEquals("Incorrect status for coil 2", true, res1.getCoilStatus(0));
    }

    @Test
    public void testMasterWriteHoldingRegisters() {
        WriteSingleRegisterResponse res = (WriteSingleRegisterResponse)writeRequest(Modbus.WRITE_SINGLE_REGISTER, 0, 5555);
        Assert.assertEquals("Incorrect write status for register 1", 5555, res.getRegisterValue());
        ReadMultipleRegistersResponse res1 = (ReadMultipleRegistersResponse)readRequest(Modbus.READ_HOLDING_REGISTERS, 0, 1);
        Assert.assertEquals("Incorrect status for register 0", 5555, res1.getRegisterValue(0));
    }

}
