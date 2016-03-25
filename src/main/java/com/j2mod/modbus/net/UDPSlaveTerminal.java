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
import com.j2mod.modbus.util.ModbusLogger;
import com.j2mod.modbus.util.ModbusUtil;

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
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
class UDPSlaveTerminal implements UDPTerminal {

    private static final ModbusLogger logger = ModbusLogger.getLogger(UDPSlaveTerminal.class);
    protected InetAddress m_LocalAddress;
    protected ModbusUDPTransport m_ModbusTransport;
    protected Hashtable<Integer, DatagramPacket> m_Requests = new Hashtable<Integer, DatagramPacket>(342);
    private DatagramSocket m_Socket;
    private boolean m_Active;
    private int m_LocalPort = Modbus.DEFAULT_PORT;
    private LinkedBlockingQueue<byte[]> m_SendQueue = new LinkedBlockingQueue<byte[]>();
    private LinkedBlockingQueue<byte[]> m_ReceiveQueue = new LinkedBlockingQueue<byte[]>();
    private PacketSender m_PacketSender;
    private PacketReceiver m_PacketReceiver;

    /**
     * Creates a slave terminal on the specified adapter address
     * Use 0.0.0.0 to listen on all adapters
     *
     * @param localaddress Local address to bind to
     */
    protected UDPSlaveTerminal(InetAddress localaddress) {
        m_LocalAddress = localaddress;
    }

    /**
     * Gets the local adapter address
     *
     * @return Adapter address
     */
    public InetAddress getLocalAddress() {
        return m_LocalAddress;
    }

    /**
     * Returns the local port the terminal is listening on
     *
     * @return Port number
     */
    public synchronized int getLocalPort() {
        return m_LocalPort;
    }

    /**
     * Sets the local port the terminal is running on
     *
     * @param port Local port
     */
    protected synchronized void setLocalPort(int port) {
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
            logger.debug("UDPSlaveTerminal.activate()");
            if (m_LocalAddress != null && m_LocalPort != -1) {
                m_Socket = new DatagramSocket(m_LocalPort, m_LocalAddress);
            }
            else {
                m_Socket = new DatagramSocket();
                m_LocalPort = m_Socket.getLocalPort();
                m_LocalAddress = m_Socket.getLocalAddress();
            }
            logger.debug("UDPSlaveTerminal::haveSocket():%s", m_Socket.toString());
            logger.debug("UDPSlaveTerminal::addr=:%s:port=%d", m_LocalAddress.toString(), m_LocalPort);

            m_Socket.setReceiveBufferSize(1024);
            m_Socket.setSendBufferSize(1024);

            // Start a sender
            m_PacketSender = new PacketSender(m_Socket);
            new Thread(m_PacketSender).start();
            logger.debug("UDPSlaveTerminal::sender started()");

            // Start a receiver

            m_PacketReceiver = new PacketReceiver(m_Socket);
            new Thread(m_PacketReceiver).start();
            logger.debug("UDPSlaveTerminal::receiver started()");

            // Create a transport to use

            m_ModbusTransport = new ModbusUDPTransport(this);
            logger.debug("UDPSlaveTerminal::transport created");
            m_Active = true;
        }
        logger.debug("UDPSlaveTerminal::activated");
    }

    /**
     * Deactivates this <tt>UDPSlaveTerminal</tt>.
     */
    public synchronized void deactivate() {
        try {
            if (m_Active) {
                // Stop receiver - this will stop and close the socket
                m_PacketReceiver.stop();

                // Stop sender gracefully
                m_PacketSender.stop();

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

    /**
     * Adds the message to the send queue
     *
     * @param msg the message as <tt>byte[]</tt>.
     *
     * @throws Exception
     */
    public void sendMessage(byte[] msg) throws Exception {
        m_SendQueue.add(msg);
    }

    /**
     * Takes a message from the received queue - waits if there is nothing available
     *
     * @return Message from the queue
     *
     * @throws Exception
     */
    public byte[] receiveMessage() throws Exception {
        return m_ReceiveQueue.take();
    }

    /**
     * Sets the timeout in milliseconds for this <tt>UDPSlaveTerminal</tt>.
     *
     * @param timeout the timeout as <tt>int</tt>.
     */
    public synchronized void setTimeout(int timeout) {
        try {
            if (m_Socket != null) {
                m_Socket.setSoTimeout(timeout);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(); // handle? }
        }
    }

    /**
     * The background thread that is responsible for sending messages in response to requests
     */
    class PacketSender implements Runnable {

        private boolean running;
        private boolean closed;
        private Thread thread;
        private DatagramSocket socket;

        /**
         * Constructs a sender with th socket
         *
         * @param socket Socket to use
         */
        public PacketSender(DatagramSocket socket) {
            running = true;
            this.socket = socket;
        }

        /**
         * Stops the sender
         */
        public void stop() {
            running = false;
            thread.interrupt();
            while (!closed) {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    logger.debug("interrupted");
                }
            }
        }

        /**
         * Thread loop that sends messages
         */
        public void run() {
            closed = false;
            thread = Thread.currentThread();
            do {
                try {
                    // Pickup the message and corresponding request
                    byte[] message = m_SendQueue.take();
                    DatagramPacket req = m_Requests.remove(ModbusUtil.registersToInt(message));

                    // Create new Package with corresponding address and port
                    if (req != null) {
                        DatagramPacket res = new DatagramPacket(message, message.length, req.getAddress(), req.getPort());
                        socket.send(res);
                        logger.debug("Sent package from queue");
                    }
                }
                catch (Exception ex) {
                    // Ignore the error if we are no longer listening

                    if (running) {
                        logger.error("Problem reading UDP socket - %s", ex.getMessage());
                    }
                }
            } while (running);
            closed = true;
        }

    }

    /**
     * The background thread that receives messages and adds them to the process list
     * for further analysis
     */
    class PacketReceiver implements Runnable {

        private boolean running;
        private boolean closed;
        private DatagramSocket socket;

        /**
         * A receiver thread for reception of UDP messages
         *
         * @param socket Socket to use
         */
        public PacketReceiver(DatagramSocket socket) {
            running = true;
            this.socket = socket;
        }

        /**
         * Stops the thread
         */
        public void stop() {
            running = false;
            socket.close();
            while (!closed) {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    logger.debug("interrupted");
                }
            }
        }

        /**
         * Background thread for reading UDP messages
         */
        public void run() {
            closed = false;
            do {
                try {
                    // 1. Prepare buffer and receive package
                    byte[] buffer = new byte[256];// max size
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    // 2. Extract TID and remember request
                    Integer tid = ModbusUtil.registersToInt(buffer);
                    m_Requests.put(tid, packet);

                    // 3. place the data buffer in the queue
                    m_ReceiveQueue.put(buffer);
                    logger.debug("Received package to queue");
                }
                catch (Exception ex) {
                    // Ignore the error if we are no longer listening

                    if (running) {
                        logger.error("Problem reading UDP socket - %s", ex.getMessage());
                    }
                }
            } while (running);
            closed = true;
        }
    }
}
