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
import com.ghgande.j2mod.modbus.io.ModbusTCPTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.util.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Class that implements a TCPMasterConnection.
 *
 * @author Dieter Wimberger
 * @author Julie Haugh
 * @version 1.07 (02/14/2016)
 *          Fix bug that caused m_use_UrgentData to be ignored.
 *          Changed default behavior from always using urgent data to it being
 *          condition based on RFC 6093.
 */
public class TCPMasterConnection {

    private static final Logger logger = Logger.getLogger(TCPMasterConnection.class);

    // instance attributes
    private Socket m_Socket;
    private int m_Timeout = Modbus.DEFAULT_TIMEOUT;
    private boolean m_Connected;

    private InetAddress m_Address;
    private int m_Port = Modbus.DEFAULT_PORT;

    private ModbusTCPTransport m_ModbusTransport;

    /**
     * m_useUrgentData - sent a byte of urgent data when testing the TCP
     * connection.
     */
    private boolean m_useUrgentData = false;

    /**
     * Prepares the associated <tt>ModbusTransport</tt> of this
     * <tt>TCPMasterConnection</tt> for use.
     *
     * @throws IOException if an I/O related error occurs.
     */
    private void prepareTransport() throws IOException {
        if (m_ModbusTransport == null) {
            m_ModbusTransport = new ModbusTCPTransport(m_Socket);
        }
        else {
            m_ModbusTransport.setSocket(m_Socket);
        }
    }

    /**
     * Opens this <tt>TCPMasterConnection</tt>.
     *
     * @throws Exception if there is a network failure.
     */
    public synchronized void connect() throws Exception {
        if (!isConnected()) {
            if (Modbus.debug) {
                System.out.println("connect()");
            }

            m_Socket = new Socket(m_Address, m_Port);
            m_Socket.setReuseAddress(true);
            m_Socket.setSoLinger(true, 1);
            m_Socket.setKeepAlive(true);

            setTimeout(m_Timeout);
            prepareTransport();

            m_Connected = true;
        }
    }

    /**
     * Tests if this <tt>TCPMasterConnection</tt> is connected.
     *
     * @return <tt>true</tt> if connected, <tt>false</tt> otherwise.
     */
    public synchronized boolean isConnected() {
        if (m_Connected && m_Socket != null) {
            if (!m_Socket.isConnected() || m_Socket.isClosed()
                    || m_Socket.isInputShutdown()
                    || m_Socket.isOutputShutdown()) {
                try {
                    m_Socket.close();
                }
                catch (IOException e) {
                    // Blah.
                }
                m_Connected = false;
            }
            else {
                /*
                 * When m_useUrgentData is set, a byte of urgent data
				 * will be sent to the server to test the connection. If
				 * the connection is actually broken, an IException will
				 * occur and the connection will be closed.
				 * 
				 * Note: RFC 6093 has decreed that we stop using urgent
				 * data.
				 */
                if (m_useUrgentData) {
                    try {
                        m_Socket.sendUrgentData(0);
                        try {
                            /*
							 * Snooze briefly so the urgent byte isn't
							 * merged with the next packet.
							 */
                            Thread.sleep(5);
                        }
                        catch (InterruptedException e) {
                            // Do nothing.
                        }
                    }
                    catch (IOException e) {
                        m_Connected = false;
                        try {
                            m_Socket.close();
                        }
                        catch (IOException e1) {
                            // Do nothing.
                        }
                    }
                }
            }
        }
        return m_Connected;
    }

    /**
     * Closes this <tt>TCPMasterConnection</tt>.
     */
    public void close() {
        if (m_Connected) {
            try {
                m_ModbusTransport.close();
            }
            catch (IOException ex) {
                if (Modbus.debug) {
                    System.out.println("close()");
                }
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
     * Set the <tt>ModbusTransport</tt> associated with this
     * <tt>TCPMasterConnection</tt>
     */
    public void setModbusTransport(ModbusTCPTransport trans) {
        m_ModbusTransport = trans;
    }

    /**
     * Returns the timeout for this <tt>TCPMasterConnection</tt>.
     *
     * @return the timeout as <tt>int</tt>.
     */
    public synchronized int getTimeout() {
        return m_Timeout;
    }

    /**
     * Sets the timeout for this <tt>TCPMasterConnection</tt>.
     *
     * @param timeout -
     *                the timeout in milliseconds as an <tt>int</tt>.
     */
    public synchronized void setTimeout(int timeout) {
        try {
            m_Socket.setSoTimeout(m_Timeout);
            m_Timeout = timeout;
        }
        catch (IOException ex) {
            // Do nothing.
        }
    }

    /**
     * Returns the destination port of this <tt>TCPMasterConnection</tt>.
     *
     * @return the port number as <tt>int</tt>.
     */
    public int getPort() {
        return m_Port;
    }

    /**
     * Sets the destination port of this <tt>TCPMasterConnection</tt>. The
     * default is defined as <tt>Modbus.DEFAULT_PORT</tt>.
     *
     * @param port the port number as <tt>int</tt>.
     */
    public void setPort(int port) {
        m_Port = port;
    }

    /**
     * Returns the destination <tt>InetAddress</tt> of this
     * <tt>TCPMasterConnection</tt>.
     *
     * @return the destination address as <tt>InetAddress</tt>.
     */
    public InetAddress getAddress() {
        return m_Address;
    }

    /**
     * Sets the destination <tt>InetAddress</tt> of this
     * <tt>TCPMasterConnection</tt>.
     *
     * @param adr the destination address as <tt>InetAddress</tt>.
     */
    public void setAddress(InetAddress adr) {
        m_Address = adr;
    }

    /**
     * Gets the current setting of the flag which controls sending
     * urgent data to test a network connection.
     *
     * @return Status
     */
    public boolean getUseUrgentData() {
        return m_useUrgentData;
    }

    /**
     * Set the flag which controls sending urgent data to test a
     * network connection.
     *
     * @param useUrgentData - Connections are testing using urgent data.
     */
    public void setUseUrgentData(boolean useUrgentData) {
        m_useUrgentData = useUrgentData;
    }

    /**
     * Constructs a <tt>TCPMasterConnection</tt> instance with a given
     * destination address.
     *
     * @param adr the destination <tt>InetAddress</tt>.
     */
    public TCPMasterConnection(InetAddress adr) {
        m_Address = adr;
    }
}
