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

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.*;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.net.ModbusMasterFactory;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.Logger;

import java.io.IOException;

/**
 * Class that implements a simple command line tool for writing to an analog
 * output over a Modbus/TCP connection.
 *
 * <p>
 * Note that if you write to a remote I/O with a Modbus protocol stack, it will
 * most likely expect that the communication is <i>kept alive</i> after the
 * first write message.
 *
 * <p>
 * This can be achieved either by sending any kind of message, or by repeating
 * the write message within a given period of time.
 *
 * <p>
 * If the time period is exceeded, then the device might react by turning off
 * all signals of the I/O modules. After this timeout, the device might require
 * a reset message.
 *
 * @author Dieter Wimberger
 * @author jfhaugh
 * @version @version@ (@date@)
 */
public class WriteHoldingRegisterTest {

    private static final Logger logger = Logger.getLogger(WriteHoldingRegisterTest.class);

    private static void printUsage() {
        logger.debug("java com.ghgande.j2mod.modbus.cmd.WriteHoldingRegisterTest" + " <connection [String]>" + " <register [int]> <value [int]> {<repeat [int]>}");
    }

    public static void main(String[] args) {

        ModbusRequest req;
        ModbusTransaction trans;
        ModbusTransport transport = null;
        int ref = 0;
        int value = 0;
        int repeat = 1;
        int unit = 0;

        // 1. Setup parameters
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }

        try {
            try {
                transport = ModbusMasterFactory.createModbusMaster(args[0]);

                if (transport instanceof ModbusSerialTransport) {
                    ((ModbusSerialTransport)transport).setReceiveTimeout(500);
                    if (System.getProperty("com.ghgande.j2mod.modbus.baud") != null) {
                        ((ModbusSerialTransport)transport).setBaudRate(Integer.parseInt(System.getProperty("com.ghgande.j2mod.modbus.baud")));
                    }
                    else {
                        ((ModbusSerialTransport)transport).setBaudRate(19200);
                    }

					/*
                     * Some serial devices are slow to wake up.
					 */
                    Thread.sleep(2000);
                }

                ref = Integer.parseInt(args[1]);
                value = Integer.parseInt(args[2]);

                if (args.length == 4) {
                    repeat = Integer.parseInt(args[3]);
                }

                if (transport instanceof ModbusTCPTransport) {
                    String parts[] = args[0].split(" *: *");
                    if (parts.length >= 4) {
                        unit = Integer.parseInt(parts[3]);
                    }
                }
                else if (transport instanceof ModbusRTUTransport) {
                    String parts[] = args[0].split(" *: *");
                    if (parts.length >= 3) {
                        unit = Integer.parseInt(parts[2]);
                    }

                    String baud = System.getProperty("com.ghgande.j2mod.modbus.baud");
                    if (baud != null) {
                        ((ModbusRTUTransport)transport).setBaudRate(Integer.parseInt(baud));
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                printUsage();
                System.exit(1);
            }

            req = new WriteSingleRegisterRequest(ref, new SimpleRegister(value));

            req.setUnitID(unit);

            // 3. Prepare the transaction
            trans = transport.createTransaction();
            trans.setRequest(req);
            req.setHeadless(trans instanceof ModbusSerialTransaction);

            if (Modbus.debug) {
                logger.debug("Request: " + req.getHexMessage());
            }

            // 4. Execute the transaction repeat times

            for (int count = 0; count < repeat; count++) {
                trans.execute();

                if (Modbus.debug) {
                    logger.debug("Response: " + trans.getResponse().getHexMessage());
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                if (transport != null) {
                    transport.close();
                }
            }
            catch (IOException e) {
                // Do nothing.
            }
        }
        System.exit(0);
    }
}
