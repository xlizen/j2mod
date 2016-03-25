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
package com.j2mod.modbus;

import com.j2mod.modbus.procimg.DefaultProcessImageFactory;
import com.j2mod.modbus.procimg.ProcessImage;
import com.j2mod.modbus.procimg.ProcessImageFactory;
import com.j2mod.modbus.util.ModbusLogger;

/**
 * Class implemented following a Singleton pattern, to couple the slave side
 * with a master side or with a device.
 *
 * <p>
 * At the moment it only provides a reference to the OO model of the process
 * image.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class ModbusCoupler {

    private static final ModbusLogger logger = ModbusLogger.getLogger(ModbusCoupler.class);

    // class attributes
    private static ModbusCoupler c_Self; // Singleton reference

    // instance attributes
    private ProcessImage m_ProcessImage;
    private int m_UnitID = Modbus.DEFAULT_UNIT_ID;
    private boolean m_Master = true;
    private ProcessImageFactory m_PIFactory;

    /**
     * A private constructor which creates a default process image.
     */
    private ModbusCoupler() {
        m_PIFactory = new DefaultProcessImageFactory();
    }

    public static synchronized boolean isInitialized() {
        return c_Self != null;
    }

    /**
     * Returns a reference to the singleton instance.
     *
     * @return the <tt>ModbusCoupler</tt> instance reference.
     */
    public static synchronized ModbusCoupler getReference() {
        if (c_Self == null) {
            c_Self = new ModbusCoupler();
        }
        return c_Self;
    }

    /**
     * Returns the actual <tt>ProcessImageFactory</tt> instance.
     *
     * @return a <tt>ProcessImageFactory</tt> instance.
     */
    public ProcessImageFactory getProcessImageFactory() {
        return m_PIFactory;
    }

    /**
     * Sets the <tt>ProcessImageFactory</tt> instance.
     *
     * @param factory the instance to be used for creating process image instances.
     */
    public void setProcessImageFactory(ProcessImageFactory factory) {
        m_PIFactory = factory;
    }

    /**
     * Returns a reference to the <tt>ProcessImage</tt> of this
     * <tt>ModbusCoupler</tt>.
     *
     * @return the <tt>ProcessImage</tt>.
     */
    public synchronized ProcessImage getProcessImage() {
        return m_ProcessImage;
    }

    /**
     * Sets the reference to the <tt>ProcessImage</tt> of this
     * <tt>ModbusCoupler</tt>.
     *
     * @param procimg the <tt>ProcessImage</tt> to be set.
     */
    public synchronized void setProcessImage(ProcessImage procimg) {
        m_ProcessImage = procimg;
    }

    /**
     * Returns the identifier of this unit. This identifier is required to be
     * set for serial protocol slave implementations.
     *
     * @return the unit identifier as <tt>int</tt>.
     */
    public int getUnitID() {
        return m_UnitID;
    }

    /**
     * Sets the identifier of this unit, which is needed to be determined in a
     * serial network.
     *
     * @param id the new unit identifier as <tt>int</tt>.
     */
    public void setUnitID(int id) {
        m_UnitID = id;
    }

    /**
     * Tests if this instance is a master device.
     *
     * @return true if master, false otherwise.
     */
    public boolean isMaster() {
        return m_Master;
    }

    /**
     * Sets this instance to be or not to be a master device.
     *
     * @param master true if master device, false otherwise.
     */
    public void setMaster(boolean master) {
        m_Master = master;
    }

    /**
     * Tests if this instance is not a master device.
     *
     * @return true if slave, false otherwise.
     */
    public boolean isSlave() {
        return !m_Master;
    }
}
