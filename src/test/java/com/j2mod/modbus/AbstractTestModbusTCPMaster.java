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

import com.j2mod.modbus.io.ModbusTCPTransaction;
import com.j2mod.modbus.io.ModbusTCPTransport;
import com.j2mod.modbus.msg.*;
import com.j2mod.modbus.net.ModbusListener;
import com.j2mod.modbus.procimg.SimpleRegister;
import com.j2mod.modbus.util.Logger;
import com.j2mod.modbus.utils.TestUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.IOException;
import java.net.Socket;

/**
 * All the master unit tests extend this class so that the system will automatically
 * create a test slave to work with and tear it down after a run
 */
public class AbstractTestModbusTCPMaster {

    private static final Logger logger = Logger.getLogger(AbstractTestModbusTCPMaster.class);
    private static ModbusListener listener = null;

    @BeforeClass
    public static void setUpSlave() {
        try {
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
     * Executes a read transaction using the function code, register and count
     *
     * @param functionCode Function code to use
     * @param register     Register number
     * @param count        Number of registers
     *
     * @return Response object
     */
    protected static ModbusResponse readRequest(int functionCode, int register, int count) {
        ModbusTCPTransport transport = null;
        ModbusTCPTransaction trans;
        try {
            // Create a socket to use
            Socket socket = new Socket(TestUtils.LOCALHOST, Modbus.DEFAULT_PORT);
            transport = new ModbusTCPTransport(socket);
            Thread.sleep(500);
            ModbusRequest req = null;

            // Prepare the request
            switch (functionCode) {
                case Modbus.READ_COILS:
                    req = new ReadCoilsRequest(register, count);
                    break;
                case Modbus.READ_INPUT_DISCRETES:
                    req = new ReadInputDiscretesRequest(register, count);
                    break;
                case Modbus.READ_INPUT_REGISTERS:
                    req = new ReadInputRegistersRequest(register, count);
                    break;
                case Modbus.READ_HOLDING_REGISTERS:
                    req = new ReadMultipleRegistersRequest(register, count);
                    break;
                default:
                    Assert.fail(String.format("Request type %d is not supported by the test harness", functionCode));
            }
            req.setUnitID(TestUtils.UNIT_ID);

            // Prepare the transaction
            trans = (ModbusTCPTransaction)transport.createTransaction();
            trans.setRequest(req);
            trans.setReconnecting(true);

            // Execute the transaction
            trans.execute();
            return trans.getResponse();
        }
        catch (Exception e) {
            logger.debug(e.getMessage());
        }
        finally {
            if (transport != null) {
                try {
                    transport.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Executes a write transaction using the function code, register and value
     *
     * @param functionCode Function code to use
     * @param register     Register number
     * @param value        Value to apply
     *
     * @return Response object
     */
    protected static ModbusResponse writeRequest(int functionCode, int register, int value) {
        ModbusTCPTransport transport = null;
        ModbusTCPTransaction trans;
        try {
            // Create a socket to use
            Socket socket = new Socket(TestUtils.LOCALHOST, Modbus.DEFAULT_PORT);
            transport = new ModbusTCPTransport(socket);
            Thread.sleep(500);
            ModbusRequest req = null;

            // Prepare the request
            switch (functionCode) {
                case Modbus.WRITE_COIL:
                    req = new WriteCoilRequest(register, value != 0);
                    break;
                case Modbus.WRITE_SINGLE_REGISTER:
                    req = new WriteSingleRegisterRequest(register, new SimpleRegister(value));
                    break;
                default:
                    Assert.fail(String.format("Request type %d is not supported by the test harness", functionCode));
            }
            req.setUnitID(TestUtils.UNIT_ID);

            // Prepare the transaction
            trans = (ModbusTCPTransaction)transport.createTransaction();
            trans.setRequest(req);
            trans.setReconnecting(true);

            // Execute the transaction
            trans.execute();
            return trans.getResponse();
        }
        catch (Exception e) {
            logger.debug(e.getMessage());
        }
        finally {
            if (transport != null) {
                try {
                    transport.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return null;
    }

}
