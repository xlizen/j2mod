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

import com.ghgande.j2mod.modbus.io.ModbusSerialTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.WriteCoilRequest;
import com.ghgande.j2mod.modbus.msg.WriteCoilResponse;
import com.ghgande.j2mod.modbus.net.ModbusMasterFactory;
import com.ghgande.j2mod.modbus.util.Logger;

/**
 * <p>
 * Class that implements a simple commandline tool for writing to a digital
 * output.
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
 * @version 1.2rc1 (09/11/2004)
 */
public class WriteCoilTest {

    private static final Logger logger = Logger.getLogger(WriteCoilTest.class);

    private static void printUsage() {
        logger.debug("java com.ghgande.j2mod.modbus.cmd.WriteCoilTest" + " <connection [String]>" + " <unit [int8]>" + " <coil [int16]>" + " <state [boolean]>" + " {<repeat [int]>}");
    }

    public static void main(String[] args) {
        WriteCoilRequest req;
        ModbusTransport transport = null;
        ModbusTransaction trans;
        int ref = 0;
        boolean value = false;
        int repeat = 1;
        int unit = 0;

        // 1. Setup the parameters
        if (args.length < 4) {
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
                }

				/*
                 * There are a number of devices which won't initialize immediately
				 * after being opened.  Take a moment to let them come up.
				 */
                Thread.sleep(2000);

                unit = Integer.parseInt(args[1]);
                ref = Integer.parseInt(args[2]);
                value = "true".equals(args[3]);

                if (args.length == 5) {
                    repeat = Integer.parseInt(args[4]);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                printUsage();
                System.exit(1);
            }

            // 3. Prepare the request
            req = new WriteCoilRequest(ref, value);
            req.setUnitID(unit);
            logger.debug("Request: " + req.getHexMessage());

            // 4. Prepare the transaction
            trans = transport.createTransaction();
            trans.setRequest(req);

            // 5. Execute the transaction repeat times
            for (int count = 0; count < repeat; count++) {
                trans.execute();

                logger.debug("Response: " + trans.getResponse().getHexMessage());

                WriteCoilResponse data = (WriteCoilResponse)trans.getResponse();
                if (data != null) {
                    logger.debug("Coil = " + data.getCoil());
                }
            }

            // 6. Close the connection
            transport.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        System.exit(0);
    }
}
