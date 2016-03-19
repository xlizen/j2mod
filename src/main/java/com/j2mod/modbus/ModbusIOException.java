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
 * Class that implements a <tt>ModbusIOException</tt>. Instances of this
 * exception are thrown when errors in the I/O occur.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ModbusIOException extends ModbusException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean m_EOF = false;

    /**
     * Constructs a new <tt>ModbusIOException</tt> instance.
     */
    public ModbusIOException() {
    }

    /**
     * Constructs a new <tt>ModbusIOException</tt> instance with the given
     * message.
     * <p>
     *
     * @param message the message describing this <tt>ModbusIOException</tt>.
     */
    public ModbusIOException(String message) {
        super(message);
    }

    /**
     * Constructs a new <tt>ModbusIOException</tt> instance.
     *
     * @param b true if caused by end of stream, false otherwise.
     */
    public ModbusIOException(boolean b) {
        m_EOF = b;
    }

    /**
     * Constructs a new <tt>ModbusIOException</tt> instance with the given
     * message.
     * <p>
     *
     * @param message the message describing this <tt>ModbusIOException</tt>.
     * @param b       true if caused by end of stream, false otherwise.
     */
    public ModbusIOException(String message, boolean b) {
        super(message);

        m_EOF = b;
    }

    /**
     * Tests if this <tt>ModbusIOException</tt> is caused by an end of the
     * stream.
     * <p>
     *
     * @return true if stream ended, false otherwise.
     */
    public boolean isEOF() {
        return m_EOF;
    }

    /**
     * Sets the flag that determines whether this <tt>ModbusIOException</tt> was
     * caused by an end of the stream.
     * <p>
     *
     * @param b true if stream ended, false otherwise.
     */
    public void setEOF(boolean b) {
        m_EOF = b;
    }
}
