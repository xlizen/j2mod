/*
 * This file is part of j2mod-steve.
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
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.util.BitVector;
import com.ghgande.j2mod.modbus.util.Logger;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import com.ghgande.j2mod.modbus.util.SerialParameters;

//////////////////////////////////////////////////////////////////////////
//
//  File:  SerialFacadeTest.java
//
//  Description: Unit test driver to exerecise the methods for
//  ModbusSerialMaster class.
//
//  Programmer:  JDC (CCC), Wed Feb  4 11:54:23 2004
//
//  Change History:
//
//  $Log: SerialFacadeTest.java,v $
//  Revision 1.2  2004/10/21 16:44:36  wimpi
//  Please see status file for changes.
//
//  Revision 1.1  2004/09/30 01:45:38  jdcharlton
//  Test driver for ModbusSerialMaster facade
//
//
//
//////////////////////////////////////////////////////////////////////////

public class SerialFacadeTest {

    private static final Logger logger = Logger.getLogger(SerialFacadeTest.class);

    private static void printUsage() {
        logger.debug("java com.ghgande.j2mod.modbus.cmd.SerialAITest" + " <portname [String]>" + " <Unit Address [int8]>");
    }

    public static void main(String[] args) {
        int inChar;
        int result = 0;
        boolean finished = false;
        int slaveId = 88;
        String portname = null;
        ModbusSerialMaster msm = null;

        // 1. Setup the parameters
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }
        else {
            try {
                portname = args[0];
                slaveId = Integer.parseInt(args[1]);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                printUsage();
                System.exit(1);
            }
        }

        try {
            logger.debug("Sending test messages to slave: " + slaveId);
            logger.debug("com.ghgande.j2mod.modbus.debug set to: " + System.getProperty("com.ghgande.j2mod.modbus.debug"));

            logger.debug("Hit enter to start and <s enter> to terminate the test.");
            inChar = System.in.read();
            if ((inChar == 's') || (inChar == 'S')) {
                logger.debug("Exiting");
                System.exit(0);
            }

            // 2. Setup serial parameters
            SerialParameters params = new SerialParameters();
            params.setPortName(portname);
            params.setBaudRate(9600);
            params.setDatabits(8);
            params.setParity("None");
            params.setStopbits(1);
            params.setEncoding("rtu");
            params.setEcho(false);

            if (Modbus.debug) {
                logger.debug("Encoding [" + params.getEncoding() + "]");
            }

            // 3. Create the master facade
            msm = new ModbusSerialMaster(params);
            msm.connect();

            do {
                if (msm.writeCoil(slaveId, 4, true)) {
                    logger.debug("Set output 5 to true");
                }
                else {
                    System.err.println("Error setting slave " + slaveId + " output 5");
                }
                BitVector coils = msm.readCoils(slaveId, 0, 8);
                if (coils != null) {
                    logger.debug("Coils:");
                    for (int i = 0; i < coils.size(); i++) {
                        logger.debug(" " + i + ": " + coils.getBit(i));
                    }

                    try {
                        msm.writeMultipleCoils(slaveId, 0, coils);
                    }
                    catch (ModbusException ex) {
                        logger.debug("Error writing coils: " + result);
                    }
                }
                else {
                    logger.debug("Outputs: null");
                    msm.disconnect();
                    System.exit(-1);
                }

                BitVector digInp = msm.readInputDiscretes(slaveId, 0, 8);

                if (digInp != null) {
                    logger.debug("Digital Inputs:");
                    for (int i = 0; i < digInp.size(); i++) {
                        logger.debug(" " + i + ": " + digInp.getBit(i));
                    }
                    logger.debug("Inputs: " + ModbusUtil.toHex(digInp.getBytes()));
                }
                else {
                    logger.debug("Inputs: null");
                    msm.disconnect();
                    System.exit(-1);
                }

                InputRegister[] ai;
                for (int i = 1000; i < 1010; i++) {
                    ai = msm.readInputRegisters(slaveId, i, 1);
                    if (ai != null) {
                        logger.debug("Tag " + i + ": ");
                        for (InputRegister anAi : ai) {
                            logger.debug(" " + anAi.getValue());
                        }
                    }
                    else {
                        logger.debug("Tag: " + i + " null");
                        msm.disconnect();
                        System.exit(-1);
                    }
                }

                Register[] regs;
                for (int i = 1000; i < 1005; i++) {
                    regs = msm.readMultipleRegisters(slaveId, i, 1);
                    if (regs != null) {
                        logger.debug("RWRegisters " + i + " length: " + regs.length);
                        for (Register reg : regs) {
                            logger.debug(" " + reg.getValue());
                        }
                    }
                    else {
                        logger.debug("RWRegisters " + i + ": null");
                        msm.disconnect();
                        System.exit(-1);
                    }
                }
                regs = msm.readMultipleRegisters(slaveId, 0, 10);
                logger.debug("Registers: ");
                if (regs != null) {
                    logger.debug("regs :");
                    for (int n = 0; n < regs.length; n++) {
                        logger.debug("  " + n + "= " + regs[n]);
                    }
                }
                else {
                    logger.debug("Registers: null");
                    msm.disconnect();
                    System.exit(-1);
                }
                while (System.in.available() > 0) {
                    inChar = System.in.read();
                    if ((inChar == 's') || (inChar == 'S')) {
                        finished = true;
                    }
                }
            } while (!finished);
        }
        catch (Exception e) {
            System.err.println("SerialFacadeTest driver: " + e);
            e.printStackTrace();
        }
        if (msm != null) {
            msm.disconnect();
        }
    }
}
