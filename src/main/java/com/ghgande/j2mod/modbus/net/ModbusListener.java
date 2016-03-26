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
package com.ghgande.j2mod.modbus.net;

/**
 * Definition of a listener class
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
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
