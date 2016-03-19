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
package com.j2mod.modbus.msg;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.ModbusCoupler;
import com.j2mod.modbus.procimg.IllegalAddressException;
import com.j2mod.modbus.procimg.InputRegister;
import com.j2mod.modbus.procimg.ProcessImage;
import com.j2mod.modbus.procimg.Register;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>Read FIFO Queue</tt> request.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 * @version jamod-1.2rc1-ghpc
 *
 * @author jfhaugh (jfh@ghgande.com)
 * @version @version@ (@date@)
 */
public final class ReadFIFOQueueRequest extends ModbusRequest {

    private int m_Reference;

    /**
     * Constructs a new <tt>Read FIFO Queue</tt> request instance.
     */
    public ReadFIFOQueueRequest() {
        super();

        setFunctionCode(Modbus.READ_FIFO_QUEUE);
        setDataLength(2);
    }

    /**
     * getReference -- get the queue register number.
     *
     * @return int
     */
    public int getReference() {
        return m_Reference;
    }

    /**
     * setReference -- set the queue register number.
     *
     * @param ref Register
     */
    public void setReference(int ref) {
        m_Reference = ref;
    }

    /**
     * getResponse -- create an empty response for this request.
     */
    public ModbusResponse getResponse() {
        ReadFIFOQueueResponse response;

        response = new ReadFIFOQueueResponse();

		/*
         * Copy any header data from the request.
		 */
        response.setHeadless(isHeadless());
        if (!isHeadless()) {
            response.setTransactionID(getTransactionID());
            response.setProtocolID(getProtocolID());
        }

		/*
         * Copy the unit ID and function code.
		 */
        response.setUnitID(getUnitID());
        response.setFunctionCode(getFunctionCode());

        return response;
    }

    /**
     * Create a response using the named register as the queue length count.
     */
    public ModbusResponse createResponse() {
        ReadFIFOQueueResponse response;
        InputRegister[] registers;

		/*
         * Get the process image.
		 */
        ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();

        try {
			/*
			 * Get the FIFO queue location and read the count of available
			 * registers.
			 */
            Register queue = procimg.getRegister(m_Reference);
            int count = queue.getValue();
            if (count < 0 || count > 31) {
                return createExceptionResponse(Modbus.ILLEGAL_VALUE_EXCEPTION);
            }

            registers = procimg.getRegisterRange(m_Reference + 1, count);
        }
        catch (IllegalAddressException e) {
            return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = (ReadFIFOQueueResponse)getResponse();
        response.setRegisters(registers);

        return response;
    }

    /**
     * writeData -- output this Modbus message to dout.
     */
    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    /**
     * readData -- read the reference word.
     */
    public void readData(DataInput din) throws IOException {
        m_Reference = din.readShort();
    }

    /**
     * getMessage -- return an empty array as there is no data for this request.
     */
    public byte[] getMessage() {
        byte results[] = new byte[2];

        results[0] = (byte)(m_Reference >> 8);
        results[1] = (byte)(m_Reference & 0xFF);

        return results;
    }
}
