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
package com.ghgande.j2mod.modbus.util;

import java.util.Vector;

/**
 * A cleanroom implementation of the Observable pattern.
 * <p/>
 *
 * @author Dieter Wimberger (wimpi)
 * @version 1.2rc1 (09/11/2004)
 */
public class Observable {

    private static final Logger logger = Logger.getLogger(Observable.class);

    private Vector<Observer> m_Observers;

    /**
     * Constructs a new Observable instance.
     */
    public Observable() {
        m_Observers = new Vector<Observer>(10);
    }

    public synchronized int getObserverCount() {
        return m_Observers.size();
    }

    /**
     * Adds an observer instance if it is not already in the set of observers
     * for this <tt>Observable</tt>.
     *
     * @param o an observer instance to be added.
     */
    public synchronized void addObserver(Observer o) {
        if (!m_Observers.contains(o)) {
            m_Observers.addElement(o);
        }
    }

    /**
     * Removes an observer instance from the set of observers of this
     * <tt>Observable</tt>.
     *
     * @param o an observer instance to be removed.
     */
    public synchronized void removeObserver(Observer o) {
        m_Observers.removeElement(o);
    }

    /**
     * Removes all observer instances from the set of observers of this
     * <tt>Observable</tt>.
     */
    public synchronized void removeObservers() {
        m_Observers.removeAllElements();
    }

    /**
     * Notifies all observer instances in the set of observers of this
     * <tt>Observable</tt>.
     *
     * @param arg an arbitrary argument to be passed.
     */
    public synchronized void notifyObservers(Object arg) {
        for (int i = 0; i < m_Observers.size(); i++) {
            m_Observers.elementAt(i).update(this, arg);
        }
    }
}
