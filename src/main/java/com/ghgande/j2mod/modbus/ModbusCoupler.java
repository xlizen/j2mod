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

import com.ghgande.j2mod.modbus.procimg.DefaultProcessImageFactory;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.ProcessImageFactory;
import com.ghgande.j2mod.modbus.util.Logger;

/**
 * Class implemented following a Singleton pattern, to couple the slave side
 * with a master side or with a device.
 *
 * <p>
 * At the moment it only provides a reference to the OO model of the process
 * image.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ModbusCoupler {

    private static final Logger logger = Logger.getLogger(ModbusCoupler.class);

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

    /**
     * Private constructor to prevent multiple instantiation.
     *
     * @param procimg a <tt>ProcessImage</tt>.
     */
    private ModbusCoupler(ProcessImage procimg) {
        setProcessImage(procimg);
        c_Self = this;
    }

    public static boolean isInitialized() {
        return c_Self != null;
    }

    /**
     * Returns a reference to the singleton instance.
     *
     * @return the <tt>ModbusCoupler</tt> instance reference.
     */
    public static synchronized ModbusCoupler getReference() {
        if (c_Self == null) {
            return (c_Self = new ModbusCoupler());
        }
        else {
            return c_Self;
        }
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
