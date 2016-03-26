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
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
class UDPSlaveTerminal implements UDPTerminal {

    private static final ModbusLogger logger = ModbusLogger.getLogger(UDPSlaveTerminal.class);
    protected InetAddress localAddress;
    protected ModbusUDPTransport transport;
    protected Hashtable<Integer, DatagramPacket> requests = new Hashtable<Integer, DatagramPacket>(342);
    private DatagramSocket socket;
    private boolean active;
    private int localPort = Modbus.DEFAULT_PORT;
    private LinkedBlockingQueue<byte[]> sendQueue = new LinkedBlockingQueue<byte[]>();
    private LinkedBlockingQueue<byte[]> receiveQueue = new LinkedBlockingQueue<byte[]>();
    private PacketSender packetSender;
    private PacketReceiver packetReceiver;

    /**
     * Creates a slave terminal on the specified adapter address
     * Use 0.0.0.0 to listen on all adapters
     *
     * @param localaddress Local address to bind to
     */
    protected UDPSlaveTerminal(InetAddress localaddress) {
        localAddress = localaddress;
    }

    /**
     * Gets the local adapter address
     *
     * @return Adapter address
     */
    public InetAddress getLocalAddress() {
        return localAddress;
    }

    /**
     * Returns the local port the terminal is listening on
     *
     * @return Port number
     */
    public synchronized int getLocalPort() {
        return localPort;
    }

    /**
     * Sets the local port the terminal is running on
     *
     * @param port Local port
     */
    protected synchronized void setLocalPort(int port) {
        localPort = port;
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
            logger.debug("UDPSlaveTerminal.activate()");
            if (localAddress != null && localPort != -1) {
                socket = new DatagramSocket(localPort, localAddress);
            }
            else {
                socket = new DatagramSocket();
                localPort = socket.getLocalPort();
                localAddress = socket.getLocalAddress();
            }
            logger.debug("UDPSlaveTerminal::haveSocket():%s", socket.toString());
            logger.debug("UDPSlaveTerminal::addr=:%s:port=%d", localAddress.toString(), localPort);

            socket.setReceiveBufferSize(1024);
            socket.setSendBufferSize(1024);

            // Start a sender
            packetSender = new PacketSender(socket);
            new Thread(packetSender).start();
            logger.debug("UDPSlaveTerminal::sender started()");

            // Start a receiver

            packetReceiver = new PacketReceiver(socket);
            new Thread(packetReceiver).start();
            logger.debug("UDPSlaveTerminal::receiver started()");

            // Create a transport to use

            transport = new ModbusUDPTransport(this);
            logger.debug("UDPSlaveTerminal::transport created");
            active = true;
        }
        logger.debug("UDPSlaveTerminal::activated");
    }

    /**
     * Deactivates this <tt>UDPSlaveTerminal</tt>.
     */
    public synchronized void deactivate() {
        try {
            if (active) {
                // Stop receiver - this will stop and close the socket
                packetReceiver.stop();

                // Stop sender gracefully
                packetSender.stop();

                transport = null;
                active = false;
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
        return transport;
    }

    /**
     * Adds the message to the send queue
     *
     * @param msg the message as <tt>byte[]</tt>.
     *
     * @throws Exception
     */
    public void sendMessage(byte[] msg) throws Exception {
        sendQueue.add(msg);
    }

    /**
     * Takes a message from the received queue - waits if there is nothing available
     *
     * @return Message from the queue
     *
     * @throws Exception
     */
    public byte[] receiveMessage() throws Exception {
        return receiveQueue.take();
    }

    /**
     * Sets the timeout in milliseconds for this <tt>UDPSlaveTerminal</tt>.
     *
     * @param timeout the timeout as <tt>int</tt>.
     */
    public synchronized void setTimeout(int timeout) {
        try {
            if (socket != null) {
                socket.setSoTimeout(timeout);
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
                    byte[] message = sendQueue.take();
                    DatagramPacket req = requests.remove(ModbusUtil.registersToInt(message));

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
                    requests.put(tid, packet);

                    // 3. place the data buffer in the queue
                    receiveQueue.put(buffer);
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
