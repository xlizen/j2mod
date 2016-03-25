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
import com.j2mod.modbus.net.UDPMasterConnection;
import com.j2mod.modbus.util.ModbusLogger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Modbus/UDP Master facade.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class ModbusUDPMaster extends AbstractModbusMaster {

    private static final ModbusLogger logger = ModbusLogger.getLogger(ModbusUDPMaster.class);

    private UDPMasterConnection m_Connection;

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param addr an internet address as resolvable IP name or IP number,
     *             specifying the slave to communicate with.
     */
    public ModbusUDPMaster(String addr) {
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
    public ModbusUDPMaster(String addr, int port) {
        super();
        try {
            InetAddress m_SlaveAddress = InetAddress.getByName(addr);
            m_Connection = new UDPMasterConnection(m_SlaveAddress);
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

}