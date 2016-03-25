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
package com.j2mod.modbus.net;

import com.j2mod.modbus.ModbusCoupler;
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.io.ModbusTransport;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.msg.ModbusResponse;
import com.j2mod.modbus.procimg.ProcessImage;
import com.j2mod.modbus.util.ModbusLogger;

/**
 * Class implementing a handler for incoming Modbus/TCP requests.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class TCPConnectionHandler implements Runnable {

    private static final ModbusLogger logger = ModbusLogger.getLogger(TCPConnectionHandler.class);

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
                if (image.getUnitID() != 0 && request.getUnitID() != image.getUnitID()) {
                    /*
                     * Do nothing -- non-existent units do not respond to
					 * message.
					 */
                    continue;
                }

                // 2. create the response.
                response = request.createResponse();
                logger.debug("Request:%s", request.getHexMessage());
                logger.debug("Response:%s", response.getHexMessage());

                // 3. write the response message.
                m_Transport.writeMessage(response);
            } while (true);
        }
        catch (ModbusIOException ex) {
            if (!ex.isEOF()) {
                logger.error(ex);
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