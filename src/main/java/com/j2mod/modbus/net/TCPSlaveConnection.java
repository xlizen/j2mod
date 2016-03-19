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

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.io.ModbusTCPTransport;
import com.j2mod.modbus.io.ModbusTransport;
import com.j2mod.modbus.util.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Class that implements a TCPSlaveConnection.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class TCPSlaveConnection {

    private static final Logger logger = Logger.getLogger(TCPSlaveConnection.class);

    // instance attributes
    private Socket m_Socket;
    private int m_Unit = 0;
    private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean m_Connected;
    private ModbusTCPTransport m_ModbusTransport;

    /**
     * Constructs a <tt>TCPSlaveConnection</tt> instance using a given socket
     * instance.
     *
     * @param socket the socket instance to be used for communication.
     */
    public TCPSlaveConnection(Socket socket) {
        try {
            setSocket(socket);
        }
        catch (IOException ex) {
            logger.debug("TCPSlaveConnection::Socket invalid");

            throw new IllegalStateException("Socket invalid");
        }
    }

    /**
     * Constructs a <tt>TCPSlaveConnection</tt> instance using a given socket
     * instance.
     *
     * @param socket the socket instance to be used for communication.
     * @param unit   the unit number for this slave connection.
     */
    public TCPSlaveConnection(Socket socket, int unit) {
        m_Unit = unit;

        try {
            setSocket(socket);
        }
        catch (IOException ex) {
            logger.debug("TCPSlaveConnection::Socket invalid");

            throw new IllegalStateException("Socket invalid");
        }
    }

    /**
     * Closes this <tt>TCPSlaveConnection</tt>.
     */
    public void close() {
        if (m_Connected) {
            try {
                m_ModbusTransport.close();
                m_Socket.close();
            }
            catch (IOException ex) {
                logger.debug(ex);
            }
            m_Connected = false;
        }
    }

    /**
     * Returns the <tt>ModbusTransport</tt> associated with this
     * <tt>TCPMasterConnection</tt>.
     *
     * @return the connection's <tt>ModbusTransport</tt>.
     */
    public ModbusTransport getModbusTransport() {
        return m_ModbusTransport;
    }

    /**
     * Prepares the associated <tt>ModbusTransport</tt> of this
     * <tt>TCPMasterConnection</tt> for use.
     *
     * @param socket the socket to be used for communication.
     *
     * @throws IOException if an I/O related error occurs.
     */
    private void setSocket(Socket socket) throws IOException {
        m_Socket = socket;

        if (m_ModbusTransport == null) {
            m_ModbusTransport = new ModbusTCPTransport(m_Socket);
        }
        else {
            m_ModbusTransport.setSocket(m_Socket);
        }

        m_Connected = true;
    }

    /**
     * Returns the timeout for this <tt>TCPSlaveConnection</tt>.
     *
     * @return the timeout as <tt>int</tt>.
     */
    public int getTimeout() {
        return m_Timeout;
    }

    /**
     * Sets the timeout for this <tt>TCPSlaveConnection</tt>.
     *
     * @param timeout the timeout in milliseconds as <tt>int</tt>.
     */
    public void setTimeout(int timeout) {
        m_Timeout = timeout;

        try {
            m_Socket.setSoTimeout(m_Timeout);
        }
        catch (IOException ex) {
            // handle?
        }
    }

    /**
     * Returns the destination port of this <tt>TCPSlaveConnection</tt>.
     *
     * @return the port number as <tt>int</tt>.
     */
    public int getPort() {
        return m_Socket.getLocalPort();
    }

    /**
     * Returns the destination <tt>InetAddress</tt> of this
     * <tt>TCPSlaveConnection</tt>.
     *
     * @return the destination address as <tt>InetAddress</tt>.
     */
    public InetAddress getAddress() {
        return m_Socket.getLocalAddress();
    }

    /**
     * Returns the slave unit number for this connection. A unit number of 0
     * means to accept all unit numbers, while a non-zero unit number means only
     * to accept requests for that specific unit.
     *
     * @return unit number
     */
    public int getUnitNumber() {
        return m_Unit;
    }

    /**
     * Tests if this <tt>TCPSlaveConnection</tt> is connected.
     *
     * @return <tt>true</tt> if connected, <tt>false</tt> otherwise.
     */
    public boolean isConnected() {
        return m_Connected;
    }
}