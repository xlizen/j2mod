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
package com.ghgande.j2mod.modbus.procimg;

/**
 * Interface defining a register.
 *
 * <p>
 * A register is read-write from slave and master or device side. Therefore
 * implementations have to be carefully designed for concurrency.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface Register extends InputRegister {

    /**
     * Sets the content of this <tt>Register</tt> from the given unsigned 16-bit
     * value (unsigned short).
     *
     * @param v
     *            the value as unsigned short (<tt>int</tt>).
     */
    void setValue(int v);

    /**
     * Sets the content of this register from the given signed 16-bit value
     * (short).
     *
     * @param s
     *            the value as <tt>short</tt>.
     */
    void setValue(short s);

    /**
     * Sets the content of this register from the given raw bytes.
     *
     * @param bytes
     *            the raw data as <tt>byte[]</tt>.
     */
    void setValue(byte[] bytes);
}
