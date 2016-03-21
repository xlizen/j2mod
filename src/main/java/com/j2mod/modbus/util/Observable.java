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
package com.j2mod.modbus.util;

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
