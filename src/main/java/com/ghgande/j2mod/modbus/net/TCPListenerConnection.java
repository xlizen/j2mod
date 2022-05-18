package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.io.ModbusRTUTCPServerTransport;
import com.ghgande.j2mod.modbus.io.ModbusTCPSeverTransport;
import com.ghgande.j2mod.modbus.util.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPListenerConnection extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(TCPListenerConnection.class);

    private ServerSocket serverSocket = null;

    // instance attributes
    private Socket socket;
    private int timeout = Modbus.DEFAULT_TIMEOUT;
    protected boolean listening;
    private String error;


    private InetAddress address;
    private int port = Modbus.DEFAULT_PORT;

    private ModbusTCPSeverTransport transport;

    private boolean useRtuOverTcp = false;

    private Thread listener = new Thread(() -> {
        try {
            while (listening) {
                Socket accept = serverSocket.accept();
                logger.debug("Making new connection {}", socket);
                if (listening) {
                    if (null != socket) {
                        socket.close();
                    }
                    socket = accept;
                    prepareTransport(useRtuOverTcp);
                    connected = true;
                    notifyObservers("new connected");
                } else {
                    socket.close();
                }
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }

    });

    private boolean connected;


    public TCPListenerConnection(InetAddress adr) {
        address = adr;
    }


    public void listener(boolean useRtuOverTcp) {
        try {
            serverSocket = new ServerSocket(port, 1, address);
            // serverSocket.setSoTimeout(timeout);
            logger.debug("Listening to {} (Port {})", serverSocket, port);
        } catch (Exception e) {
            error = String.format("Cannot start TCP listener on port %d - %s", port, e.getMessage());
            listening = false;
            return;
        }
        listening = true;
        listener.start();
    }

    /**
     * Prepares the associated <tt>ModbusTransport</tt> of this
     * <tt>TCPMasterConnection</tt> for use.
     *
     * @param useRtuOverTcp True if the RTU protocol should be used over TCP
     * @throws IOException if an I/O related error occurs.
     */
    private void prepareTransport(boolean useRtuOverTcp) throws IOException {

        // If we don't have a transport, or the transport type has changed
        if (transport == null || (this.useRtuOverTcp != useRtuOverTcp)) {

            // Save the flag to tell us which transport type to use
            this.useRtuOverTcp = useRtuOverTcp;

            // Select the correct transport
            if (useRtuOverTcp) {
                logger.trace("prepareTransport() -> using RTU over TCP transport.");
                transport = new ModbusRTUTCPServerTransport(socket);
                transport.setMaster(this);
            } else {
                logger.trace("prepareTransport() -> using standard TCP transport.");
                transport = new ModbusTCPSeverTransport(socket);
                transport.setMaster(this);
            }
        } else {
            logger.trace("prepareTransport() -> using custom transport: {}", transport.getClass().getSimpleName());
            transport.setSocket(socket);
        }
        transport.setTimeout(timeout);
    }

    /**
     * Returns the destination port of this <tt>TCPMasterConnection</tt>.
     *
     * @return the port number as <tt>int</tt>.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the destination port of this <tt>TCPMasterConnection</tt>. The
     * default is defined as <tt>Modbus.DEFAULT_PORT</tt>.
     *
     * @param port the port number as <tt>int</tt>.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns the destination <tt>InetAddress</tt> of this
     * <tt>TCPMasterConnection</tt>.
     *
     * @return the destination address as <tt>InetAddress</tt>.
     */
    public InetAddress getAddress() {
        return address;
    }

    /**
     * Sets the destination <tt>InetAddress</tt> of this
     * <tt>TCPMasterConnection</tt>.
     *
     * @param adr the destination address as <tt>InetAddress</tt>.
     */
    public void setAddress(InetAddress adr) {
        address = adr;
    }

    /**
     * Returns the <tt>ModbusTransport</tt> associated with this
     * <tt>TCPMasterConnection</tt>.
     *
     * @return the connection's <tt>ModbusTransport</tt>.
     */
    public AbstractModbusTransport getModbusTransport() {
        return transport;
    }

    /**
     * Set the <tt>ModbusTransport</tt> associated with this
     * <tt>TCPMasterConnection</tt>
     *
     * @param trans associated transport
     */
    public void setModbusTransport(ModbusTCPSeverTransport trans) {
        transport = trans;
    }


    /**
     * Tests if this <tt>TCPMasterConnection</tt> is connected.
     *
     * @return <tt>true</tt> if connected, <tt>false</tt> otherwise.
     */
    public synchronized boolean isConnected() {
        if (connected && socket != null) {
            if (!socket.isConnected() || socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Socket exception", e);
                } finally {
                    connected = false;
                }
            }
        }
        return connected;
    }

    /**
     * Closes this <tt>TCPMasterConnection</tt>.
     */
    public void close() {
        if (connected) {
            try {
                transport.close();
            } catch (IOException ex) {
                logger.debug("close()", ex);
            } finally {
                connected = false;
            }
        }
    }

    /**
     * Returns the timeout (msec) for this <tt>TCPMasterConnection</tt>.
     *
     * @return the timeout as <tt>int</tt>.
     */
    public synchronized int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout (msec) for this <tt>TCPMasterConnection</tt>. This is both the
     * connection timeout and the transaction timeout
     *
     * @param timeout - the timeout in milliseconds as an <tt>int</tt>.
     */
    public synchronized void setTimeout(int timeout) {
        try {
            this.timeout = timeout;
            if (socket != null) {
                socket.setSoTimeout(timeout);
            }
        } catch (IOException ex) {
            logger.warn("Could not set timeout to value {}", timeout, ex);
        }
    }
}
