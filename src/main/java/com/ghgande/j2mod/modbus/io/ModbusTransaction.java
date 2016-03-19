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
 * @version 1.2rc1 (09/11/2004)
 */
public interface ModbusTransaction {

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
     * Returns the <tt>ModbusRequest</tt> instance
     * associated with this <tt>ModbusTransaction</tt>.
     * <p>
     *
     * @return the associated <tt>ModbusRequest</tt> instance.
     */
    ModbusRequest getRequest();

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
     * Set the amount of retries for opening
     * the connection for executing the transaction.
     * <p>
     *
     * @param retries the amount of retries as <tt>int</tt>.
     */
    void setRetries(int retries);

    /**
     * Returns the amount of retries for opening
     * the connection for executing the transaction.
     * <p>
     *
     * @return the amount of retries as <tt>int</tt>.
     */
    int getRetries();

    /**
     * Sets the flag that controls whether the
     * validity of a transaction will be checked.
     * <p>
     *
     * @param b true if checking validity, false otherwise.
     */
    void setCheckingValidity(boolean b);

    /**
     * Tests whether the validity of a transaction
     * will be checked.
     * <p>
     *
     * @return true if checking validity, false otherwise.
     */
    boolean isCheckingValidity();

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