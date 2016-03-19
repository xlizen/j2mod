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
package com.j2mod.modbus.net;

import com.fazecast.jSerialComm.SerialPort;
import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.io.ModbusRTUTransport;
import com.j2mod.modbus.io.ModbusTCPTransport;
import com.j2mod.modbus.io.ModbusTransport;
import com.j2mod.modbus.util.Logger;
import com.j2mod.modbus.util.SerialParameters;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Create a <tt>ModbusListener</tt> from an URI-like specifier.
 *
 * @author Julie
 */
public class ModbusMasterFactory {

    private static final Logger logger = Logger.getLogger(ModbusMasterFactory.class);

    public static ModbusTransport createModbusMaster(String address) {
        String parts[] = address.split(" *: *");
        if (parts.length < 2) {
            throw new IllegalArgumentException("missing connection information");
        }

        if (parts[0].equalsIgnoreCase("device")) {
            /*
             * Create a ModbusSerialListener with the default Modbus values of
             * 19200 baud, no parity, using the specified device. If there is an
             * additional part after the device name, it will be used as the
             * Modbus unit number.
             */
            SerialParameters parms = new SerialParameters();
            parms.setPortName(parts[1]);
            parms.setBaudRate(19200);
            parms.setDatabits(8);
            parms.setEcho(false);
            parms.setParity(SerialPort.NO_PARITY);
            parms.setFlowControlIn(SerialPort.FLOW_CONTROL_DISABLED);
            try {
                ModbusRTUTransport transport = new ModbusRTUTransport();
                transport.setCommPort(SerialPort.getCommPort(parms.getPortName()));
                transport.setEcho(false);
                return transport;
            }
            catch (IOException e) {
                return null;
            }
        }
        else if (parts[0].equalsIgnoreCase("tcp")) {
            /*
             * Create a ModbusTCPListener with the default interface value. The
             * second optional value is the TCP port number and the third
             * optional value is the Modbus unit number.
             */
            String hostName = parts[1];
            int port = Modbus.DEFAULT_PORT;

            if (parts.length > 2) {
                port = Integer.parseInt(parts[2]);
            }

            try {
                Socket socket = new Socket(hostName, port);
                logger.debug("connecting to " + socket);

                return new ModbusTCPTransport(socket);
            }
            catch (UnknownHostException x) {
                return null;
            }
            catch (IOException e) {
                return null;
            }
        }
        else if (parts[0].equalsIgnoreCase("udp")) {
            /*
             * Create a ModbusUDPListener with the default interface value. The
             * second optional value is the TCP port number and the third
             * optional value is the Modbus unit number.
             */
            String hostName = parts[1];
            int port = Modbus.DEFAULT_PORT;

            if (parts.length > 2) {
                port = Integer.parseInt(parts[2]);
            }

            UDPMasterTerminal terminal;
            try {
                terminal = new UDPMasterTerminal(InetAddress.getByName(hostName));
                terminal.setRemotePort(port);
                terminal.activate();
            }
            catch (UnknownHostException e) {
                e.printStackTrace();
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return terminal.getModbusTransport();
        }
        else {
            throw new IllegalArgumentException("unknown type " + parts[0]);
        }
    }
}
