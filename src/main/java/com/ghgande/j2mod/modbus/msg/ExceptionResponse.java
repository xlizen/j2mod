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
 * Class implementing a<tt>ModbusResponse</tt> that represents an exception.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1-ghpc (04/26/2011) Cleaned up a bit and added some Javadocs.
 */
public class ExceptionResponse extends ModbusResponse {

    // instance attributes
    private int m_ExceptionCode = -1;

    /**
     * Returns the Modbus exception code of this <tt>ExceptionResponse</tt>.
     * <p>
     *
     * @return the exception code as <tt>int</tt>.
     */
    public int getExceptionCode() {
        return m_ExceptionCode;
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(getExceptionCode());
    }

    /**
     * readData()
     *
     * read the single byte of data, which is the exception code.
     */
    public void readData(DataInput din) throws IOException {
        m_ExceptionCode = din.readUnsignedByte();
    }

    /**
     * getMessage()
     *
     * return the exception type, which is the "message" for this response.
     *
     * @return -- byte array containing the 1 byte exception code.
     */
    public byte[] getMessage() {
        byte result[] = new byte[1];
        result[0] = (byte)getExceptionCode();
        return result;
    }

    /**
     * Constructs a new <tt>ExceptionResponse</tt> instance with a given
     * function code and an exception code. The function code will be
     * automatically increased with the exception offset.
     *
     * @param fc  the function code as <tt>int</tt>.
     * @param exc the exception code as <tt>int</tt>.
     */
    public ExceptionResponse(int fc, int exc) {

		/*
         * One byte of data.
		 */
        setDataLength(1);
        setFunctionCode(fc | Modbus.EXCEPTION_OFFSET);

        m_ExceptionCode = exc;
    }

    /**
     * Constructs a new <tt>ExceptionResponse</tt> instance with a given
     * function code. ORs the exception offset automatically.
     *
     * @param fc the function code as <tt>int</tt>.
     */
    public ExceptionResponse(int fc) {

		/*
         * One byte of data.
		 */
        setDataLength(1);
        setFunctionCode(fc | Modbus.EXCEPTION_OFFSET);
    }

    /**
     * Constructs a new <tt>ExceptionResponse</tt> instance with no function
     * or exception code.
     */
    public ExceptionResponse() {

		/*
         * One byte of data.
		 */
        setDataLength(1);
    }
}
