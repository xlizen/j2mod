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

/**
 * Interface defining the factory methods for
 * the process image and it's elements.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface ProcessImageFactory {

    /**
     * Returns a new ProcessImageImplementation instance.
     *
     * @return a ProcessImageImplementation instance.
     */
    ProcessImageImplementation createProcessImageImplementation();

    /**
     * Returns a new DigitalIn instance.
     *
     * @return a DigitalIn instance.
     */
    DigitalIn createDigitalIn();

    /**
     * Returns a new DigitalIn instance with the given state.
     *
     * @param state true if set, false otherwise.
     *
     * @return a DigitalIn instance.
     */
    DigitalIn createDigitalIn(boolean state);

    /**
     * Returns a new DigitalOut instance.
     *
     * @return a DigitalOut instance.
     */
    DigitalOut createDigitalOut();

    /**
     * Returns a new DigitalOut instance with the
     * given state.
     *
     * @param b true if set, false otherwise.
     *
     * @return a DigitalOut instance.
     */
    DigitalOut createDigitalOut(boolean b);

    /**
     * Returns a new InputRegister instance.
     *
     * @return an InputRegister instance.
     */
    InputRegister createInputRegister();

    /**
     * Returns a new InputRegister instance with a
     * given value.
     *
     * @param b1 the first <tt>byte</tt>.
     * @param b2 the second <tt>byte</tt>.
     *
     * @return an InputRegister instance.
     */
    InputRegister createInputRegister(byte b1, byte b2);

    /**
     * Creates a new Register instance.
     *
     * @return a Register instance.
     */
    Register createRegister();

    /**
     * Returns a new Register instance with a
     * given value.
     *
     * @param b1 the first <tt>byte</tt>.
     * @param b2 the second <tt>byte</tt>.
     *
     * @return a Register instance.
     */
    Register createRegister(byte b1, byte b2);

}