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
import com.ghgande.j2mod.modbus.io.ModbusTransport;

import java.net.InetAddress;

/**
 * Class that implements a UDPMasterConnection.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class UDPMasterConnection {

    //instance attributes
    private UDPMasterTerminal m_Terminal;
    private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean m_Connected;

    private InetAddress m_Address;
    private int m_Port = Modbus.DEFAULT_PORT;

    /**
     * Constructs a <tt>UDPMasterConnection</tt> instance
     * with a given destination address.
     *
     * @param adr the destination <tt>InetAddress</tt>.
     */
    public UDPMasterConnection(InetAddress adr) {
        m_Address = adr;
    }

    /**
     * Opens this <tt>UDPMasterConnection</tt>.
     *
     * @throws Exception if there is a network failure.
     */
    public synchronized void connect() throws Exception {
        if (!m_Connected) {
            m_Terminal = new UDPMasterTerminal();
            m_Terminal.setLocalAddress(InetAddress.getLocalHost());
            m_Terminal.setLocalPort(5000);
            m_Terminal.setRemoteAddress(m_Address);
            m_Terminal.setRemotePort(m_Port);
            m_Terminal.setTimeout(m_Timeout);
            m_Terminal.activate();
            m_Connected = true;
        }
    }

    /**
     * Closes this <tt>UDPMasterConnection</tt>.
     */
    public void close() {
        if (m_Connected) {
            try {
                m_Terminal.deactivate();
            }
            catch (Exception ex) {
                if (Modbus.debug) {
                    ex.printStackTrace();
                }
            }
            m_Connected = false;
        }
    }

    /**
     * Returns the <tt>ModbusTransport</tt> associated with this
     * <tt>UDPMasterConnection</tt>.
     *
     * @return the connection's <tt>ModbusTransport</tt>.
     */
    public ModbusTransport getModbusTransport() {
        return m_Terminal.getModbusTransport();
    }

    /**
     * Returns the terminal used for handling the package traffic.
     *
     * @return a <tt>UDPTerminal</tt> instance.
     */
    public UDPTerminal getTerminal() {
        return m_Terminal;
    }

    /**
     * Returns the timeout for this <tt>UDPMasterConnection</tt>.
     *
     * @return the timeout as <tt>int</tt>.
     */
    public synchronized int getTimeout() {
        return m_Timeout;
    }

    /**
     * Sets the timeout for this <tt>UDPMasterConnection</tt>.
     *
     * @param timeout the timeout as <tt>int</tt>.
     */
    public synchronized void setTimeout(int timeout) {
        m_Timeout = timeout;
        m_Terminal.setTimeout(timeout);
    }

    /**
     * Returns the destination port of this
     * <tt>UDPMasterConnection</tt>.
     *
     * @return the port number as <tt>int</tt>.
     */
    public int getPort() {
        return m_Port;
    }

    /**
     * Sets the destination port of this
     * <tt>UDPMasterConnection</tt>.
     * The default is defined as <tt>Modbus.DEFAULT_PORT</tt>.
     *
     * @param port the port number as <tt>int</tt>.
     */
    public void setPort(int port) {
        m_Port = port;
    }

    /**
     * Returns the destination <tt>InetAddress</tt> of this
     * <tt>UDPMasterConnection</tt>.
     *
     * @return the destination address as <tt>InetAddress</tt>.
     */
    public InetAddress getAddress() {
        return m_Address;
    }

    /**
     * Sets the destination <tt>InetAddress</tt> of this
     * <tt>UDPMasterConnection</tt>.
     *
     * @param adr the destination address as <tt>InetAddress</tt>.
     */
    public void setAddress(InetAddress adr) {
        m_Address = adr;
    }

    /**
     * Tests if this <tt>UDPMasterConnection</tt> is connected.
     *
     * @return <tt>true</tt> if connected, <tt>false</tt> otherwise.
     */
    public boolean isConnected() {
        return m_Connected;
    }

}