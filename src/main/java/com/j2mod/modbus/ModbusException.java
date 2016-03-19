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
package com.j2mod.modbus;

/**
 * Superclass of all specialised exceptions in this package.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ModbusException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <tt>ModbusException</tt> instance.
     */
    public ModbusException() {
        super();
    }

    /**
     * Constructs a new <tt>ModbusException</tt> instance with the given
     * message.
     * <p>
     *
     * @param message the message describing this <tt>ModbusException</tt>.
     */
    public ModbusException(String message) {
        super(message);
    }
}
