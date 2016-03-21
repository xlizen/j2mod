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
import com.j2mod.modbus.msg.ReadInputDiscretesResponse;
import com.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.j2mod.modbus.util.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class tests the TCP master read features of the library
 */
@SuppressWarnings("ConstantConditions")
public class TestModbusTCPMasterRead extends AbstractTestModbusTCPMaster {

    private static final Logger logger = Logger.getLogger(TestModbusTCPMasterRead.class);

    @Test
    public void testMasterReadCoils() {
        ReadCoilsResponse res = (ReadCoilsResponse)readRequest(Modbus.READ_COILS, 0, 1);
        Assert.assertEquals("Incorrect status for coil 0", true, res.getCoilStatus(0));
        Assert.assertEquals("Incorrect status for coil 1", false, res.getCoilStatus(1));
    }

    @Test
    public void testMasterReadInvalidCoil() {
        Assert.assertNull("Failed check for missing coil 3", readRequest(Modbus.READ_COILS, 3, 1));
    }

    @Test
    public void testMasterReadDiscretes() {
        ReadInputDiscretesResponse res = (ReadInputDiscretesResponse)readRequest(Modbus.READ_INPUT_DISCRETES, 0, 2);
        Assert.assertEquals("Incorrect status for discrete 1", false, res.getDiscreteStatus(0));
        Assert.assertEquals("Incorrect status for discrete 2", true, res.getDiscreteStatus(1));
    }

    @Test
    public void testMasterReadInvalidDiscretes() {
        Assert.assertNull("Failed check for missing discrete 3", readRequest(Modbus.READ_INPUT_DISCRETES, 9, 1));
    }

    @Test
    public void testMasterReadInputRegisters() {
        ReadInputRegistersResponse res = (ReadInputRegistersResponse)readRequest(Modbus.READ_INPUT_REGISTERS, 0, 1);
        Assert.assertEquals("Incorrect value for input register 1", 45, res.getRegisterValue(0));
    }

    @Test
    public void testMasterReadInvalidInputRegisters() {
        Assert.assertNull("Failed check for missing input register 6", readRequest(Modbus.READ_INPUT_REGISTERS, 6, 1));
    }

    @Test
    public void testMasterReadHoldingRegisters() {
        ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse)readRequest(Modbus.READ_HOLDING_REGISTERS, 0, 1);
        Assert.assertEquals("Incorrect value for holding register 1", 251, res.getRegisterValue(0));
    }

    @Test
    public void testMasterReadInvalidHoldingRegisters() {
        Assert.assertNull("Failed check for missing holding register 5", readRequest(Modbus.READ_HOLDING_REGISTERS, 5, 1));
    }

    @Test
    public void testMasterReadMultipleInputRegisters() {
        ReadInputRegistersResponse res = (ReadInputRegistersResponse)readRequest(Modbus.READ_INPUT_REGISTERS, 0, 5);
        Assert.assertEquals("Failed to read multiple input register 1 length 5", 45, res.getRegisterValue(0));
        Assert.assertEquals("Failed to read multiple input register 2 length 5", 9999, res.getRegisterValue(1));
        Assert.assertEquals("Failed to read multiple input register 3 length 5", 8888, res.getRegisterValue(2));
        Assert.assertEquals("Failed to read multiple input register 4 length 5", 7777, res.getRegisterValue(3));
        Assert.assertEquals("Failed to read multiple input register 5 length 5", 6666, res.getRegisterValue(4));
    }

    @Test
    public void testMasterReadMultipleHoldingRegisters() {
        ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse)readRequest(Modbus.READ_HOLDING_REGISTERS, 0, 5);
        Assert.assertEquals("Failed to read multiple holding register 1 length 5", 251, res.getRegisterValue(0));
        Assert.assertEquals("Failed to read multiple holding register 2 length 5", 1111, res.getRegisterValue(1));
        Assert.assertEquals("Failed to read multiple holding register 3 length 5", 2222, res.getRegisterValue(2));
        Assert.assertEquals("Failed to read multiple holding register 4 length 5", 3333, res.getRegisterValue(3));
        Assert.assertEquals("Failed to read multiple holding register 5 length 5", 4444, res.getRegisterValue(4));
    }

}
