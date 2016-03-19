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
package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.ModbusUDPTransport;
import com.ghgande.j2mod.modbus.util.Logger;
import com.ghgande.j2mod.modbus.util.ModbusUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class implementing a <tt>UDPSlaveTerminal</tt>.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
class UDPSlaveTerminal implements UDPTerminal {

    private static final Logger logger = Logger.getLogger(UDPSlaveTerminal.class);

    private DatagramSocket m_Socket;
    private boolean m_Active;
    protected InetAddress m_LocalAddress;
    private int m_LocalPort = Modbus.DEFAULT_PORT;
    protected ModbusUDPTransport m_ModbusTransport;

    private LinkedBlockingQueue<byte[]> m_SendQueue;
    private LinkedBlockingQueue<byte[]> m_ReceiveQueue;
    private PacketSender m_PacketSender;
    private PacketReceiver m_PacketReceiver;
    private Thread m_Receiver;
    private Thread m_Sender;

    protected Hashtable<Integer, DatagramPacket> m_Requests;

    protected UDPSlaveTerminal() {
        m_SendQueue = new LinkedBlockingQueue<byte[]>();
        m_ReceiveQueue = new LinkedBlockingQueue<byte[]>();
        m_Requests = new Hashtable<Integer, DatagramPacket>(342);
    }

    protected UDPSlaveTerminal(InetAddress localaddress) {
        m_LocalAddress = localaddress;
        m_SendQueue = new LinkedBlockingQueue<byte[]>();
        m_ReceiveQueue = new LinkedBlockingQueue<byte[]>();
        m_Requests = new Hashtable<Integer, DatagramPacket>(342);
    }

    public InetAddress getLocalAddress() {
        return m_LocalAddress;
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
            if (Modbus.debug) {
                logger.debug("UDPSlaveTerminal.activate()");
            }
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
            if (Modbus.debug) {
                logger.debug("UDPSlaveTerminal::haveSocket():" + m_Socket.toString());
            }
            if (Modbus.debug) {
                logger.debug("UDPSlaveTerminal::addr=:" + m_LocalAddress.toString() + ":port=" + m_LocalPort);
            }

            m_Socket.setReceiveBufferSize(1024);
            m_Socket.setSendBufferSize(1024);
            m_PacketReceiver = new PacketReceiver();
            m_Receiver = new Thread(m_PacketReceiver);
            m_Receiver.start();
            if (Modbus.debug) {
                logger.debug("UDPSlaveTerminal::receiver started()");
            }
            m_PacketSender = new PacketSender();
            m_Sender = new Thread(m_PacketSender);
            m_Sender.start();
            if (Modbus.debug) {
                logger.debug("UDPSlaveTerminal::sender started()");
            }
            m_ModbusTransport = new ModbusUDPTransport(this);
            if (Modbus.debug) {
                logger.debug("UDPSlaveTerminal::transport created");
            }
            m_Active = true;
        }
        if (Modbus.debug) {
            logger.debug("UDPSlaveTerminal::activated");
        }
    }

    /**
     * Deactivates this <tt>UDPSlaveTerminal</tt>.
     */
    public synchronized void deactivate() {
        try {
            if (m_Active) {
                // 1. stop receiver
                m_PacketReceiver.stop();
                m_Receiver.join();
                // 2. stop sender gracefully
                m_PacketSender.stop();
                m_Sender.join();
                // 3. close socket
                m_Socket.close();
                m_ModbusTransport = null;
                m_Active = false;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns the <tt>ModbusTransport</tt> associated with this
     * <tt>TCPMasterConnection</tt>.
     *
     * @return the connection's <tt>ModbusTransport</tt>.
     */
    public ModbusUDPTransport getModbusTransport() {
        return m_ModbusTransport;
    }

    protected boolean hasResponse() {
        return !m_ReceiveQueue.isEmpty();
    }

    /**
     * Sets the timeout in milliseconds for this <tt>UDPSlaveTerminal</tt>.
     *
     * @param timeout the timeout as <tt>int</tt>.
     *
     * @return the timeout as <tt>int</tt>.
     *
     * public int getTimeout() { return m_Timeout; }
     *
     * /** Sets the timeout for this <tt>UDPSlaveTerminal</tt>.
     */
    public void setTimeout(int timeout) {

        try {
            m_Socket.setSoTimeout(timeout);
        }
        catch (IOException ex) {
            ex.printStackTrace(); // handle? }
        }
    }

    /**
     * Returns the socket of this <tt>UDPSlaveTerminal</tt>.
     *
     * @return the socket as <tt>DatagramSocket</tt>.
     */
    public DatagramSocket getSocket() {
        return m_Socket;
    }

    /**
     * Sets the socket of this <tt>UDPTerminal</tt>.
     *
     * @param sock the <tt>DatagramSocket</tt> for this terminal.
     */
    protected void setSocket(DatagramSocket sock) {
        m_Socket = sock;
    }

    public void sendMessage(byte[] msg) throws Exception {
        m_SendQueue.add(msg);
    }

    public byte[] receiveMessage() throws Exception {
        return m_ReceiveQueue.take();
    }

    class PacketSender implements Runnable {

        private boolean m_Continue;

        public PacketSender() {
            m_Continue = true;
        }

        public void run() {
            do {
                try {
                    // 1. pickup the message and corresponding request
                    byte[] message = m_SendQueue.take();
                    DatagramPacket req = m_Requests.remove(ModbusUtil.registersToInt(message));
                    // 2. create new Package with corresponding address and port
                    DatagramPacket res = new DatagramPacket(message, message.length, req.getAddress(), req.getPort());
                    m_Socket.send(res);
                    if (Modbus.debug) {
                        logger.debug("Sent package from queue.");
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } while (m_Continue || !m_SendQueue.isEmpty());
        }

        public void stop() {
            m_Continue = false;
        }

    }

    class PacketReceiver implements Runnable {

        private boolean m_Continue;

        public PacketReceiver() {
            m_Continue = true;
        }

        public void run() {
            do {
                try {
                    // 1. Prepare buffer and receive package
                    byte[] buffer = new byte[256];// max size
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    m_Socket.receive(packet);
                    // 2. Extract TID and remember request
                    Integer tid = ModbusUtil.registersToInt(buffer);
                    m_Requests.put(tid, packet);
                    // 3. place the data buffer in the queue
                    m_ReceiveQueue.put(buffer);
                    if (Modbus.debug) {
                        logger.debug("Received package to queue.");
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } while (m_Continue);
        }

        public void stop() {
            m_Continue = false;
        }
    }
}
