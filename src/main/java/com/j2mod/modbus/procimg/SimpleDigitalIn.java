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
 * Class implementing a simple <tt>DigitalIn</tt>.
 * <p>
 * The set method is synchronized, which ensures atomic
 * access, but no specific access order.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class SimpleDigitalIn implements DigitalIn {

    /**
     * Field for the digital in state.
     */
    protected boolean m_Set = false;

    /**
     * Constructs a new <tt>SimpleDigitalIn</tt> instance.
     */
    public SimpleDigitalIn() {
    }

    /**
     * Constructs a new <tt>SimpleDigitalIn</tt> instance
     * with a given valid state.
     *
     * @param b true if to be set, false otherwise.
     */
    public SimpleDigitalIn(boolean b) {
        set(b);
    }

    public boolean isSet() {
        return m_Set;
    }

    /**
     * Sets the state of this <tt>SimpleDigitalIn</tt>.
     * This method should only be used from master/device
     * side.
     *
     * @param b true if to be set, false otherwise.
     */
    public synchronized void set(boolean b) {
        m_Set = b;
    }

}
