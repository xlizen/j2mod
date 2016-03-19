/*
 * This file is part of j2mod.
 *
 * j2mod is a fork of the jamod library written by Dieter Wimberger
 * and then further enhanced by Julie Haugh with a new LGPL license
 * and upgraded to Java 1.6
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
package com.ghgande.j2mod.modbus.procimg;

/**
 * The default ProcessImageFactory. It creates a new <tt>SimpleProcessImage</tt>
 * each time <tt>createProcessImageImplementation()</tt> is invoked.
 *
 * @author Dieter Wimberger
 * @author jfhaugh
 * @version @version@ (@date@)
 */
public class DefaultProcessImageFactory implements ProcessImageFactory {

    /**
     * Returns a new SimpleProcessImage instance.
     *
     * @return a SimpleProcessImage instance.
     */
    public ProcessImageImplementation createProcessImageImplementation() {
        return new SimpleProcessImage();
    }

    /**
     * Returns a new SimpleDigitalIn instance.
     *
     * @return a SimpleDigitalIn instance.
     */
    public DigitalIn createDigitalIn() {
        return new SimpleDigitalIn();
    }

    /**
     * Returns a new DigitalIn instance with the given state.
     *
     * @param state true if set, false otherwise.
     *
     * @return a SimpleDigitalIn instance.
     */
    public DigitalIn createDigitalIn(boolean state) {
        return new SimpleDigitalIn(state);
    }

    /**
     * Returns a new SimpleDigitalOut instance.
     *
     * @return a SimpleDigitalOut instance.
     */
    public DigitalOut createDigitalOut() {
        return new SimpleDigitalOut();
    }

    /**
     * Returns a new DigitalOut instance with the given state.
     *
     * @param b true if set, false otherwise.
     *
     * @return a SimpleDigitalOut instance.
     */
    public DigitalOut createDigitalOut(boolean b) {
        return new SimpleDigitalOut(b);
    }

    /**
     * Returns a new SimpleInputRegister instance.
     *
     * @return a SimpleInputRegister instance.
     */
    public InputRegister createInputRegister() {
        return new SimpleInputRegister();
    }

    /**
     * Returns a new InputRegister instance with a given value.
     *
     * @param value the value of the register as an <tt>int</tt>
     *
     * @return an InputRegister instance.
     */
    public InputRegister createInputRegister(int value) {
        return new SimpleInputRegister(value);
    }

    /**
     * Returns a new InputRegister instance with a given value.
     *
     * @param b1 the first <tt>byte</tt>.
     * @param b2 the second <tt>byte</tt>.
     *
     * @return an InputRegister instance.
     */
    public InputRegister createInputRegister(byte b1, byte b2) {
        return new SimpleInputRegister(b1, b2);
    }

    /**
     * Creates a new SimpleRegister instance.
     *
     * @return a SimpleRegister instance.
     */
    public Register createRegister() {
        return new SimpleRegister();
    }

    /**
     * Creates a new SimpleRegister instance.
     *
     * @return a SimpleRegister instance.
     */
    public Register createRegister(int value) {
        return new SimpleRegister(value);
    }

    /**
     * Returns a new Register instance with a given value.
     *
     * @param b1 the first <tt>byte</tt>.
     * @param b2 the second <tt>byte</tt>.
     *
     * @return a Register instance.
     */
    public Register createRegister(byte b1, byte b2) {
        return new SimpleRegister(b1, b2);
    }
}