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
import com.j2mod.modbus.facade.ModbusSerialMaster;
import com.j2mod.modbus.procimg.InputRegister;
import com.j2mod.modbus.procimg.Register;
import com.j2mod.modbus.util.BitVector;
import com.j2mod.modbus.util.ModbusLogger;
import com.j2mod.modbus.util.ModbusUtil;
import com.j2mod.modbus.util.SerialParameters;

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

    private static final ModbusLogger logger = ModbusLogger.getLogger(SerialFacadeTest.class);

    private static void printUsage() {
        logger.system("\nUsage:\n    java com.j2mod.modbus.cmd.SerialAITest <portname [String]> <Unit Address [int8]>");
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
            logger.system("Sending test messages to slave: %s", slaveId);
            logger.system("Hit enter to start and <s enter> to terminate the test");
            inChar = System.in.read();
            if ((inChar == 's') || (inChar == 'S')) {
                logger.system("Exiting");
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

            logger.system("Encoding [%s]", params.getEncoding());

            // 3. Create the master facade
            msm = new ModbusSerialMaster(params);
            msm.connect();

            do {
                if (msm.writeCoil(slaveId, 4, true)) {
                    logger.system("Set output 5 to true");
                }
                else {
                    logger.system("Error setting slave " + slaveId + " output 5");
                }
                BitVector coils = msm.readCoils(slaveId, 0, 8);
                if (coils != null) {
                    logger.system("Coils:");
                    for (int i = 0; i < coils.size(); i++) {
                        logger.system(" %d: %d", i, coils.getBit(i));
                    }

                    try {
                        msm.writeMultipleCoils(slaveId, 0, coils);
                    }
                    catch (ModbusException ex) {
                        logger.system("Error writing coils: %d", result);
                    }
                }
                else {
                    logger.system("Outputs: null");
                    msm.disconnect();
                    System.exit(-1);
                }

                BitVector digInp = msm.readInputDiscretes(slaveId, 0, 8);

                if (digInp != null) {
                    logger.system("Digital Inputs:");
                    for (int i = 0; i < digInp.size(); i++) {
                        logger.system(" %d: %d", i, digInp.getBit(i));
                    }
                    logger.system("Inputs: %s", ModbusUtil.toHex(digInp.getBytes()));
                }
                else {
                    logger.system("Inputs: null");
                    msm.disconnect();
                    System.exit(-1);
                }

                InputRegister[] ai;
                for (int i = 1000; i < 1010; i++) {
                    ai = msm.readInputRegisters(slaveId, i, 1);
                    if (ai != null) {
                        logger.system("Tag %d:", i);
                        for (InputRegister anAi : ai) {
                            logger.system(" %d", anAi.getValue());
                        }
                    }
                    else {
                        logger.system("Tag: %d null", i);
                        msm.disconnect();
                        System.exit(-1);
                    }
                }

                Register[] regs;
                for (int i = 1000; i < 1005; i++) {
                    regs = msm.readMultipleRegisters(slaveId, i, 1);
                    if (regs != null) {
                        logger.system("RWRegisters " + i + " length: " + regs.length);
                        for (Register reg : regs) {
                            logger.system(" %d", reg.getValue());
                        }
                    }
                    else {
                        logger.system("RWRegisters %d: null", i);
                        msm.disconnect();
                        System.exit(-1);
                    }
                }
                regs = msm.readMultipleRegisters(slaveId, 0, 10);
                logger.system("Registers: ");
                if (regs != null) {
                    logger.system("regs :");
                    for (int n = 0; n < regs.length; n++) {
                        logger.system("  %d= %d", n, regs[n]);
                    }
                }
                else {
                    logger.system("Registers: null");
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
            logger.system("SerialFacadeTest driver: %s", e);
            e.printStackTrace();
        }
        if (msm != null) {
            msm.disconnect();
        }
    }
}
