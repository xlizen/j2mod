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
package com.ghgande.j2mod.modbus.cmd;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.ModbusMasterFactory;
import com.ghgande.j2mod.modbus.util.Logger;

import java.io.IOException;

/**
 * Class that implements a simple command line tool for reading the coomunications
 * event counter.
 *
 * <p>
 * Note that if you read from a remote I/O with a Modbus protocol stack, it will
 * most likely expect that the communication is <i>kept alive</i> after the
 * first read message.
 *
 * <p>
 * This can be achieved either by sending any kind of message, or by repeating
 * the read message within a given period of time.
 *
 * <p>
 * If the time period is exceeded, then the device might react by turning off
 * all signals of the I/O modules. After this timeout, the device might require
 * a reset message.
 *
 * @author Julie Haugh
 * @version 1.04 (1/18/2014)
 */
public class ReadCommEventCounterTest {

    private static final Logger logger = Logger.getLogger(ReadCommEventCounterTest.class);

    private static void printUsage() {
        logger.debug("java com.ghgande.j2mod.modbus.cmd.ReadCommEventCounterTest" + " <address{:port} [String]>" + " <unit [int]>" + " {<repeat [int]>}");
    }

    public static void main(String[] args) {
        ModbusTransport transport = null;
        ModbusRequest req;
        ModbusTransaction trans = null;
        int repeat = 1;
        int unit = 0;

        // 1. Setup parameters
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        try {
            try {
                // 2. Open the connection.
                transport = ModbusMasterFactory.createModbusMaster(args[0]);
                if (transport == null) {
                    logger.debug("Cannot open " + args[0]);
                    System.exit(1);
                }

                if (transport instanceof ModbusSerialTransport) {
                    ((ModbusSerialTransport)transport).setReceiveTimeout(500);
                    if (System.getProperty("com.ghgande.j2mod.modbus.baud") != null) {
                        ((ModbusSerialTransport)transport).setBaudRate(Integer.parseInt(System.getProperty("com.ghgande.j2mod.modbus.baud")));
                    }
                    else {
                        ((ModbusSerialTransport)transport).setBaudRate(19200);
                    }
                }

				/*
                 * There are a number of devices which won't initialize immediately
				 * after being opened.  Take a moment to let them come up.
				 */
                Thread.sleep(2000);

                if (args.length > 1) {
                    unit = Integer.parseInt(args[1]);
                }

                if (args.length > 2) {
                    repeat = Integer.parseInt(args[2]);
                }

            }
            catch (Exception ex) {
                ex.printStackTrace();
                printUsage();
                System.exit(1);
            }

            // 5. Execute the transaction repeat times

            for (int k = 0; k < repeat; k++) {
                // 3. Create the command.
                req = new ReadCommEventCounterRequest();
                req.setUnitID(unit);
                req.setHeadless(trans instanceof ModbusSerialTransaction);

                logger.debug("Request: " + req.getHexMessage());

                // 4. Prepare the transaction
                trans = transport.createTransaction();
                trans.setRequest(req);
                trans.setRetries(1);

                if (trans instanceof ModbusSerialTransaction) {
                    /*
					 * 10ms interpacket delay.
					 */
                    ((ModbusSerialTransaction)trans).setTransDelayMS(10);
                }

                try {
                    trans.execute();
                }
                catch (ModbusException x) {
                    logger.debug(x.getMessage());
                    continue;
                }
                ModbusResponse res = trans.getResponse();
                if (res != null) {
                    logger.debug("Response: " + res.getHexMessage());
                }
                else {
                    logger.debug("No response to READ INPUT request");
                }
                if (res instanceof ExceptionResponse) {
                    ExceptionResponse exception = (ExceptionResponse)res;
                    logger.debug(exception);
                    continue;
                }

                if (!(res instanceof ReadCommEventCounterResponse)) {
                    continue;
                }

                ReadCommEventCounterResponse data = (ReadCommEventCounterResponse)res;
                logger.debug("Status: " + data.getStatus() + ", Events " + data.getEventCount());
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            // 6. Close the connection
            if (transport != null) {
                transport.close();
            }
        }
        catch (IOException e) {
            // Do nothing.
        }
        System.exit(0);
    }
}
