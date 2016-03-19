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
 * Abstract class with synchronized register operations.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public abstract class SynchronizedAbstractRegister implements Register {

    /**
     * The word (<tt>byte[2]</tt>) holding the state of this register.
     *
     * Note that a superclass may set m_Register to null to create a
     * gap in a Modbus map.
     */
    protected byte[] m_Register = new byte[2];

    synchronized public int getValue() {
        if (m_Register == null) {
            throw new IllegalAddressException();
        }

        return ((m_Register[0] & 0xff) << 8 | (m_Register[1] & 0xff));
    }

    public synchronized byte[] toBytes() {
        byte[] dest = new byte[m_Register.length];
        System.arraycopy(m_Register, 0, dest, 0, dest.length);
        return dest;
    }

    public final short toShort() {
        if (m_Register == null) {
            throw new IllegalAddressException();
        }

        return (short)((m_Register[0] << 8) | (m_Register[1] & 0xff));
    }

    public final int toUnsignedShort() {
        if (m_Register == null) {
            throw new IllegalAddressException();
        }

        return ((m_Register[0] & 0xff) << 8 | (m_Register[1] & 0xff));
    }

    public final synchronized void setValue(int v) {
        if (m_Register == null) {
            throw new IllegalAddressException();
        }

        m_Register[0] = (byte)(0xff & (v >> 8));
        m_Register[1] = (byte)(0xff & v);
    }

    public final synchronized void setValue(short s) {
        if (m_Register == null) {
            throw new IllegalAddressException();
        }

        m_Register[0] = (byte)(0xff & (s >> 8));
        m_Register[1] = (byte)(0xff & s);
    }

    public final synchronized void setValue(byte[] bytes) {
        if (bytes.length < 2) {
            throw new IllegalArgumentException();
        }
        else {
            if (m_Register == null) {
                throw new IllegalAddressException();
            }

            m_Register[0] = bytes[0];
            m_Register[1] = bytes[1];
        }
    }

}
