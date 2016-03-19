/*
 * This file is part of j2mod.
 *
 * j2mod is a fork of the jamod library written by Dieter Wimberger
 * and then further enhanced by Julie Haugh with a new LGPL license
 * and upgraded to Java 1.6
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

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.io.*;
import com.ghgande.j2mod.modbus.util.SerialParameters;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class that implements a serial connection which can be used for master and
 * slave implementations.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @version 1.2rc1 (09/11/2004)
 */
public class SerialConnection implements SerialPortDataListener {

    private SerialParameters m_Parameters;
    private ModbusSerialTransport m_Transport;
    private SerialPort m_SerialPort;
    private InputStream m_SerialIn;

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
                System.err.println(e.getMessage());
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
                if (Modbus.debug) {
                    System.out.println("Serial port event: " + e.getEventType());
                }
        }
    }

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
}
