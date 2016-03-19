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
package com.j2mod.modbus.procimg;

/**
 * Class implementing a simple <tt>DigitalOut</tt>.
 * <p>
 * The set method is synchronized, which ensures atomic
 * access, but no specific access order.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class SimpleDigitalOut implements DigitalOut {

    /**
     * Field for the digital out state.
     */
    protected boolean m_Set;

    /**
     * Constructs a new <tt>SimpleDigitalOut</tt> instance.
     * It's state will be invalid.
     */
    public SimpleDigitalOut() {
    }

    /**
     * Constructs a new <tt>SimpleDigitalOut</tt> instance
     * with the given state.
     *
     * @param b true if set, false otherwise.
     */
    public SimpleDigitalOut(boolean b) {
        set(b);
    }

    public boolean isSet() {
        return m_Set;
    }

    public synchronized void set(boolean b) {
        m_Set = b;
    }

}
