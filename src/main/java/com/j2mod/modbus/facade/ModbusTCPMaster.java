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
package com.j2mod.modbus.facade;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.io.ModbusTCPTransaction;
import com.j2mod.modbus.net.TCPMasterConnection;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Modbus/TCP Master facade.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class ModbusTCPMaster extends AbstractModbusMaster {

    private TCPMasterConnection m_Connection;
    private boolean m_Reconnecting = false;

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param addr an internet address as resolvable IP name or IP number,
     *             specifying the slave to communicate with.
     */
    public ModbusTCPMaster(String addr) {
        this(addr, Modbus.DEFAULT_PORT);
    }

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param addr an internet address as resolvable IP name or IP number,
     *             specifying the slave to communicate with.
     * @param port the port the slave is listening to.
     */
    public ModbusTCPMaster(String addr, int port) {
        super();
        try {
            InetAddress m_SlaveAddress = InetAddress.getByName(addr);
            m_Connection = new TCPMasterConnection(m_SlaveAddress);
            m_Connection.setPort(port);
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Connects this <tt>ModbusTCPMaster</tt> with the slave.
     *
     * @throws Exception if the connection cannot be established.
     */
    public synchronized void connect() throws Exception {
        if (m_Connection != null && !m_Connection.isConnected()) {
            m_Connection.connect();
            m_Transaction = m_Connection.getModbusTransport().createTransaction();
            ((ModbusTCPTransaction)m_Transaction).setReconnecting(m_Reconnecting);
            setTransaction(m_Transaction);
        }
    }

    /**
     * Disconnects this <tt>ModbusTCPMaster</tt> from the slave.
     */
    public synchronized void disconnect() {
        if (m_Connection != null && m_Connection.isConnected()) {
            m_Connection.close();
            m_Transaction = null;
            setTransaction(null);
        }
    }

    /**
     * Tests if a constant connection is maintained or if a new
     * connection is established for every transaction.
     *
     * @return true if a new connection should be established for each
     * transaction, false otherwise.
     */
    public boolean isReconnecting() {
        return m_Reconnecting;
    }

    /**
     * Sets the flag that specifies whether to maintain a
     * constant connection or reconnect for every transaction.
     *
     * @param b true if a new connection should be established for each
     *          transaction, false otherwise.
     */
    public synchronized void setReconnecting(boolean b) {
        m_Reconnecting = b;
        if (m_Transaction != null) {
            ((ModbusTCPTransaction)m_Transaction).setReconnecting(b);
        }
    }

}