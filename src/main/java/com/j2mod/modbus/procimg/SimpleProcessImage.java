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

import java.util.Vector;

/**
 * Class implementing a simple process image to be able to run unit tests or
 * handle simple cases.
 *
 * <p>
 * The image has a simple linear address space for, analog, digital and file
 * objects. Holes may be created by adding a object with a reference after the
 * last object reference of that type.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Julie Added support for files of records.
 */
public class SimpleProcessImage implements ProcessImageImplementation {

    // instance attributes
    protected final Vector<DigitalIn> m_DigitalInputs = new Vector<DigitalIn>();
    protected final Vector<DigitalOut> m_DigitalOutputs = new Vector<DigitalOut>();
    protected final Vector<InputRegister> m_InputRegisters = new Vector<InputRegister>();
    protected final Vector<Register> m_Registers = new Vector<Register>();
    protected final Vector<File> m_Files = new Vector<File>();
    protected final Vector<FIFO> m_FIFOs = new Vector<FIFO>();
    protected boolean m_Locked = false;
    protected int m_Unit = 0;

    /**
     * Constructs a new <tt>SimpleProcessImage</tt> instance.
     */
    public SimpleProcessImage() {
    }

    /**
     * Constructs a new <tt>SimpleProcessImage</tt> instance having a
     * (potentially) non-zero unit ID.
     */
    public SimpleProcessImage(int unit) {
        m_Unit = unit;
    }

    /**
     * The process image is locked to prevent changes.
     *
     * @return whether or not the process image is locked.
     */
    public synchronized boolean isLocked() {
        return m_Locked;
    }

    /**
     * setLocked -- lock or unlock the process image. It is an error (false
     * return value) to attempt to lock the process image when it is already
     * locked.
     *
     * <p>
     * Compatability Note: jamod did not enforce this restriction, so it is
     * being handled in a way which is backwards compatible. If you wish to
     * determine if you acquired the lock, check the return value. If your code
     * is still based on the jamod paradigm, you will ignore the return value
     * and your code will function as before.
     */
    public synchronized boolean setLocked(boolean locked) {
        if (m_Locked && locked) {
            return false;
        }

        m_Locked = locked;
        return true;
    }

    public int getUnitID() {
        return m_Unit;
    }

    public DigitalOut[] getDigitalOutRange(int ref, int count) {
        // ensure valid reference range
        if (ref < 0 || ref + count > m_DigitalOutputs.size()) {
            throw new IllegalAddressException();
        }
        else {
            DigitalOut[] douts = new DigitalOut[count];
            for (int i = 0; i < douts.length; i++) {
                douts[i] = getDigitalOut(ref + i);
            }
            return douts;
        }
    }

