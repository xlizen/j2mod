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

import com.j2mod.modbus.util.Observable;

/**
 * Class implementing an observable register.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ObservableRegister extends Observable implements Register {

    /**
     * The word holding the content of this register.
     */
    protected short m_Register;

    synchronized public int getValue() {
        return m_Register & 0xFFFF;
    }

    public final int toUnsignedShort() {
        return m_Register & 0xFFFF;
    }

    public final short toShort() {
        return m_Register;
    }

    public synchronized byte[] toBytes() {
        return new byte[]{(byte)(m_Register >> 8), (byte)(m_Register & 0xFF)};
    }

    public final synchronized void setValue(short s) {
        m_Register = s;
        notifyObservers("value");
    }

    public final synchronized void setValue(byte[] bytes) {
        if (bytes.length < 2) {
            throw new IllegalArgumentException();
        }
        else {
            m_Register = (short)(((short)((bytes[0] << 8))) | (((short)(bytes[1])) & 0xFF));
            notifyObservers("value");
        }
    }

    public final synchronized void setValue(int v) {
        m_Register = (short)v;
        notifyObservers("value");
    }
}