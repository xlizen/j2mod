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

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.ModbusCoupler;
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.io.ModbusTransport;
import com.j2mod.modbus.io.ModbusUDPTransport;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.msg.ModbusResponse;
import com.j2mod.modbus.util.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class that implements a ModbusUDPListener.<br>
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Julie Haugh
 * @version 0.97 (8/11/2012) Major code cleanup. Change to listen on wildcard
 *          address.
 */
public class ModbusUDPListener implements ModbusListener {

    private static final Logger logger = Logger.getLogger(ModbusUDPListener.class);

    private int m_Port = Modbus.DEFAULT_PORT;
    private boolean m_Listening = false;
    private boolean m_Continue = false;
    private InetAddress m_Interface;
    private UDPSlaveTerminal m_Terminal;
    private int m_Unit = 0;

    /**
     * Create a new <tt>ModbusUDPListener</tt> instance listening to the given
     * interface address.
     *
     * @param ifc
     *            an <tt>InetAddress</tt> instance.
     */
    public ModbusUDPListener(InetAddress ifc) {
        m_Interface = ifc;
        m_Listening = true;
    }

    /**
     * Constructs a new ModbusUDPListener instance. The address will be set to a
     * default value of the wildcard local address and the default Modbus port.
     */
    public ModbusUDPListener() {
        try {
            m_Interface = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
        }
        catch (UnknownHostException e) {
            // Can't happen -- length is fixed by code.
        }
    }

    /**
     * Returns the number of the port this <tt>ModbusUDPListener</tt> is
     * listening to.
     *
     * @return the number of the IP port as <tt>int</tt>.
     */
    public int getPort() {
        return m_Port;
    }

    /**
     * Sets the number of the port this <tt>ModbusUDPListener</tt> is listening
     * to.
     *
     * @param port
     *            the number of the IP port as <tt>int</tt>.
     */
    public void setPort(int port) {
        m_Port = ((port > 0) ? port : Modbus.DEFAULT_PORT);
    }

    /**
     * Starts this <tt>ModbusUDPListener</tt>.
     */
    public void run() {
        ModbusTransport m_Transport;
        try {
            if (m_Interface == null) {
                m_Terminal = new UDPSlaveTerminal(InetAddress.getByName("0.0.0.0"));
            }
            else {
                m_Terminal = new UDPSlaveTerminal(m_Interface);
            }
            m_Terminal.setLocalPort(m_Port);
            m_Terminal.activate();

            m_Transport = new ModbusUDPTransport(m_Terminal);
        }
        catch (Exception e) {
            /*
             * TODO -- Make sure the methods in the try block are throwing
			 * reasonable exemptions and not just "Exception".
			 */
            logger.debug(e);
            m_Listening = false;
            return;
        }

        m_Listening = true;
        m_Continue = true;

        try {
            while (m_Continue) {

				/*
                 * Get the request from the transport. It will be processed
				 * using an associated process image.
				 */
                ModbusRequest request = m_Transport.readRequest();
                ModbusResponse response;

				/*
				 * Make sure there is a process image to handle the request.
				 */
                if (ModbusCoupler.getReference().getProcessImage() == null) {
                    response = request.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
                }
                else {
                    response = request.createResponse();
                }
                logger.debug("Request:%s",  request.getHexMessage());
                logger.debug("Response:%s", response.getHexMessage());
                m_Transport.writeMessage(response);
            }
        }
        catch (ModbusIOException ex) {
            if (!ex.isEOF()) {
                logger.error(ex);
            }
        }
        finally {
            try {
                m_Terminal.deactivate();
                m_Transport.close();
            }
            catch (Exception ex) {
                // ignore
            }
        }
    }

    public int getUnit() {
        return m_Unit;
    }

    public void setUnit(int unit) {
        m_Unit = unit;
    }

    /**
     * Tests if this <tt>ModbusTCPListener</tt> is listening and accepting
     * incoming connections.
     *
     * @return true if listening (and accepting incoming connections), false
     *         otherwise.
     */
    public boolean isListening() {
        return m_Listening;
    }

    /**
     * Sets if this <tt>ModbusUDPListener</tt> is listening and accepting
     * incoming connections.
     *
     * @param listen
     *            true if the <tt>ModbusUDPListener</tt> should listen, false
     *            otherwise.
     */
    public void setListening(boolean listen) {
        m_Listening = listen;
    }

    /**
     * Start the listener thread for this serial interface.
     */
    public Thread listen() {
        m_Listening = true;
        Thread result = new Thread(this);
        result.start();

        return result;
    }

    /**
     * Stops this <tt>ModbusUDPListener</tt>.
     */
    public void stop() {
        m_Terminal.deactivate();
        m_Listening = false;
        m_Continue = false;
    }
}
