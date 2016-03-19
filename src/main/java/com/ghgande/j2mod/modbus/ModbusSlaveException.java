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
package com.ghgande.j2mod.modbus;

/**
 * Class that implements a <tt>ModbusSlaveException</tt>. Instances of this
 * exception are thrown when the slave returns a Modbus exception.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ModbusSlaveException extends ModbusException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instance type attribute
     */
    private int m_Type = -1;

    /**
     * <p>
     * Constructs a new <tt>ModbusSlaveException</tt> instance with the given
     * type.
     *
     * <p>
     * Types are defined according to the protocol specification in
     * <tt>net.wimpi.modbus.Modbus</tt>.
     *
     * @param TYPE the type of exception that occurred.
     */
    public ModbusSlaveException(int TYPE) {
        super();

        m_Type = TYPE;
    }

    /**
     * Get the exception type message associated with the given exception
     * number.
     *
     * @param type Numerical value of the Modbus exception.
     *
     * @return a String indicating the type of slave exception.
     */
    public static String getMessage(int type) {
        switch (type) {
            case 1:
                return "Illegal Function";
            case 2:
                return "Illegal Data Address";
            case 3:
                return "Illegal Data Value";
            case 4:
                return "Slave Device Failure";
            case 5:
                return "Acknowledge";
            case 6:
                return "Slave Device Busy";
            case 8:
                return "Memory Parity Error";
        }
        return "Error Code = " + type;
    }

    /**
     * <p>
     * Returns the type of this <tt>ModbusSlaveException</tt>. <br>
     * Types are defined according to the protocol specification in
     * <tt>net.wimpi.modbus.Modbus</tt>.
     *
     * @return the type of this <tt>ModbusSlaveException</tt>.
     */
    public int getType() {
        return m_Type;
    }

    /**
     * <p>
     * Tests if this <tt>ModbusSlaveException</tt> is of a given type.
     *
     * <p>
     * Types are defined according to the protocol specification in
     * <tt>net.wimpi.modbus.Modbus</tt>.
     *
     * @param TYPE the type to test this <tt>ModbusSlaveException</tt> type
     *             against.
     *
     * @return true if this <tt>ModbusSlaveException</tt> is of the given type,
     * false otherwise.
     */
    public boolean isType(int TYPE) {
        return (TYPE == m_Type);
    }

    /**
     * Get the exception type message associated with this exception.
     *
     * @return a String indicating the type of slave exception.
     */
    public String getMessage() {
        return getMessage(m_Type);
    }
}
