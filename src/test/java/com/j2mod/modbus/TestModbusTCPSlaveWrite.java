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

import org.junit.Assert;
import org.junit.Test;

/**
 * This class uses an external master tool to test that the j2mod slave write features
 * work against a known good standard
 * Once this is verified, the j2mod slave code can be used to test the features of the
 * master
 */
public class TestModbusTCPSlaveWrite extends AbstractTestModbusTCPSlave {

    @Test
    public void testSlaveWriteCoils() {
        Assert.assertEquals("Incorrect write status for coil 1", true, writeModPoll(1, 0, 0, "Written 1 reference"));
        Assert.assertEquals("Incorrect status for coil 1", true, readModPoll(1, 0, "[1]: 0"));
    }

    @Test
    public void testSlaveWriteHoldingRegisters() {
        Assert.assertEquals("Incorrect write status for holding register 1", true, writeModPoll(1, 4, 5555, "Written 1 reference"));
        Assert.assertEquals("Incorrect value for holding register 1", true, readModPoll(1, 4, "[1]: 5555"));
    }

}
