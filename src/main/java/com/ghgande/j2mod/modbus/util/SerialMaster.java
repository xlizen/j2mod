/*
 *
 * Copyright (c) 2020, 4NG and/or its affiliates. All rights reserved.
 * 4NG PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.ghgande.j2mod.modbus.util;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;

/**
 * This class mimics ModPoll for serial connections
 */
public class SerialMaster {

    public static void main(String[] args) {

        String portname = null;
        int parity = AbstractSerialConnection.NO_PARITY;
        int rate = 1000, unit = -1, reference = 0, count = 1, type = 3, baudRate = 9600, dataBits = 8, stopBits = 1;
        boolean continuous = true;

        for (int arg = 0; arg < args.length; arg++) {
            if (args[arg].equals("-a")) {
                unit = Integer.parseInt(args[++arg]);
            }
            else if (args[arg].equals("-r")) {
                reference = Integer.parseInt(args[++arg]);
            }
            else if (args[arg].equals("-c")) {
                count = Integer.parseInt(args[++arg]);
            }
            else if (args[arg].equals("-t")) {
                type = Integer.parseInt(args[++arg]);
            }
            else if (args[arg].equals("-b")) {
                baudRate = Integer.parseInt(args[++arg]);
            }
            else if (args[arg].equals("-d")) {
                dataBits = Integer.parseInt(args[++arg]);
            }
            else if (args[arg].equals("-s")) {
                stopBits = Integer.parseInt(args[++arg]);
            }
            else if (args[arg].equals("-1")) {
                continuous = false;
            }
            else if (args[arg].equals("-l")) {
                rate = Integer.parseInt(args[++arg]);
            }
            else if (args[arg].equals("-p")) {
                String tmp = args[++arg];
                if (tmp.equalsIgnoreCase("none")) {
                    parity = AbstractSerialConnection.NO_PARITY;
                }
                else if (tmp.equalsIgnoreCase("even")) {
                    parity = AbstractSerialConnection.EVEN_PARITY;
                }
                else if (tmp.equalsIgnoreCase("odd")) {
                    parity = AbstractSerialConnection.ODD_PARITY;
                }
            }
            else {
                portname = args[arg];
            }
        }

        if (portname == null) {
            System.out.println("ERROR - you must specify a port name");
        }
        else if (unit < 0 || unit > 255) {
            System.out.println("ERROR - you must specify a valid unit ID (1-255) e.g. -a 49");
        }

        else {
            ModbusSerialMaster master;
            // Create master
            SerialParameters parameters = new SerialParameters();
            parameters.setPortName(portname);
            parameters.setOpenDelay(1000);
            parameters.setEncoding(Modbus.SERIAL_ENCODING_RTU);
            parameters.setStopbits(stopBits);
            parameters.setParity(parity);
            parameters.setBaudRate(baudRate);
            parameters.setDatabits(dataBits);
            parameters.setFlowControlOut(AbstractSerialConnection.FLOW_CONTROL_DISABLED);
            parameters.setFlowControlIn(AbstractSerialConnection.FLOW_CONTROL_DISABLED);
            parameters.setEcho(false);
            parameters.setOpenDelay(0);
            master = new ModbusSerialMaster(parameters);

            boolean first = true;
            while (continuous || first) {
                first = false;
                try {
                    master.connect();

                    System.out.println("-- Polling slave... (Ctrl-C to stop)");
                    if (type == 1) {
                        BitVector vector = master.readInputDiscretes(unit, reference, count);
                        System.out.println(vector.toString());
                    }
                    else if (type == 3) {
                        InputRegister[] regs = master.readInputRegisters(unit, reference, count);
                        int cnt = 0;
                        for (InputRegister reg : regs) {
                            System.out.printf("[%04d]: %s (%d)%n", reference + cnt, ModbusUtil.toHex(reg.toBytes()), reg.toUnsignedShort());
                            cnt++;
                        }
                    }
                    else if (type == 4) {
                        Register[] regs = master.readMultipleRegisters(unit, reference, count);
                        int cnt = 0;
                        for (Register reg : regs) {
                            System.out.printf("[%04d]: %s (%d)%n", reference + cnt, ModbusUtil.toHex(reg.toBytes()), reg.toUnsignedShort());
                            cnt++;
                        }
                    }

                }
                catch (Exception e) {
                    System.out.printf("ERROR - %s%n", e.getMessage());
                }
                finally {
                    master.disconnect();
                }
                if (continuous) {
                    ModbusUtil.sleep(rate);
                }
            }
        }
    }

}
