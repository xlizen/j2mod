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
 * Interface defining a digital input.
 * <p>
 * In Modbus terms this represents an
 * input discrete, it is read only from
 * the slave side.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface DigitalIn {

    /**
     * Tests if this <tt>DigitalIn</tt> is set.
     * <p>
     *
     * @return true if set, false otherwise.
     */
    boolean isSet();

}
