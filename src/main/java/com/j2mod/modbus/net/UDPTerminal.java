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

import com.j2mod.modbus.io.ModbusUDPTransport;

/**
 * Interface defining a <tt>UDPTerminal</tt>.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public interface UDPTerminal {

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