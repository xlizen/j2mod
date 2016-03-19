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
 * along with j2mod.  If not, see <http://www.gnu.org/licenses
 */
package com.j2mod.modbus.procimg;

/**
 * Class implementing a simple <tt>InputRegister</tt>.
 * <p>
 * The <tt>setValue()</tt> method is synchronized, which ensures atomic access, * but no specific access order.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class SimpleInputRegister extends SynchronizedAbstractRegister implements InputRegister {

    /**
     * Constructs a new <tt>SimpleInputRegister</tt> instance. It's state will
     * be invalid.
     */
    public SimpleInputRegister() {
    }

    /**
     * Constructs a new <tt>SimpleInputRegister</tt> instance.
     *
     * @param b1
     *            the first (hi) byte of the word.
     * @param b2
     *            the second (low) byte of the word.
     */
    public SimpleInputRegister(byte b1, byte b2) {
        m_Register[0] = b1;
        m_Register[1] = b2;
    }

    /**
     * Constructs a new <tt>SimpleInputRegister</tt> instance with the given
     * value.
     *
     * @param value
     *            the value of this <tt>SimpleInputRegister</tt> as <tt>int</tt>
     *            .
     */
    public SimpleInputRegister(int value) {
        setValue(value);
    }// constructor(int)

    public String toString() {
        if (m_Register == null) {
            return "invalid";
        }

        return getValue() + "";
    }

}
