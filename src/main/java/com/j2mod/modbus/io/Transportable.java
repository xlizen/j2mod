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
package com.j2mod.modbus.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Interface defining a transportable class.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface Transportable {

    /**
     * Returns the number of bytes that will
     * be written by {@link #writeTo(DataOutput)}.
     *
     * @return the number of bytes that will be written as <tt>int</tt>.
     */
    int getOutputLength();

    /**
     * Writes this <tt>Transportable</tt> to the
     * given <tt>DataOutput</tt>.
     *
     * @param dout the <tt>DataOutput</tt> to write to.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    void writeTo(DataOutput dout) throws IOException;

    /**
     * Reads this <tt>Transportable</tt> from the given
     * <tt>DataInput</tt>.
     *
     * @param din the <tt>DataInput</tt> to read from.
     *
     * @throws java.io.IOException if an I/O error occurs or the data
     *                             is invalid.
     */
    void readFrom(DataInput din) throws IOException;

}