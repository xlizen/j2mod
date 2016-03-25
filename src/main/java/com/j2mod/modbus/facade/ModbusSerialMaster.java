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

import com.j2mod.modbus.net.SerialConnection;
import com.j2mod.modbus.util.Logger;
import com.j2mod.modbus.util.SerialParameters;

/**
 * Modbus/Serial Master facade.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class ModbusSerialMaster extends AbstractModbusMaster {

    private static final Logger logger = Logger.getLogger(ModbusSerialMaster.class);

    private SerialConnection m_Connection;

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param param SerialParameters specifies the serial port parameters to use
     *              to communicate with the slave device network.
     */
    public ModbusSerialMaster(SerialParameters param) {
        try {
            m_Connection = new SerialConnection(param);
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Connects this <tt>ModbusSerialMaster</tt> with the slave.
     *
     * @throws Exception if the connection cannot be established.
     */
    public synchronized void connect() throws Exception {
        if (m_Connection != null && !m_Connection.isOpen()) {
            m_Connection.open();
            m_Transaction = m_Connection.getModbusTransport().createTransaction();
            setTransaction(m_Transaction);
        }
    }

    /**
     * Disconnects this <tt>ModbusSerialMaster</tt> from the slave.
     */
    public synchronized void disconnect() {
        if (m_Connection != null && m_Connection.isOpen()) {
            m_Connection.close();
            m_Transaction = null;
            setTransaction(null);
        }
    }

}