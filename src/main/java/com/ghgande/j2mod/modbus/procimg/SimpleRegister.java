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
package com.ghgande.j2mod.modbus.procimg;

/**
 * Class implementing a simple <tt>Register</tt>.
 * <p>
 * The <tt>setValue()</tt> method is synchronized, which ensures atomic access, * but no specific access order.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Julie Haugh
 * @version 0.97 (8/12/12)
 */
public class SimpleRegister extends SynchronizedAbstractRegister implements
        Register {

    public String toString() {
        if (m_Register == null) {
            return "invalid";
        }

        return getValue() + "";
    }

    /**
     * Constructs a new <tt>SimpleRegister</tt> instance.
     *
     * @param b1
     *            the first (hi) byte of the word.
     * @param b2
     *            the second (low) byte of the word.
     */
    public SimpleRegister(byte b1, byte b2) {
        m_Register[0] = b1;
        m_Register[1] = b2;
    }

    /**
     * Constructs a new <tt>SimpleRegister</tt> instance with the given value.
     *
     * @param value
     *            the value of this <tt>SimpleRegister</tt> as <tt>int</tt>.
     */
    public SimpleRegister(int value) {
        setValue(value);
    }

    /**
     * Constructs a new <tt>SimpleRegister</tt> instance. It's state will be
     * invalid.
     *
     * Attempting to access this register will result in an
     * IllegalAddressException(). It may be used to create "holes" in a Modbus
     * register map.
     */
    public SimpleRegister() {
        m_Register = null;
    }
}
