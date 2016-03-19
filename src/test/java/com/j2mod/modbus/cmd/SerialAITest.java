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
package com.j2mod.modbus.cmd;

import com.j2mod.modbus.ModbusCoupler;
import com.j2mod.modbus.io.ModbusSerialTransaction;
import com.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.j2mod.modbus.net.SerialConnection;
import com.j2mod.modbus.util.Logger;
import com.j2mod.modbus.util.SerialParameters;

/**
 * Class that implements a simple commandline
 * tool for reading an analog input.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class SerialAITest {

    private static final Logger logger = Logger.getLogger(SerialAITest.class);

    public static void main(String[] args) {

        SerialConnection con = null;
        ModbusSerialTransaction trans;
        ReadInputRegistersRequest req;
        ReadInputRegistersResponse res;

        String portname = null;
        int unitid = 0;
        int ref = 0;
        int count = 0;
        int repeat = 1;

        try {

            //1. Setup the parameters
            if (args.length < 4) {
                printUsage();
                System.exit(1);
            }
            else {
                try {
                    portname = args[0];
                    unitid = Integer.parseInt(args[1]);
                    ref = Integer.parseInt(args[2]);
                    count = Integer.parseInt(args[3]);
                    if (args.length == 5) {
                        repeat = Integer.parseInt(args[4]);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    printUsage();
                    System.exit(1);
                }
            }

            //2. Set slave identifier for master response parsing
            ModbusCoupler.getReference().setUnitID(unitid);

            logger.debug("com.ghgande.j2mod.modbus.debug set to: " + System.getProperty("com.ghgande.j2mod.modbus.debug"));

            //3. Setup serial parameters
            SerialParameters params = new SerialParameters();
            params.setPortName(portname);
            params.setBaudRate(9600);
            params.setDatabits(8);
            params.setParity("None");
            params.setStopbits(1);
            params.setEncoding("ascii");
            params.setEcho(false);
            logger.debug("Encoding [" + params.getEncoding() + "]");

            //4. Open the connection
            con = new SerialConnection(params);
            con.open();

            //5. Prepare a request
            req = new ReadInputRegistersRequest(ref, count);
            req.setUnitID(unitid);
            req.setHeadless();
            logger.debug("Request: " + req.getHexMessage());

            //6. Prepare the transaction
            trans = new ModbusSerialTransaction(con);
            trans.setRequest(req);

            //7. Execute the transaction repeat times
            int k = 0;
            do {
                trans.execute();

                res = (ReadInputRegistersResponse)trans.getResponse();
                logger.debug("Response: " + res.getHexMessage());
                for (int n = 0; n < res.getWordCount(); n++) {
                    logger.debug("Word " + n + "=" + res.getRegisterValue(n));
                }
                k++;
            } while (k < repeat);

            //8. Close the connection
            con.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
            // Close the connection
            if (con != null) {
                con.close();
            }
        }
    }

    private static void printUsage() {
        logger.debug("java com.ghgande.j2mod.modbus.cmd.SerialAITest <portname [String]>  <Unit Address [int8]> <register [int16]> <wordcount [int16]> {<repeat [int]>}"
        );
    }
}
