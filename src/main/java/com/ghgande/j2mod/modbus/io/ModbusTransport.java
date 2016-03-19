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
package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;

import java.io.IOException;

/**
 * Interface defining the I/O mechanisms for
 * <tt>ModbusMessage</tt> instances.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface ModbusTransport {

    /**
     * Closes the raw input and output streams of
     * this <tt>ModbusTransport</tt>.
     * <p>
     *
     * @throws IOException if a stream
     *                     cannot be closed properly.
     */
    void close() throws IOException;

    /**
     * Creates a Modbus transaction for the underlying transport.
     *
     * @return the new transaction
     */
    ModbusTransaction createTransaction();

    /**
     * Writes a <tt<ModbusMessage</tt> to the
     * output stream of this <tt>ModbusTransport</tt>.
     * <p>
     *
     * @param msg a <tt>ModbusMessage</tt>.
     *
     * @throws ModbusIOException data cannot be
     *                           written properly to the raw output stream of
     *                           this <tt>ModbusTransport</tt>.
     */
    void writeMessage(ModbusMessage msg) throws ModbusIOException;

    /**
     * Reads a <tt>ModbusRequest</tt> from the
     * input stream of this <tt>ModbusTransport<tt>.
     * <p>
     *
     * @return req the <tt>ModbusRequest</tt> read from the underlying stream.
     *
     * @throws ModbusIOException data cannot be
     *                           read properly from the raw input stream of
     *                           this <tt>ModbusTransport</tt>.
     */
    ModbusRequest readRequest() throws ModbusIOException;

    /**
     * Reads a <tt>ModbusResponse</tt> from the
     * input stream of this <tt>ModbusTransport<tt>.
     * <p>
     *
     * @return res the <tt>ModbusResponse</tt> read from the underlying stream.
     *
     * @throws ModbusIOException data cannot be
     *                           read properly from the raw input stream of
     *                           this <tt>ModbusTransport</tt>.
     */
    ModbusResponse readResponse() throws ModbusIOException;

}