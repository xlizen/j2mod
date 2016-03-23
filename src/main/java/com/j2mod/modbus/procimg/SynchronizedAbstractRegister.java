/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.j2mod.modbus.procimg;

/**
 * Abstract class with synchronized register operations.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
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

    public final int toUnsignedShort() {
        if (m_Register == null) {
            throw new IllegalAddressException();
        }

        return ((m_Register[0] & 0xff) << 8 | (m_Register[1] & 0xff));
    }

    public final short toShort() {
        if (m_Register == null) {
            throw new IllegalAddressException();
        }

        return (short)((m_Register[0] << 8) | (m_Register[1] & 0xff));
    }

    public synchronized byte[] toBytes() {
        byte[] dest = new byte[m_Register.length];
        System.arraycopy(m_Register, 0, dest, 0, dest.length);
        return dest;
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

    public final synchronized void setValue(int v) {
        if (m_Register == null) {
            throw new IllegalAddressException();
        }

        m_Register[0] = (byte)(0xff & (v >> 8));
        m_Register[1] = (byte)(0xff & v);
    }

}
