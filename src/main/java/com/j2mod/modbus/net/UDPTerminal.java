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
package com.j2mod.modbus.net;

import com.j2mod.modbus.io.ModbusUDPTransport;

import java.net.InetAddress;

/**
 * Interface defining a <tt>UDPTerminal</tt>.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface UDPTerminal {

    /**
     * Returns the local address of this <tt>UDPTerminal</tt>.
     *
     * @return an <tt>InetAddress</tt> instance.
     */
    InetAddress getLocalAddress();

    /**
     * Returns the local port of this <tt>UDPTerminal</tt>.
     *
     * @return the local port as <tt>int</tt>.
     */
    int getLocalPort();

    /**
     * Tests if this <tt>UDPTerminal</tt> is active.
     *
     * @return <tt>true</tt> if active, <tt>false</tt> otherwise.
     */
    boolean isActive();

    /**
     * Activate this <tt>UDPTerminal</tt>.
     *
     * @throws java.lang.Exception if there is a network failure.
     */
    void activate() throws Exception;

    /**
     * Deactivates this <tt>UDPTerminal</tt>.
     */
    void deactivate();

    /**
     * Returns the <tt>ModbusTransport</tt> associated with this
     * <tt>UDPTerminal</tt>.
     *
     * @return a <tt>ModbusTransport</tt> instance.
     */
    ModbusUDPTransport getModbusTransport();

    /**
     * Sends the given message.
     *
     * @param msg the message as <tt>byte[]</tt>.
     *
     * @throws Exception if sending the message fails.
     */
    void sendMessage(byte[] msg) throws Exception;

    /**
     * Receives and returns a message.
     *
     * @return the message as a newly allocated <tt>byte[]</tt>.
     *
     * @throws Exception if receiving a message fails.
     */
    byte[] receiveMessage() throws Exception;

}