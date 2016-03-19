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
 * Class implementing an observable digital output.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ObservableDigitalOut extends Observable implements DigitalOut {

    /**
     * A boolean holding the state of this digital out.
     */
    protected boolean m_Set;

    /**
     * Determine if the digital output is set.
     *
     * @return the boolean value of the digital output.
     */
    public boolean isSet() {
        return m_Set;
    }

    /**
     * Set or clear the digital output.  Will notify any registered
     * observers.
     */
    public void set(boolean b) {
        m_Set = b;
        notifyObservers("value");
    }
}
