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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for serial <tt>ModbusTransport</tt>
 * implementations.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @version 1.2rc1 (09/11/2004)
 */
abstract public class ModbusSerialTransport implements ModbusTransport {

    private static final Logger logger = Logger.getLogger(ModbusSerialTransport.class);

    protected SerialPort m_CommPort;
    protected boolean m_Echo = false;     // require RS-485 echo processing
    private final Set<ModbusSerialTransportListener> listeners = Collections.synchronizedSet(new HashSet<ModbusSerialTransportListener>());

    /**
     * <code>prepareStreams</code> prepares the input and output streams of this
     * <tt>ModbusSerialTransport</tt> instance.
     *
     * @param in  the input stream to be read from.
     * @param out the output stream to write to.
     *
     * @throws IOException if an I\O error occurs.
     */
    abstract public void prepareStreams(InputStream in, OutputStream out) throws IOException;

    /**
     * The <code>close</code> method closes the serial input/output streams.
     *
     * @throws IOException if an error occurs
     */
    abstract public void close() throws IOException;

    /**
     * The <code>writeMessage</code> method writes a modbus serial message to
     * its serial output stream to a specified slave unit ID.
     *
     * @param msg a <code>ModbusMessage</code> value
     *
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
        ModbusResponse res = readResponseIn();
        notifyListeners(ModbusSerialTransportListener.EventType.AFTER_READ_RESPONSE);
        return res;
    }

    /**
     * The <code>writeMessage</code> method writes a modbus serial message to
     * its serial output stream to a specified slave unit ID.
     *
     * @param msg a <code>ModbusMessage</code> value
     *
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
     *
     * @throws IOException if an I/O related error occurs.
     */
    public void setCommPort(SerialPort cp) throws IOException {
        m_CommPort = cp;
        if (cp != null) {
            if (!m_CommPort.openPort()) {
                throw new IOException(String.format("Cannot open port %s", m_CommPort.getDescriptivePortName()));
            }
            prepareStreams(cp.getInputStream(), cp.getOutputStream());
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
        m_CommPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, ms, ms); /* milliseconds */
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
     *
     * @throws IOException if a I/O error occurred.
     */
    public void readEcho(int len) throws IOException {

        byte echoBuf[] = new byte[len];
        int echoLen = m_CommPort.getInputStream().read(echoBuf, 0, len);
        logger.debug("Echo: %s", ModbusUtil.toHex(echoBuf, 0, echoLen));
        if (echoLen != len) {
            logger.debug("Error: Transmit echo not received");
            throw new IOException("Echo not received");
        }
    }
}
