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
import com.j2mod.modbus.io.ModbusUDPTransport;
import com.j2mod.modbus.util.Logger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Class implementing a <tt>UDPMasterTerminal</tt>.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
class UDPMasterTerminal implements UDPTerminal {

    private static final Logger logger = Logger.getLogger(UDPMasterTerminal.class);
    protected InetAddress m_LocalAddress;
    protected InetAddress m_RemoteAddress;
    protected ModbusUDPTransport m_ModbusTransport;
    private DatagramSocket m_Socket;
    private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean m_Active;
    private int m_RemotePort = Modbus.DEFAULT_PORT;
    private int m_LocalPort = Modbus.DEFAULT_PORT;

    /**
     * Create a UDP master connection to the specified Internet address.
     *
     * @param addr
     */
    protected UDPMasterTerminal(InetAddress addr) {
        m_RemoteAddress = addr;
    }

    /**
     * Create an uninitialized UDP master connection.
     */
    public UDPMasterTerminal() {
    }

    public InetAddress getLocalAddress() {
        return m_LocalAddress;
    }

    public void setLocalAddress(InetAddress addr) {
        m_LocalAddress = addr;
    }

    public int getLocalPort() {
        return m_LocalPort;
    }

    protected void setLocalPort(int port) {
        m_LocalPort = port;
    }

    /**
     * Tests if this <tt>UDPSlaveTerminal</tt> is active.
     *
     * @return <tt>true</tt> if active, <tt>false</tt> otherwise.
     */
    public boolean isActive() {
        return m_Active;
    }

    /**
     * Activate this <tt>UDPTerminal</tt>.
     *
     * @throws Exception if there is a network failure.
     */
    public synchronized void activate() throws Exception {
        if (!isActive()) {
            logger.debug("UDPMasterTerminal::activate()::laddr=:%s:lport:%d",  m_LocalAddress,  m_LocalPort);

            if (m_Socket == null) {
                if (m_LocalAddress != null && m_LocalPort != -1) {
                    m_Socket = new DatagramSocket(m_LocalPort, m_LocalAddress);
                }
                else {
                    m_Socket = new DatagramSocket();
                    m_LocalPort = m_Socket.getLocalPort();
                    m_LocalAddress = m_Socket.getLocalAddress();
                }
            }
            logger.debug("UDPMasterTerminal::haveSocket():%s", m_Socket.toString());
            logger.debug("UDPMasterTerminal::laddr=:%s:lport:%d", m_LocalAddress.toString(), m_LocalPort);
            logger.debug("UDPMasterTerminal::raddr=:%s:rport:%d", m_RemoteAddress.toString(), m_RemotePort);

            m_Socket.setReceiveBufferSize(1024);
            m_Socket.setSendBufferSize(1024);

            m_ModbusTransport = new ModbusUDPTransport(this);
            m_Active = true;
        }
        logger.debug("UDPMasterTerminal::activated");
    }

    /**
     * Deactivates this <tt>UDPSlaveTerminal</tt>.
     */
    public void deactivate() {
        try {
            logger.debug("UDPMasterTerminal::deactivate()");

            m_Socket.close();
            m_ModbusTransport = null;
            m_Active = false;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the <tt>ModbusTransport</tt> associated with this
     * <tt>UDPMasterTerminal</tt>.
     *
     * @return the connection's <tt>ModbusTransport</tt>.
     */
    public ModbusUDPTransport getModbusTransport() {
        return m_ModbusTransport;
    }

    public void sendMessage(byte[] msg) throws Exception {

        DatagramPacket req = new DatagramPacket(msg, msg.length, m_RemoteAddress, m_RemotePort);
        synchronized (m_Socket) {
            m_Socket.send(req);
        }
    }

    public byte[] receiveMessage() throws Exception {

		/*
         * The longest possible DatagramPacket is 256 bytes (Modbus message
		 * limit) plus the 6 byte header.
		 */
        byte[] buffer = new byte[262];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        synchronized (m_Socket) {
            m_Socket.setSoTimeout(m_Timeout);
            m_Socket.receive(packet);
        }
        return buffer;
    }

    /**
     * Returns the destination port of this <tt>UDPSlaveTerminal</tt>.
     *
     * @return the port number as <tt>int</tt>.
     */
    public int getRemotePort() {
        return m_RemotePort;
    }

    /**
     * Sets the destination port of this <tt>UDPSlaveTerminal</tt>. The default
     * is defined as <tt>Modbus.DEFAULT_PORT</tt>.
     *
     * @param port the port number as <tt>int</tt>.
     */
    public void setRemotePort(int port) {
        m_RemotePort = port;
    }

    /**
     * Returns the destination <tt>InetAddress</tt> of this
     * <tt>UDPSlaveTerminal</tt>.
     *
     * @return the destination address as <tt>InetAddress</tt>.
     */
    public InetAddress getRemoteAddress() {
        return m_RemoteAddress;
    }

    /**
     * Sets the destination <tt>InetAddress</tt> of this
     * <tt>UDPSlaveTerminal</tt>.
     *
     * @param adr the destination address as <tt>InetAddress</tt>.
     */
    public void setRemoteAddress(InetAddress adr) {
        m_RemoteAddress = adr;
    }

    /**
     * Returns the timeout for this <tt>UDPMasterTerminal</tt>.
     *
     * @return the timeout as <tt>int</tt>.
     */
    public int getTimeout() {
        return m_Timeout;
    }

    /**
     * Sets the timeout for this <tt>UDPMasterTerminal</tt>.
     *
     * @param timeout the timeout as <tt>int</tt>.
     */
    public void setTimeout(int timeout) {
        m_Timeout = timeout;
    }

    public void receiveMessage(byte[] buffer) throws Exception {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        synchronized (m_Socket) {
            m_Socket.setSoTimeout(m_Timeout);
            m_Socket.receive(packet);
        }
    }
}
