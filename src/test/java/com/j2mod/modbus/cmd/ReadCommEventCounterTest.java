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
package com.j2mod.modbus.cmd;

import com.j2mod.modbus.ModbusException;
import com.j2mod.modbus.io.ModbusSerialTransaction;
import com.j2mod.modbus.io.ModbusSerialTransport;
import com.j2mod.modbus.io.ModbusTransaction;
import com.j2mod.modbus.io.ModbusTransport;
import com.j2mod.modbus.msg.*;
import com.j2mod.modbus.net.ModbusMasterFactory;
import com.j2mod.modbus.util.Logger;

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
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class ReadCommEventCounterTest {

    private static final Logger logger = Logger.getLogger(ReadCommEventCounterTest.class);

    private static void printUsage() {
        logger.system("\nUsage:\n    java com.j2mod.modbus.cmd.ReadCommEventCounterTest <address{:port} [String]> <unit [int]> {<repeat [int]>}");
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
                    logger.system("Cannot open %s", args[0]);
                    System.exit(1);
                }

                if (transport instanceof ModbusSerialTransport) {
                    ((ModbusSerialTransport)transport).setReceiveTimeout(500);
                    if (System.getProperty("com.j2mod.modbus.baud") != null) {
                        ((ModbusSerialTransport)transport).setBaudRate(Integer.parseInt(System.getProperty("com.j2mod.modbus.baud")));
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
                logger.system("Request: %s", req.getHexMessage());

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
                    logger.system(x.getMessage());
                    continue;
                }
                ModbusResponse res = trans.getResponse();
                if (res != null) {
                    logger.system("Response: %s", res.getHexMessage());
                }
                else {
                    logger.system("No response to READ INPUT request");
                }
                if (res instanceof ExceptionResponse) {
                    ExceptionResponse exception = (ExceptionResponse)res;
                    logger.system(exception.toString());
                    continue;
                }

                if (!(res instanceof ReadCommEventCounterResponse)) {
                    continue;
                }

                ReadCommEventCounterResponse data = (ReadCommEventCounterResponse)res;
                logger.system("Status: %d, Events: %d", data.getStatus(), data.getEventCount());
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
