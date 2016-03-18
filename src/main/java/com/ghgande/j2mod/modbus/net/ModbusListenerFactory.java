/***
 * Java Modbus Library (j2mod)
 * Copyright (c) 2002-2004, jamod development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/
package com.ghgande.j2mod.modbus.net;

import com.fazecast.jSerialComm.SerialPort;
import com.ghgande.j2mod.modbus.util.SerialParameters;

/**
 * Create a <tt>ModbusListener</tt> from an URI-like specifier.
 *
 * @author Julie
 */
public class ModbusListenerFactory {
    public static ModbusListener createModbusListener(String address) {
        String parts[] = address.split(" *: *");
        if (parts.length < 2) {
            throw new IllegalArgumentException("missing connection information");
        }

        if (parts[0].toLowerCase().equals("device")) {
            /*
             * Create a ModbusSerialListener with the default Modbus
			 * values of 19200 baud, no parity, using the specified
			 * device.  If there is an additional part after the
			 * device name, it will be used as the Modbus unit number.
			 */
            SerialParameters parms = new SerialParameters();
            parms.setPortName(parts[1]);
            parms.setBaudRate(19200);
            parms.setDatabits(8);
            parms.setEcho(false);
            parms.setParity(SerialPort.NO_PARITY);
            parms.setFlowControlIn(SerialPort.FLOW_CONTROL_DISABLED);

            ModbusSerialListener listener = new ModbusSerialListener(parms);
            if (parts.length > 2) {
                int unit = Integer.parseInt(parts[2]);
                if (unit < 0 || unit > 248) {
                    throw new IllegalArgumentException("illegal unit number");
                }

                listener.setUnit(unit);
            }
            listener.setListening(true);

            Thread result = new Thread(listener);
            result.start();

            return listener;
        }
        else if (parts[0].toLowerCase().equals("tcp")) {
            /*
			 * Create a ModbusTCPListener with the default interface
			 * value.  The second optional value is the TCP port number
			 * and the third optional value is the Modbus unit number.
			 */
            ModbusTCPListener listener = new ModbusTCPListener(5);
            if (parts.length > 2) {
                int port = Integer.parseInt(parts[2]);
                listener.setPort(port);
                if (parts.length > 3) {
                    int unit = Integer.parseInt(parts[3]);
                    listener.setUnit(unit);
                }
            }
            listener.setListening(true);

            Thread result = new Thread(listener);
            result.start();

            return listener;
        }
        else if (parts[0].toLowerCase().equals("udp")) {
			/*
			 * Create a ModbusUDPListener with the default interface
			 * value.  The second optional value is the TCP port number
			 * and the third optional value is the Modbus unit number.
			 */
            ModbusUDPListener listener = new ModbusUDPListener();
            if (parts.length > 2) {
                int port = Integer.parseInt(parts[2]);
                listener.setPort(port);
                if (parts.length > 3) {
                    int unit = Integer.parseInt(parts[3]);
                    listener.setUnit(unit);
                }
            }
            listener.setListening(true);

            Thread result = new Thread(listener);
            result.start();

            return listener;
        }
        else {
            throw new IllegalArgumentException("unknown type " + parts[0]);
        }
    }
}
