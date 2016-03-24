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
package com.j2mod.modbus;

import com.j2mod.modbus.msg.ReadCoilsResponse;
import com.j2mod.modbus.msg.ReadInputDiscretesResponse;
import com.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.j2mod.modbus.utils.AbstractTestModbusUDPMaster;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class tests the TCP master read features of the library
 */
@SuppressWarnings("ConstantConditions")
public class TestModbusUDPRead extends AbstractTestModbusUDPMaster {

    @Test
    public void testReadCoils() {
        ReadCoilsResponse res = (ReadCoilsResponse)readRequest(Modbus.READ_COILS, 0, 1);
        Assert.assertEquals("Incorrect status for coil 0", true, res.getCoilStatus(0));
        Assert.assertEquals("Incorrect status for coil 1", false, res.getCoilStatus(1));
    }

    @Test
    public void testReadInvalidCoil() {
        Assert.assertNull("Failed check for missing coil 3", readRequest(Modbus.READ_COILS, 3, 1));
    }

    @Test
    public void testReadDiscretes() {
        ReadInputDiscretesResponse res = (ReadInputDiscretesResponse)readRequest(Modbus.READ_INPUT_DISCRETES, 0, 2);
        Assert.assertEquals("Incorrect status for discrete 1", false, res.getDiscreteStatus(0));
        Assert.assertEquals("Incorrect status for discrete 2", true, res.getDiscreteStatus(1));
    }

    @Test
    public void testReadInvalidDiscretes() {
        Assert.assertNull("Failed check for missing discrete 3", readRequest(Modbus.READ_INPUT_DISCRETES, 9, 1));
    }

    @Test
    public void testReadInputRegisters() {
        ReadInputRegistersResponse res = (ReadInputRegistersResponse)readRequest(Modbus.READ_INPUT_REGISTERS, 0, 1);
        Assert.assertEquals("Incorrect value for input register 1", 45, res.getRegisterValue(0));
    }

    @Test
    public void testReadInvalidInputRegisters() {
        Assert.assertNull("Failed check for missing input register 6", readRequest(Modbus.READ_INPUT_REGISTERS, 6, 1));
    }

    @Test
    public void testReadHoldingRegisters() {
        ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse)readRequest(Modbus.READ_HOLDING_REGISTERS, 0, 1);
        Assert.assertEquals("Incorrect value for holding register 1", 251, res.getRegisterValue(0));
    }

    @Test
    public void testReadInvalidHoldingRegisters() {
        Assert.assertNull("Failed check for missing holding register 5", readRequest(Modbus.READ_HOLDING_REGISTERS, 5, 1));
    }

    @Test
    public void testReadMultipleInputRegisters() {
        ReadInputRegistersResponse res = (ReadInputRegistersResponse)readRequest(Modbus.READ_INPUT_REGISTERS, 0, 5);
        Assert.assertEquals("Failed to read multiple input register 1 length 5", 45, res.getRegisterValue(0));
        Assert.assertEquals("Failed to read multiple input register 2 length 5", 9999, res.getRegisterValue(1));
        Assert.assertEquals("Failed to read multiple input register 3 length 5", 8888, res.getRegisterValue(2));
        Assert.assertEquals("Failed to read multiple input register 4 length 5", 7777, res.getRegisterValue(3));
        Assert.assertEquals("Failed to read multiple input register 5 length 5", 6666, res.getRegisterValue(4));
    }

    @Test
    public void testReadMultipleHoldingRegisters() {
        ReadMultipleRegistersResponse res = (ReadMultipleRegistersResponse)readRequest(Modbus.READ_HOLDING_REGISTERS, 0, 5);
        Assert.assertEquals("Failed to read multiple holding register 1 length 5", 251, res.getRegisterValue(0));
        Assert.assertEquals("Failed to read multiple holding register 2 length 5", 1111, res.getRegisterValue(1));
        Assert.assertEquals("Failed to read multiple holding register 3 length 5", 2222, res.getRegisterValue(2));
        Assert.assertEquals("Failed to read multiple holding register 4 length 5", 3333, res.getRegisterValue(3));
        Assert.assertEquals("Failed to read multiple holding register 5 length 5", 4444, res.getRegisterValue(4));
    }

}
