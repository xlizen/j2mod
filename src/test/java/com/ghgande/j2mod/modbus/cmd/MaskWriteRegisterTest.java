/*
 * This file is part of j2mod.
 *
 * j2mod is a fork of the jamod library written by Dieter Wimberger
 * and then further enhanced by Julie Haugh with a new LGPL license
 * and upgraded to Java 1.6
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
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.MaskWriteRegisterRequest;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.net.ModbusMasterFactory;

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
public class MaskWriteRegisterTest {

    private static void printUsage() {
        System.out.println("java com.ghgande.j2mod.modbus.cmd.WriteHoldingRegisterTest" + " <address{:<port>{:<unit>}} [String]>" + " <register [int]> <andMask [int]> <orMask [int]> {<repeat [int]>}");
    }

    public static void main(String[] args) {

        ModbusTransport transport = null;
        ModbusRequest req;
        ModbusTransaction trans;
        int ref = 0;
        int andMask = 0xFFFF;
        int orMask = 0;
        int repeat = 1;
        int unit = 0;

        // 1. Setup parameters
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }

        try {
            try {
                ref = Integer.parseInt(args[1]);
                andMask = Integer.parseInt(args[2]);
                orMask = Integer.parseInt(args[3]);

                if (args.length == 5) {
                    repeat = Integer.parseInt(args[4]);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                printUsage();
                System.exit(1);
            }

            // 2. Open the connection
            transport = ModbusMasterFactory.createModbusMaster(args[0]);

            if (Modbus.debug) {
                System.out.println("Connected to " + transport);
            }

            req = new MaskWriteRegisterRequest(ref, andMask, orMask);

            req.setUnitID(unit);
            if (Modbus.debug) {
                System.out.println("Request: " + req.getHexMessage());
            }

            // 3. Prepare the transaction
            trans = transport.createTransaction();
            trans.setRequest(req);

            // 4. Execute the transaction repeat times

            for (int count = 0; count < repeat; count++) {
                trans.execute();

                if (Modbus.debug) {
                    if (trans.getResponse() != null) {
                        System.out.println("Response: " + trans.getResponse().getHexMessage());
                    }
                    else {
                        System.out.println("No response.");
                    }
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
    }
}
