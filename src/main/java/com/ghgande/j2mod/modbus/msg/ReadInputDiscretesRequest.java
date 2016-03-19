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
import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.procimg.DigitalIn;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>ReadInputDiscretesRequest</tt>. The implementation
 * directly correlates with the class 1 function <i>read input discretes (FC
 * 2)</i>. It encapsulates the corresponding request message.
 * <p>
 * Input Discretes are understood as bits that cannot be manipulated (i.e. set
 * or unset).
 *
 * @author Dieter Wimberger
 * @author jfhaugh
 * @version @version@ (@date@)
 */
public final class ReadInputDiscretesRequest extends ModbusRequest {

    // instance attributes
    private int m_Reference;
    private int m_BitCount;

    /**
     * Constructs a new <tt>ReadInputDiscretesRequest</tt> instance.
     */
    public ReadInputDiscretesRequest() {
        super();

        setFunctionCode(Modbus.READ_INPUT_DISCRETES);

		/*
         * Two bytes for count, two bytes for offset.
		 */
        setDataLength(4);
    }

    /**
     * Constructs a new <tt>ReadInputDiscretesRequest</tt> instance with a given
     * reference and count of input discretes (i.e. bits) to be read.
     * <p>
     *
     * @param ref   the reference number of the register to read from.
     * @param count the number of bits to be read.
     */
    public ReadInputDiscretesRequest(int ref, int count) {
        super();

        setFunctionCode(Modbus.READ_INPUT_DISCRETES);
        // 4 bytes (unit id and function code is excluded)
        setDataLength(4);
        setReference(ref);
        setBitCount(count);
    }

    /**
     * Constructs a response to match this request.
     *
     * <p>Used by slave implementations to construct the appropriate
     * response.
     *
     * @return Discretes response
     */
    public ReadInputDiscretesResponse getResponse() {
        ReadInputDiscretesResponse response = new ReadInputDiscretesResponse(getBitCount());

        response.setUnitID(getUnitID());
        response.setFunctionCode(getFunctionCode());

        response.setHeadless(isHeadless());
        if (!isHeadless()) {
            response.setTransactionID(getTransactionID());
            response.setProtocolID(getProtocolID());
        }
        return response;
    }

    public ModbusResponse createResponse() {
        ReadInputDiscretesResponse response;
        DigitalIn[] dins;

        // 1. get process image
        ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
        // 2. get input discretes range
        try {
            dins = procimg.getDigitalInRange(getReference(), getBitCount());
        }
        catch (IllegalAddressException e) {
            return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = getResponse();

		/*
		 * Populate the discrete values from the process image.
		 */
        for (int i = 0; i < dins.length; i++) {
            response.setDiscreteStatus(i, dins[i].isSet());
        }

        return response;
    }

    /**
     * Returns the reference of the discrete to to start reading from with
     * this <tt>ReadInputDiscretesRequest</tt>.
     *
     * @return the reference of the discrete to start reading from as
     * <tt>int</tt>.
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * Sets the reference of the register to start reading from with this
     * <tt>ReadInputDiscretesRequest</tt>.
     * <p>
     *
     * @param ref the reference of the register to start reading from.
     */
    public void setReference(int ref) {
        if (ref < 0 || m_BitCount + ref >= 65536) {
            throw new IllegalArgumentException();
        }

        m_Reference = ref;
    }

    /**
     * Returns the number of bits (i.e. input discretes) to be read with this
     * <tt>ReadInputDiscretesRequest</tt>.
     * <p>
     *
     * @return the number of bits to be read.
     */
    public int getBitCount() {
        return m_BitCount;
    }

    /**
     * Sets the number of bits (i.e. input discretes) to be read with this
     * <tt>ReadInputDiscretesRequest</tt>.
     *
     * @param count the number of bits to be read.
     */
    public void setBitCount(int count) {
        if (count < 0 || count > 2000 || count + m_Reference >= 65536) {
            throw new IllegalArgumentException();
        }

        m_BitCount = count;
    }

    public void writeData(DataOutput dout) throws IOException {
        dout.writeShort(m_Reference);
        dout.writeShort(m_BitCount);
    }

    public void readData(DataInput din) throws IOException {
        m_Reference = din.readUnsignedShort();
        m_BitCount = din.readUnsignedShort();
    }

    public byte[] getMessage() {
        byte result[] = new byte[4];

        result[0] = (byte)((m_Reference >> 8) & 0xff);
        result[1] = (byte)((m_Reference & 0xff));
        result[2] = (byte)((m_BitCount >> 8) & 0xff);
        result[3] = (byte)((m_BitCount & 0xff));

        return result;
    }
}
