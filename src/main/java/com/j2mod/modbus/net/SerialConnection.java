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

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.io.*;
import com.j2mod.modbus.util.Logger;
import com.j2mod.modbus.util.SerialParameters;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class that implements a serial connection which can be used for master and
 * slave implementations.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class SerialConnection implements SerialPortDataListener {

    private static final Logger logger = Logger.getLogger(SerialConnection.class);

    private SerialParameters m_Parameters;
    private ModbusSerialTransport m_Transport;
    private SerialPort m_SerialPort;
    private InputStream m_SerialIn;

    /**
     * Creates a SerialConnection object and initializes variables passed in as
     * params.
     *
     * @param parameters
     *            A SerialParameters object.
     */
    public SerialConnection(SerialParameters parameters) {
        m_Parameters = parameters;
    }

    /**
     * Returns the <tt>ModbusTransport</tt> instance to be used for receiving
     * and sending messages.
     *
     * @return a <tt>ModbusTransport</tt> instance.
     */
    public ModbusTransport getModbusTransport() {
        return m_Transport;
    }

    /**
     * Opens the communication port.
     *
     * @throws Exception
     *             if an error occurs.
     */
    public void open() throws Exception {
        m_SerialPort = SerialPort.getCommPort(m_Parameters.getPortName());
        m_SerialPort.closePort();
        setConnectionParameters();

        if (Modbus.SERIAL_ENCODING_ASCII.equals(m_Parameters.getEncoding())) {
            m_Transport = new ModbusASCIITransport();
        }
        else if (Modbus.SERIAL_ENCODING_RTU.equals(m_Parameters.getEncoding())) {
            m_Transport = new ModbusRTUTransport();
        }
        else if (Modbus.SERIAL_ENCODING_BIN.equals(m_Parameters.getEncoding())) {
            m_Transport = new ModbusBINTransport();
        }
        m_Transport.setEcho(m_Parameters.isEcho());

        // Open the input and output streams for the connection. If they won't
        // open, close the port before throwing an exception.
        m_Transport.setCommPort(m_SerialPort);

        // Add this object as an event listener for the serial port.
        if (!m_SerialPort.openPort()) {
            close();
            throw new Exception("Error opening i/o streams");
        }
        m_SerialPort.addDataListener(this);
        m_SerialIn = m_SerialPort.getInputStream();
    }

    /**
     * Sets the connection parameters to the setting in the parameters object.
     * If set fails return the parameters object to original settings and throw
     * exception.
     */
    public void setConnectionParameters() {

        // Set connection parameters, if set fails return parameters object
        // to original state.
        m_SerialPort.setComPortParameters(m_Parameters.getBaudRate(), m_Parameters.getDatabits(), m_Parameters.getStopbits(), m_Parameters.getParity());
        m_SerialPort.setFlowControl(m_Parameters.getFlowControlIn() | m_Parameters.getFlowControlOut());
    }

    /**
     * Close the port and clean up associated elements.
     */
    public void close() {
        // Check to make sure sPort has reference to avoid a NPE.
        if (m_SerialPort != null) {
            try {
                m_Transport.close();
                m_SerialIn.close();
            }
            catch (IOException e) {
                logger.debug(e.getMessage());
            }
            finally {
                // Close the port.
                m_SerialPort.closePort();
            }
        }
        m_SerialPort = null;
    }

    /**
     * Reports the open status of the port.
     *
     * @return true if port is open, false if port is closed.
     */
    public boolean isOpen() {
        return m_SerialPort != null;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    public void serialEvent(SerialPortEvent e) {
        // Determine type of event.
        switch (e.getEventType()) {
            case SerialPort.LISTENING_EVENT_DATA_AVAILABLE:
                // This event is ignored, the application reads directly from
                // the serial input stream
                break;
            default:
                logger.debug("Serial port event: %s", e.getEventType());
        }
    }
}
