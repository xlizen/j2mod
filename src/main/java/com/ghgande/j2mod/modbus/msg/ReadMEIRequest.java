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
import java.io.EOFException;
import java.io.IOException;

/**
 * Class implementing a <tt>Read MEI Data</tt> request.
 *
 * @author jfhaugh (jfh@ghgande.com)
 * @version jamod-1.2rc1-ghpc
 *
 * @version @version@ (@date@)
 */
public final class ReadMEIRequest extends ModbusRequest {

    // instance attributes
    private int m_SubCode;
    private int m_FieldLevel;
    private int m_FieldId;

    /**
     * Constructs a new <tt>Read MEI Data request</tt> instance.
     */
    public ReadMEIRequest() {
        super();

        setFunctionCode(Modbus.READ_MEI);
        m_SubCode = 0x0E;

        // 3 bytes (unit id and function code is excluded)
        setDataLength(3);
    }

    /**
     * Constructs a new <tt>Read MEI Data request</tt> instance with a given
     * reference and count of coils (i.e. bits) to be read.
     * <p>
     *
     * @param level the reference number of the register to read from.
     * @param id the number of bits to be read.
     */
    public ReadMEIRequest(int level, int id) {
        super();

        setFunctionCode(Modbus.READ_MEI);
        m_SubCode = 0x0E;

        // 3 bytes (unit id and function code is excluded)
        setDataLength(3);
        setLevel(level);
        setFieldId(id);
    }

    /**
     * Returns the response
     * @return Response
     */
    public ModbusResponse getResponse() {
        ReadMEIResponse response;

		/*
         * Any other sub-function is an error.
		 */
        if (getSubCode() != 0x0E) {
            IllegalFunctionExceptionResponse error = new IllegalFunctionExceptionResponse();

            error.setUnitID(getUnitID());
            error.setFunctionCode(getFunctionCode());

            return error;
        }

        response = new ReadMEIResponse();

        // transfer header data
        if (!isHeadless()) {
            response.setTransactionID(getTransactionID());
            response.setProtocolID(getProtocolID());
        }
        else {
            response.setHeadless();
        }
        response.setUnitID(getUnitID());
        response.setFunctionCode(Modbus.READ_MEI);

        return response;
    }

    /**
     * The ModbusCoupler interface doesn't have a method for defining MEI for a
     * device.
     */
    public ModbusResponse createResponse() {
        return createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
    }

    /**
     * Gets the MEI subcode associated with this request.
     */
    public int getSubCode() {
        return m_SubCode;
    }

    /**
     * Sets the reference of the register to start reading from with this
     * <tt>ReadCoilsRequest</tt>.
     * <p>
     *
     * @param level the reference of the register to start reading from.
     */
    public void setLevel(int level) {
        m_FieldLevel = level;
    }

    /**
     * Returns the reference of the register to to start reading from with this
     * <tt>ReadCoilsRequest</tt>.
     * <p>
     *
     * @return the reference of the register to start reading from as
     *         <tt>int</tt>.
     */
    public int getLevel() {
        return m_FieldLevel;
    }

    /**
     * Sets the number of bits (i.e. coils) to be read with this
     * <tt>ReadCoilsRequest</tt>.
     * <p>
     *
     * @param id the number of bits to be read.
     */
    public void setFieldId(int id) {
        m_FieldId = id;
    }

    /**
     * Returns the number of bits (i.e. coils) to be read with this
     * <tt>ReadCoilsRequest</tt>.
     * <p>
     *
     * @return the number of bits to be read.
     */
    public int getFieldId() {
        return m_FieldId;
    }

    public void writeData(DataOutput dout) throws IOException {
        byte results[] = new byte[3];

        results[0] = (byte)m_SubCode;
        results[1] = (byte)m_FieldLevel;
        results[2] = (byte)m_FieldId;

        dout.write(results);
    }

    public void readData(DataInput din) throws IOException {
        m_SubCode = din.readUnsignedByte();

        if (m_SubCode != 0xE) {
            try {
                while (din.readByte() >= 0) {
                }
            }
            catch (EOFException x) {
                // do nothing.
            }
            return;
        }
        m_FieldLevel = din.readUnsignedByte();
        m_FieldId = din.readUnsignedByte();
    }

    public byte[] getMessage() {
        byte results[] = new byte[3];

        results[0] = (byte)m_SubCode;
        results[1] = (byte)m_FieldLevel;
        results[2] = (byte)m_FieldId;

        return results;
    }
}