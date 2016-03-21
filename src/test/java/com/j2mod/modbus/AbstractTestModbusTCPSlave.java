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

import com.j2mod.modbus.net.ModbusListener;
import com.j2mod.modbus.util.Logger;
import com.j2mod.modbus.utils.TestUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * All the slave unit tests extend this class so that the system will automatically
 * create a test slave to work with and tear it down after a run
 */
public class AbstractTestModbusTCPSlave {

    private static final Logger logger = Logger.getLogger(AbstractTestModbusTCPSlave.class);
    private static ModbusListener listener = null;

    @BeforeClass
    public static void setUpSlave() {
        try {
            TestUtils.loadModPollTool();
            listener = TestUtils.createTCPSlave();
        }
        catch (Exception e) {
            Assert.fail(String.format("Cannot initialise tests - %s", e.getMessage()));
        }
    }

    @AfterClass
    public static void tearDownSlave() {
        if (listener != null && listener.isListening()) {
            listener.stop();
        }
    }

    /**
     * Convenience method to run the modpoll executable with the given command parameters
     * to read from a register
     *
     * @param register       Register reference number
     * @param type           Type of register to query
     * @param expectedOutput The text that should be found in the output
     *
     * @return True if the expected output is available
     */
    public static boolean readModPoll(int register, int type, String expectedOutput) {
        return readModPoll(register, type, 1, expectedOutput);
    }

    /**
     * Convenience method to run the modpoll executable with the given command parameters
     * to read from a register
     *
     * @param register          Register reference number
     * @param type              Type of register to query
     * @param numberOfRegisters Number of registers to read
     * @param expectedOutput    The text that should be found in the output
     *
     * @return True if the expected output is available
     */
    public static boolean readModPoll(int register, int type, int numberOfRegisters, String expectedOutput) {
        return execModPoll(register, type, null, expectedOutput, numberOfRegisters);
    }

    /**
     * Convenience method to run the modpoll executable with the given command parameters
     * to write to a register
     *
     * @param register       Register reference number
     * @param type           Type of register to query
     * @param expectedOutput The text that should be found in the output
     *
     * @return True if the expected output is available
     */
    public static boolean writeModPoll(int register, int type, int value, String expectedOutput) {
        return execModPoll(register, type, value, expectedOutput, 1);
    }

    /**
     * Convenience method to run the modpoll executable with the given command parameters
     *
     * @param register          Register reference number
     * @param type              Type of register to query
     * @param outValue          Value to write to register
     * @param expectedOutput    The text that should be found in the output
     * @param numberOfRegisters Number of registers to read
     *
     * @return True if the expected output is available
     */
    private static boolean execModPoll(int register, int type, Integer outValue, String expectedOutput, int numberOfRegisters) {
        try {
            String output = TestUtils.execToString(String.format("%smodpoll -m tcp -r %d -t %d -c %d -1 %s %s",
                    TestUtils.getTemporaryDirectory(), register, type, numberOfRegisters,
                    TestUtils.LOCALHOST, outValue == null ? "" : outValue));
            boolean returnValue = output != null && output.replaceAll("[\r]", "").contains(expectedOutput);
            if (!returnValue) {
                logger.error(output);
            }
            return returnValue;
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

}
