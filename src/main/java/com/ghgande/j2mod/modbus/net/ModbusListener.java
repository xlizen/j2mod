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
package com.ghgande.j2mod.modbus.net;

public interface ModbusListener extends Runnable {
    /**
     * Main execution loop for this Modbus interface listener
     */
    void run();

    /**
     * Gets the unit number for this Modbus interface listener.
     *
     * @return The Modbus unit number.
     */
    int getUnit();

    /**
     * Sets the unit number for this Modbus interface listener.
     *
     * @param unit Modbus unit number. A value of 0 indicates this Modbus
     *             interface accepts all unit numbers.
     */
    void setUnit(int unit);

    /**
     * Gets the <i>listening</i> state for this Modbus interface. A Modbus
     * interface which is not <i>listening</i> will silently discard all
     * requests. Additionally, an interface which is no longer alive will return
     * <b>false</b>.
     *
     * @return The current <i>listening</i> state.
     */
    boolean isListening();

    /**
     * Sets the <i>listening</i> state for this Modbus interface. A Modbus
     * interface which is not <i>listening</i> will silently discard all
     * requests.
     *
     * @param listening This interface will accept and process requests.
     */
    void setListening(boolean listening);

    /**
     * Starts the listener thread with the <tt>ModbusListener</tt> in
     * <i>listening</i> mode.
     *
     * @return The listener Thread.
     */
    Thread listen();

    /**
     * Stop the listener thread for this <tt>ModbusListener</tt> instance.
     */
    void stop();
}
