/*
 * This file is part of j2mod-steve.
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
package com.ghgande.j2mod.modbus.msg;

//import java.io.EOFException;
//import java.io.IOException;

import com.ghgande.j2mod.modbus.io.Transportable;

/**
 * Interface defining a ModbusMessage.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface ModbusMessage extends Transportable {

    /**
     * Check the flag which indicates that this <tt>ModbusMessage</tt> is for a
     * headless (serial, or headless networked) connection.
     */
    boolean isHeadless();

    /**
     * Sets the flag that marks this <tt>ModbusMessage</tt> as headless (for
     * serial transport).
     */
    void setHeadless();

    /**
     * Returns the transaction identifier of this <tt>ModbusMessage</tt> as
     * <tt>int</tt>.
     *
     * <p>
     * The identifier is a 2-byte (short) non negative integer value valid in
     * the range of 0-65535.
     *
     * @return the transaction identifier as <tt>int</tt>.
     */
    int getTransactionID();

    /**
     * Returns the protocol identifier of this <tt>ModbusMessage</tt> as
     * <tt>int</tt>.
     *
     * <p>
     * The identifier is a 2-byte (short) non negative integer value valid in
     * the range of 0-65535.
     *
     * @return the protocol identifier as <tt>int</tt>.
     */
    int getProtocolID();

    /**
     * Returns the length of the data appended after the protocol header.
     * <p>
     *
     * @return the data length as <tt>int</tt>.
     */
    int getDataLength();

    /**
     * Returns the unit identifier of this <tt>ModbusMessage</tt> as
     * <tt>int</tt>.
     *
     * <p>
     * The identifier is a 1-byte non negative integer value valid in the range
     * of 0-255.
     *
     * @return the unit identifier as <tt>int</tt>.
     */
    int getUnitID();

    /**
     * Returns the function code of this <tt>ModbusMessage</tt> as <tt>int</tt>.<br>
     * The function code is a 1-byte non negative integer value valid in the
     * range of 0-127.
     *
     * <p>
     * Function codes are ordered in conformance classes their values are
     * specified in <tt>com.ghgande.j2mod.modbus.Modbus</tt>.
     *
     * @return the function code as <tt>int</tt>.
     *
     * @see com.ghgande.j2mod.modbus.Modbus
     */
    int getFunctionCode();

    /**
     * Returns the <i>raw</i> message as an array of bytes.
     * <p>
     *
     * @return the <i>raw</i> message as <tt>byte[]</tt>.
     */
    byte[] getMessage();

    /**
     * Returns the <i>raw</i> message as <tt>String</tt> containing a
     * hexadecimal series of bytes.
     *
     * <p>
     * This method is specially for debugging purposes, allowing the user to log
     * the communication in a manner used in the specification document.
     *
     * @return the <i>raw</i> message as <tt>String</tt> containing a
     * hexadecimal series of bytes.
     */
    String getHexMessage();
}