    public DigitalOut getDigitalOut(int ref) throws IllegalAddressException {
        try {
            DigitalOut result = m_DigitalOutputs.elementAt(ref);
            if (result == null) {
                throw new IllegalAddressException();
            }
            return result;
        }
        catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    public int getDigitalOutCount() {
        return m_DigitalOutputs.size();
    }

    public DigitalIn[] getDigitalInRange(int ref, int count) {
        // ensure valid reference range
        if (ref < 0 || ref + count > m_DigitalInputs.size()) {
            throw new IllegalAddressException();
        }
        else {
            DigitalIn[] dins = new DigitalIn[count];
            for (int i = 0; i < dins.length; i++) {
                dins[i] = getDigitalIn(ref + i);
            }
            return dins;
        }
    }

    public DigitalIn getDigitalIn(int ref) throws IllegalAddressException {
        try {
            DigitalIn result = m_DigitalInputs.elementAt(ref);
            if (result == null) {
                throw new IllegalAddressException();
            }
            return result;
        }
        catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    public int getDigitalInCount() {
        return m_DigitalInputs.size();
    }

    public InputRegister[] getInputRegisterRange(int ref, int count) {
        // ensure valid reference range
        if (ref < 0 || ref + count > m_InputRegisters.size()) {
            throw new IllegalAddressException();
        }

        InputRegister[] iregs = new InputRegister[count];
        for (int i = 0; i < iregs.length; i++) {
            iregs[i] = getInputRegister(ref + i);
        }

        return iregs;
    }

    public InputRegister getInputRegister(int ref) throws IllegalAddressException {
        try {
            InputRegister result = m_InputRegisters.elementAt(ref);
            if (result == null) {
                throw new IllegalAddressException();
            }

            return result;
        }
        catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    public int getInputRegisterCount() {
        return m_InputRegisters.size();
    }

    public Register[] getRegisterRange(int ref, int count) {
        if (ref < 0 || ref + count > m_Registers.size()) {
            throw new IllegalAddressException();
        }
        else {
            Register[] iregs = new Register[count];
            for (int i = 0; i < iregs.length; i++) {
                iregs[i] = getRegister(ref + i);
            }
            return iregs;
        }
    }

    public Register getRegister(int ref) throws IllegalAddressException {
        try {
            Register result = m_Registers.elementAt(ref);
            if (result == null) {
                throw new IllegalAddressException();
            }

            return result;
        }
        catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    public int getRegisterCount() {
        return m_Registers.size();
    }

    public File getFile(int fileNumber) {
        try {
            File result = m_Files.elementAt(fileNumber);
            if (result == null) {
                throw new IllegalAddressException();
            }

            return result;
        }
        catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    public File getFileByNumber(int ref) {
        if (ref < 0 || ref >= 10000 || m_Files == null) {
            throw new IllegalAddressException();
        }

        synchronized (m_Files) {
            for (File file : m_Files) {
                if (file.getFileNumber() == ref) {
                    return file;
                }
            }
        }

        throw new IllegalAddressException();
    }

    public int getFileCount() {
        return m_Files.size();
    }

    public FIFO getFIFO(int fifoNumber) {
        try {
            FIFO result = m_FIFOs.elementAt(fifoNumber);
            if (result == null) {
                throw new IllegalAddressException();
            }

            return result;
        }
        catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    public FIFO getFIFOByAddress(int ref) {
        for (FIFO fifo : m_FIFOs) {
            if (fifo.getAddress() == ref) {
                return fifo;
            }
        }

        return null;
    }

    public int getFIFOCount() {
        if (m_FIFOs == null) {
            return 0;
        }

        return m_FIFOs.size();
    }

    public void setDigitalOut(int ref, DigitalOut _do) throws IllegalAddressException {
        if (!isLocked()) {
            try {
                if (m_DigitalOutputs.get(ref) == null) {
                    throw new IllegalAddressException();
                }
                m_DigitalOutputs.setElementAt(_do, ref);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    public void addDigitalOut(DigitalOut _do) {
        if (!isLocked()) {
            m_DigitalOutputs.addElement(_do);
        }
    }

    public void addDigitalOut(int ref, DigitalOut dout) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            synchronized (m_DigitalOutputs) {
                if (ref < m_DigitalOutputs.size()) {
                    m_DigitalOutputs.setElementAt(dout, ref);
                    return;
                }
                m_DigitalOutputs.setSize(ref + 1);
                m_DigitalOutputs.setElementAt(dout, ref);
            }
        }
    }

    public void removeDigitalOut(DigitalOut _do) {
        if (!isLocked()) {
            m_DigitalOutputs.removeElement(_do);
        }
    }

    public void setDigitalIn(int ref, DigitalIn di) throws IllegalAddressException {
        if (!isLocked()) {
            try {
                if (m_DigitalInputs.get(ref) == null) {
                    throw new IllegalAddressException();
                }
                m_DigitalInputs.setElementAt(di, ref);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    public void addDigitalIn(DigitalIn di) {
        if (!isLocked()) {
            m_DigitalInputs.addElement(di);
        }
    }

    public void addDigitalIn(int ref, DigitalIn d1) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            synchronized (m_DigitalInputs) {
                if (ref < m_DigitalInputs.size()) {
                    m_DigitalInputs.setElementAt(d1, ref);
                    return;
                }
                m_DigitalInputs.setSize(ref + 1);
                m_DigitalInputs.setElementAt(d1, ref);
            }
        }
    }

    public void removeDigitalIn(DigitalIn di) {
        if (!isLocked()) {
            m_DigitalInputs.removeElement(di);
        }
    }

    public void setInputRegister(int ref, InputRegister reg) throws IllegalAddressException {
        if (!isLocked()) {
            try {
                if (m_InputRegisters.get(ref) == null) {
                    throw new IllegalAddressException();
                }

                m_InputRegisters.setElementAt(reg, ref);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    public void addInputRegister(InputRegister reg) {
        if (!isLocked()) {
            m_InputRegisters.addElement(reg);
        }
    }

    public void addInputRegister(int ref, InputRegister inReg) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            synchronized (m_InputRegisters) {
                if (ref < m_InputRegisters.size()) {
                    m_InputRegisters.setElementAt(inReg, ref);
                    return;
                }
                m_InputRegisters.setSize(ref + 1);
                m_InputRegisters.setElementAt(inReg, ref);
            }
        }
    }

    public void removeInputRegister(InputRegister reg) {
        if (!isLocked()) {
            m_InputRegisters.removeElement(reg);
        }
    }

    public void setRegister(int ref, Register reg) throws IllegalAddressException {
        if (!isLocked()) {
            try {
                if (m_Registers.get(ref) == null) {
                    throw new IllegalAddressException();
                }

                m_Registers.setElementAt(reg, ref);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    public void addRegister(Register reg) {
        if (!isLocked()) {
            m_Registers.addElement(reg);
        }
    }

    public void addRegister(int ref, Register reg) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            synchronized (m_Registers) {
                if (ref < m_Registers.size()) {
                    m_Registers.setElementAt(reg, ref);
                    return;
                }
                m_Registers.setSize(ref + 1);
                m_Registers.setElementAt(reg, ref);
            }
        }
    }

    public void removeRegister(Register reg) {
        if (!isLocked()) {
            m_Registers.removeElement(reg);
        }
    }

    public void setFile(int fileNumber, File file) {
        if (!isLocked()) {
            try {
                if (m_Files.get(fileNumber) == null) {
                    throw new IllegalAddressException();
                }

                m_Files.setElementAt(file, fileNumber);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    public void addFile(File newFile) {
        if (!isLocked()) {
            m_Files.add(newFile);
        }
    }

    public void addFile(int ref, File newFile) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            synchronized (m_Files) {
                if (ref < m_Files.size()) {
                    m_Files.setElementAt(newFile, ref);
                    return;
                }
                m_Files.setSize(ref + 1);
                m_Files.setElementAt(newFile, ref);
            }
        }
    }

    public void removeFile(File oldFile) {
        if (!isLocked()) {
            m_Files.removeElement(oldFile);
        }
    }

    public void setFIFO(int fifoNumber, FIFO fifo) {
        if (!isLocked()) {
            try {
                if (m_FIFOs.get(fifoNumber) == null) {
                    throw new IllegalAddressException();
                }

                m_FIFOs.setElementAt(fifo, fifoNumber);
            }
            catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    public void addFIFO(FIFO fifo) {
        if (!isLocked()) {
            m_FIFOs.add(fifo);
        }
    }

    public void addFIFO(int ref, FIFO newFIFO) {
        if (ref < 0 || ref >= 65536) {
            throw new IllegalArgumentException();
        }

        if (!isLocked()) {
            synchronized (m_FIFOs) {
                if (ref < m_FIFOs.size()) {
                    m_FIFOs.setElementAt(newFIFO, ref);
                    return;
                }
                m_FIFOs.setSize(ref + 1);
                m_FIFOs.setElementAt(newFIFO, ref);
            }
        }
    }

    public void removeFIFO(FIFO oldFIFO) {
        if (!isLocked()) {
            m_FIFOs.removeElement(oldFIFO);
        }
    }

}
