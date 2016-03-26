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
package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;

/**
 * Interface defining a ModbusTransaction.
 * <p>
 * A transaction is defined by the sequence of
 * sending a request message and receiving a
 * related response message.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public interface ModbusTransaction {

    /**
     * Returns the <tt>ModbusRequest</tt> instance
     * associated with this <tt>ModbusTransaction</tt>.
     * <p>
     *
     * @return the associated <tt>ModbusRequest</tt> instance.
     */
    ModbusRequest getRequest();

    /**
     * Sets the <tt>ModbusRequest</tt> for this
     * <tt>ModbusTransaction</tt>.<p>
     * The related <tt>ModbusResponse</tt> is acquired
     * from the passed in <tt>ModbusRequest</tt> instance.<br>
     * <p>
     *
     * @param req a <tt>ModbusRequest</tt>.
     */
    void setRequest(ModbusRequest req);

    /**
     * Returns the <tt>ModbusResponse</tt> instance
     * associated with this <tt>ModbusTransaction</tt>.
     * <p>
     *
     * @return the associated <tt>ModbusRequest</tt> instance.
     */
    ModbusResponse getResponse();

    /**
     * Returns the actual transaction identifier of
     * this <tt>ModbusTransaction</tt>.
     * The identifier is a 2-byte (short) non negative
     * integer value valid in the range of 0-65535.<br>
     * <p>
     *
     * @return the actual transaction identifier as
     * <tt>int</tt>.
     */
    int getTransactionID();

    /**
     * Returns the amount of retries for opening
     * the connection for executing the transaction.
     * <p>
     *
     * @return the amount of retries as <tt>int</tt>.
     */
    int getRetries();

    /**
     * Set the amount of retries for opening
     * the connection for executing the transaction.
     * <p>
     *
     * @param retries the amount of retries as <tt>int</tt>.
     */
    void setRetries(int retries);

    /**
     * Tests whether the validity of a transaction
     * will be checked.
     * <p>
     *
     * @return true if checking validity, false otherwise.
     */
    boolean isCheckingValidity();

    /**
     * Sets the flag that controls whether the
     * validity of a transaction will be checked.
     * <p>
     *
     * @param b true if checking validity, false otherwise.
     */
    void setCheckingValidity(boolean b);

    /**
     * Executes this <tt>ModbusTransaction</tt>.
     * Locks the <tt>ModbusTransport</tt> for sending
     * the <tt>ModbusRequest</tt> and reading the
     * related <tt>ModbusResponse</tt>.
     * If reconnecting is activated the connection will
     * be opened for the transaction and closed afterwards.
     * <p>
     *
     * @throws ModbusException if an I/O error occurs,
     *                         or the response is a modbus protocol exception.
     */
    void execute() throws ModbusException;

}