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
package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusUDPTransport;
import com.ghgande.j2mod.modbus.util.ModbusLogger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Class implementing a <tt>UDPMasterTerminal</tt>.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
class UDPMasterTerminal implements UDPTerminal {

    private static final ModbusLogger logger = ModbusLogger.getLogger(UDPMasterTerminal.class);
    protected InetAddress remoteAddress;
    protected ModbusUDPTransport transport;
    private DatagramSocket socket;
    private int timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean active;
    private int remotePort = Modbus.DEFAULT_PORT;

    /**
     * Create a UDP master connection to the specified Internet address.
     *
     * @param addr Remote address to connect to
     */
    protected UDPMasterTerminal(InetAddress addr) {
        remoteAddress = addr;
    }

    /**
     * Create an uninitialized UDP master connection.
     */
    public UDPMasterTerminal() {
    }

    /**
     * Tests if this <tt>UDPSlaveTerminal</tt> is active.
     *
     * @return <tt>true</tt> if active, <tt>false</tt> otherwise.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Activate this <tt>UDPTerminal</tt>.
     *
     * @throws Exception if there is a network failure.
     */
    public synchronized void activate() throws Exception {
        if (!isActive()) {

            if (socket == null) {
                socket = new DatagramSocket();
            }
            logger.debug("UDPMasterTerminal::haveSocket():%s", socket.toString());
            logger.debug("UDPMasterTerminal::raddr=:%s:rport:%d", remoteAddress.toString(), remotePort);

            socket.setReceiveBufferSize(1024);
            socket.setSendBufferSize(1024);

            transport = new ModbusUDPTransport(this);
            active = true;
        }
        logger.debug("UDPMasterTerminal::activated");
    }

    /**
     * Deactivates this <tt>UDPSlaveTerminal</tt>.
     */
    public synchronized void deactivate() {
        try {
            logger.debug("UDPMasterTerminal::deactivate()");
            if (socket != null) {
                socket.close();
            }
            transport = null;
            active = false;
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
        return transport;
    }

    /**
     * Sends a message to the remote address and port
     *
     * @param msg the message as <tt>byte[]</tt>.
     *
     * @throws Exception
     */
    public synchronized void sendMessage(byte[] msg) throws Exception {
        DatagramPacket req = new DatagramPacket(msg, msg.length, remoteAddress, remotePort);
        socket.send(req);
    }

    public synchronized byte[] receiveMessage() throws Exception {

        // The longest possible DatagramPacket is 256 bytes (Modbus message
        // limit) plus the 6 byte header.
        byte[] buffer = new byte[262];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.setSoTimeout(timeout);
        socket.receive(packet);
        return buffer;
    }

    /**
     * Returns the destination port of this <tt>UDPSlaveTerminal</tt>.
     *
     * @return the port number as <tt>int</tt>.
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * Sets the destination port of this <tt>UDPSlaveTerminal</tt>. The default
     * is defined as <tt>Modbus.DEFAULT_PORT</tt>.
     *
     * @param port the port number as <tt>int</tt>.
     */
    public void setRemotePort(int port) {
        remotePort = port;
    }

    /**
     * Returns the destination <tt>InetAddress</tt> of this
     * <tt>UDPSlaveTerminal</tt>.
     *
     * @return the destination address as <tt>InetAddress</tt>.
     */
    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * Sets the destination <tt>InetAddress</tt> of this
     * <tt>UDPSlaveTerminal</tt>.
     *
     * @param adr the destination address as <tt>InetAddress</tt>.
     */
    public void setRemoteAddress(InetAddress adr) {
        remoteAddress = adr;
    }

    /**
     * Returns the timeout for this <tt>UDPMasterTerminal</tt>.
     *
     * @return the timeout as <tt>int</tt>.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout for this <tt>UDPMasterTerminal</tt>.
     *
     * @param timeout the timeout as <tt>int</tt>.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
