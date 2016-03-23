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
package com.j2mod.modbus.io;

import com.fazecast.jSerialComm.SerialPort;
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.msg.ModbusMessage;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.msg.ModbusResponse;
import com.j2mod.modbus.util.Logger;
import com.j2mod.modbus.util.ModbusUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for serial <tt>ModbusTransport</tt>
 * implementations.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public abstract class ModbusSerialTransport implements ModbusTransport {

    private static final Logger logger = Logger.getLogger(ModbusSerialTransport.class);

    protected SerialPort m_CommPort;
    protected boolean m_Echo = false;     // require RS-485 echo processing
    private final Set<ModbusSerialTransportListener> listeners = Collections.synchronizedSet(new HashSet<ModbusSerialTransportListener>());
    private int receiveTimeout = 500;

    /**
     * The <code>writeMessage</code> method writes a modbus serial message to
     * its serial output stream to a specified slave unit ID.
     *
     * @param msg a <code>ModbusMessage</code> value
     * @throws ModbusIOException if an error occurs
     */
    public void writeMessage(ModbusMessage msg) throws ModbusIOException {
        notifyListeners(ModbusSerialTransportListener.EventType.BEFORE_WRITE_MESSAGE);
        writeMessageOut(msg);
        notifyListeners(ModbusSerialTransportListener.EventType.AFTER_WRITE_MESSAGE);
    }

    /**
     * The <code>readRequest</code> method listens continuously on the serial
     * input stream for master request messages and replies if the request slave
     * ID matches its own set in ModbusCoupler.getUnitID().
     *
     * @return a <code>ModbusRequest</code> value
     *
     * @throws ModbusIOException if an error occurs
     */
    public ModbusRequest readRequest() throws ModbusIOException {
        notifyListeners(ModbusSerialTransportListener.EventType.BEFORE_READ_REQUEST);
        m_CommPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, receiveTimeout, receiveTimeout);
        ModbusRequest req = readRequestIn();
        notifyListeners(ModbusSerialTransportListener.EventType.AFTER_READ_REQUEST);
        return req;
    }

    /**
     * <code>readResponse</code> reads a response message from the slave
     * responding to a master writeMessage request.
     *
     * @return a <code>ModbusResponse</code> value
     *
     * @throws ModbusIOException if an error occurs
     */
    public ModbusResponse readResponse() throws ModbusIOException {
        notifyListeners(ModbusSerialTransportListener.EventType.BEFORE_READ_RESPONSE);
        m_CommPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, receiveTimeout, receiveTimeout);
        ModbusResponse res = readResponseIn();
        notifyListeners(ModbusSerialTransportListener.EventType.AFTER_READ_RESPONSE);
        return res;
    }

    /**
     * The <code>writeMessage</code> method writes a modbus serial message to
     * its serial output stream to a specified slave unit ID.
     *
     * @param msg a <code>ModbusMessage</code> value
     * @throws ModbusIOException if an error occurs
     */
    abstract protected void writeMessageOut(ModbusMessage msg) throws ModbusIOException;

    /**
     * The <code>readRequest</code> method listens continuously on the serial
     * input stream for master request messages and replies if the request slave
     * ID matches its own set in ModbusCoupler.getUnitID().
     *
     * @return a <code>ModbusRequest</code> value
     *
     * @throws ModbusIOException if an error occurs
     */
    abstract protected ModbusRequest readRequestIn() throws ModbusIOException;

    /**
     * <code>readResponse</code> reads a response message from the slave
     * responding to a master writeMessage request.
     *
     * @return a <code>ModbusResponse</code> value
     *
     * @throws ModbusIOException if an error occurs
     */
    abstract protected ModbusResponse readResponseIn() throws ModbusIOException;

    /**
     * Adds a listener to the transport to be called when an event occurs
     *
     * @param listener Listner callback
     */
    public void addListener(ModbusSerialTransportListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener from the event callback chain
     *
     * @param listener Listener to remove
     */
    public void removeListener(ModbusSerialTransportListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Clears the list of listeners
     */
    public void clearListeners() {
        listeners.clear();
    }

    /**
     * Calls any listeners with the given event and current port
     *
     * @param eventType Event type
     */
    private void notifyListeners(ModbusSerialTransportListener.EventType eventType) {
        synchronized (listeners) {
            for (ModbusSerialTransportListener listener : listeners) {
                listener.event(eventType, m_CommPort);
            }
        }
    }

    /**
     * <code>setCommPort</code> sets the comm port member and prepares the input
     * and output streams to be used for reading from and writing to.
     *
     * @param cp the comm port to read from/write to.
     * @throws IOException if an I/O related error occurs.
     */
    public void setCommPort(SerialPort cp) throws IOException {
        m_CommPort = cp;
        if (cp != null) {
            if (!m_CommPort.openPort()) {
                throw new IOException(String.format("Cannot open port %s", m_CommPort.getDescriptivePortName()));
            }
            setReceiveTimeout(500);
        }
    }

    /**
     * <code>isEcho</code> method returns the output echo state.
     *
     * @return a <code>boolean</code> value
     */
    public boolean isEcho() {
        return m_Echo;
    }

    /**
     * <code>setEcho</code> method sets the output echo state.
     *
     * @param b a <code>boolean</code> value
     */
    public void setEcho(boolean b) {
        this.m_Echo = b;
    }

    /**
     * Describe <code>setReceiveTimeout</code> method here.
     *
     * @param ms an <code>int</code> value
     */
    public void setReceiveTimeout(int ms) {
        receiveTimeout = ms;
    }

    /**
     * <code>setBaudRate</code> - Change the serial port baud rate
     *
     * @param baud - an <code>int</code> value
     */
    public void setBaudRate(int baud) {
        m_CommPort.setBaudRate(baud);
        logger.debug("baud rate is now %d", m_CommPort.getBaudRate());
    }

    /**
     * Reads the own message echo produced in RS485 Echo Mode
     * within the given time frame.
     *
     * @param len is the length of the echo to read.  Timeout will occur if the
     *            echo is not received in the time specified in the SerialConnection.
     * @throws IOException if a I/O error occurred.
     */
    protected void readEcho(int len) throws IOException {

        byte echoBuf[] = new byte[len];
        int echoLen = m_CommPort.readBytes(echoBuf, len);
        logger.debug("Echo: %s", ModbusUtil.toHex(echoBuf, 0, echoLen));
        if (echoLen != len) {
            logger.debug("Error: Transmit echo not received");
            throw new IOException("Echo not received");
        }
    }

    /**
     * Reads a byte from the comms port
     *
     * @return Value of the byte
     *
     * @throws IOException If it cannot read or times out
     */
    protected int readByte() throws IOException {
        if (m_CommPort != null && m_CommPort.isOpen()) {
            byte[] buffer = new byte[1];
            int cnt = m_CommPort.readBytes(buffer, 1);
            if (cnt != 1) {
                throw new IOException("Cannot read from serial port");
            }
            else {
                return buffer[0];
            }
        }
        else {
            throw new IOException("Comm port is not valid or not open");
        }
    }

    /**
     * Reads the specified number of bytes from the input stream
     *
     * @param buffer      Buffer to put data into
     * @param bytesToRead Number of bytes to read
     * @throws IOException If the port is invalid or if the number of bytes returned is not equal to that asked for
     */
    protected void readBytes(byte[] buffer, long bytesToRead) throws IOException {
        if (m_CommPort != null && m_CommPort.isOpen()) {
            int cnt = m_CommPort.readBytes(buffer, bytesToRead);
            if (cnt != 1) {
                throw new IOException("Cannot read from serial port");
            }
        }
        else {
            throw new IOException("Comm port is not valid or not open");
        }
    }

    /**
     * Writes the bytes to the output stream
     * @param buffer Buffer to write
     * @param bytesToWrite Number of bytes to write
     * @return Number of bytes written
     */
    public final int writeBytes(byte[] buffer, long bytesToWrite) throws IOException {
        if (m_CommPort != null && m_CommPort.isOpen()) {
            return m_CommPort.writeBytes(buffer, bytesToWrite);
        }
        else {
            throw new IOException("Comm port is not valid or not open");
        }
    }

    /**
     * clearInput - Clear the input if characters are found in the input stream.
     *
     * @throws IOException
     */
    public void clearInput() throws IOException {
        if (m_CommPort.bytesAvailable() > 0) {
            int len = m_CommPort.bytesAvailable();
            byte buf[] = new byte[len];
            readBytes(buf, len);
            logger.debug("Clear input: %s", ModbusUtil.toHex(buf, 0, len));
        }
    }

    /**
     * Closes the comms port and any streams associated with it
     *
     * @throws IOException
     */
    public void close() throws IOException {
        m_CommPort.closePort();
    }

}
