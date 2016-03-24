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

import com.j2mod.modbus.utils.AbstractTestModbusTCPSlave;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class uses an external master tool to test that the j2mod slave write features
 * work against a known good standard
 * Once this is verified, the j2mod slave code can be used to test the features of the
 * master
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class TestModbusTCPExternalWrite extends AbstractTestModbusTCPSlave {

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
