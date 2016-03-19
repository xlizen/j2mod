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
 * Interface defining an input register.
 * <p>
 * This register is read only from the slave side.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface InputRegister {

    /**
     * Returns the value of this <tt>InputRegister</tt>. The value is stored as
     * <tt>int</tt> but should be treated like a 16-bit word.
     *
     * @return the value as <tt>int</tt>.
     */
    int getValue();

    /**
     * Returns the content of this <tt>Register</tt> as unsigned 16-bit value
     * (unsigned short).
     *
     * @return the content as unsigned short (<tt>int</tt>).
     */
    int toUnsignedShort();

    /**
     * Returns the content of this <tt>Register</tt> as signed 16-bit value
     * (short).
     *
     * @return the content as <tt>short</tt>.
     */
    short toShort();

    /**
     * Returns the content of this <tt>Register</tt> as bytes.
     *
     * @return a <tt>byte[]</tt> with length 2.
     */
    byte[] toBytes();

}