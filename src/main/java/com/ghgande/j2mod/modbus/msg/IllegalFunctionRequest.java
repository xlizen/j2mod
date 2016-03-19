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
package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.Modbus;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * <p>
 * Class implementing a <tt>ModbusRequest</tt> which is created for illegal or
 * non implemented function codes.
 *
 * <p>
 * This is just a helper class to keep the implementation patterns the same for
 * all cases.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class IllegalFunctionRequest extends ModbusRequest {

    /**
     * Constructs a new <tt>IllegalFunctionRequest</tt> instance for a given
     * function code.
     *
     * <p>Used to implement slave devices when an illegal function code
     * has been requested.
     *
     * @param function the function code as <tt>int</tt>.
     */
    public IllegalFunctionRequest(int function) {
        setFunctionCode(function);
    }

    /**
     * Constructs a new <tt>IllegalFunctionRequest</tt> instance for a given
     * function code.
     *
     * <p>Used to implement slave devices when an illegal function code
     * has been requested.
     *
     * @param function the function code as <tt>int</tt>.
     */
    public IllegalFunctionRequest(int unit, int function) {
        setUnitID(unit);
        setFunctionCode(function);
    }

    /**
     * There is no unit number associated with this exception.
     */
    public ModbusResponse getResponse() {
        IllegalFunctionExceptionResponse response = new IllegalFunctionExceptionResponse(getFunctionCode());

        response.setUnitID(getUnitID());
        return response;
    }

    public ModbusResponse createResponse() {
        return createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
    }

    public void writeData(DataOutput dout) throws IOException {
        throw new RuntimeException();
    }

    /**
     * Read all of the data that can be read.  This is an unsupported
     * function, so it may not be possible to know exactly how much data
     * needs to be read.
     */
    public void readData(DataInput din) throws IOException {
        // skip all following bytes
        int length = getDataLength();
        for (int i = 0; i < length; i++) {
            din.readByte();
        }
    }

    public byte[] getMessage() {
        return null;
    }
}
