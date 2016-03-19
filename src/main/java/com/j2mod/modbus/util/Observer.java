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
package com.j2mod.modbus.util;

/**
 * A cleanroom implementation of the Observer interface
 * for the Observable design pattern.
 * <p/>
 *
 * @author Dieter Wimberger (wimpi)
 * @version 1.2rc1 (09/11/2004)
 */
public interface Observer {

    /**
     * Updates the state of this <tt>Observer</tt> to be in
     * synch with an <tt>Observable</tt> instance.
     * The argument should usually be an indication of the
     * aspects that changed in the <tt>Observable</tt>.
     *
     * @param o   an <tt>Observable</tt> instance.
     * @param arg an arbitrary argument to be passed.
     */
    void update(Observable o, Object arg);

}