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
package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;

/**
 * Class implementing a handler for incoming Modbus/TCP requests.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class TCPConnectionHandler implements Runnable {
    private TCPSlaveConnection m_Connection;
    private ModbusTransport m_Transport;

    /**
     * Constructs a new <tt>TCPConnectionHandler</tt> instance.
     *
     * <p>
     * The connections will be handling using the <tt>ModbusCouple</tt> class
     * and a <tt>ProcessImage</tt> which provides the interface between the
     * slave implementation and the <tt>TCPSlaveConnection</tt>.
     *
     * @param con an incoming connection.
     */
    public TCPConnectionHandler(TCPSlaveConnection con) {
        setConnection(con);
    }

    /**
     * Sets a connection to be handled by this <tt>
     * TCPConnectionHandler</tt>.
     *
     * @param con a <tt>TCPSlaveConnection</tt>.
     */
    public void setConnection(TCPSlaveConnection con) {
        m_Connection = con;
        m_Transport = m_Connection.getModbusTransport();
    }

    public void run() {
        try {
            do {
                // 1. read the request
                ModbusRequest request = m_Transport.readRequest();
                ModbusResponse response;

				/*
                 * test if Process image exists.
				 */
                ProcessImage image = ModbusCoupler.getReference().getProcessImage();
                if (image == null) {
                    /*
                     * Do nothing -- non-existent devices do not respond to
					 * messages.
					 */
                    continue;
                }
                if (image.getUnitID() != 0
                        && request.getUnitID() != image.getUnitID()) {
					/*
					 * Do nothing -- non-existent units do not respond to
					 * message.
					 */
                    continue;
                }

                // 2. create the response.
                response = request.createResponse();

                if (Modbus.debug) {
                    System.out.println("Request:" + request.getHexMessage());
                    System.out.println("Response:" + response.getHexMessage());
                }

                // 3. write the response message.
                m_Transport.writeMessage(response);
            } while (true);
        }
        catch (ModbusIOException ex) {
            if (!ex.isEOF() && Modbus.debug) {
                ex.printStackTrace();
            }
        }
        finally {
            try {
                m_Connection.close();
            }
            catch (Exception ex) {
                // ignore
            }
        }
    }
}