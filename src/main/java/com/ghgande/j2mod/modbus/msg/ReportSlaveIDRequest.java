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
package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.Modbus;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Class implementing a <tt>Read MEI Data</tt> request.
 *
 * @author Julie Haugh (jfh@ghgande.com)
 * @version jamod-1.2rc1-ghpc
 *
 * @author jfhaugh (jfh@ghgande.com)
 * @version @version@ (@date@)
 */
public final class ReportSlaveIDRequest extends ModbusRequest {

    /**
     * createResponse -- create an empty response for this request.
     */
    public ModbusResponse getResponse() {
        ReportSlaveIDResponse response;

        response = new ReportSlaveIDResponse();

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
     * The ModbusCoupler doesn't have a means of reporting the slave
     * state or ID information.
     */
    public ModbusResponse createResponse() {
        return createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
    }

    /**
     * writeData -- output this Modbus message to dout.
     */
    public void writeData(DataOutput dout) throws IOException {
        dout.write(getMessage());
    }

    /**
     * readData -- dummy function.  There is no data with the request.
     */
    public void readData(DataInput din) throws IOException {
    }

    /**
     * getMessage -- return an empty array as there is no data for
     * 		this request.
     */
    public byte[] getMessage() {

        return new byte[0];
    }

    /**
     * Constructs a new <tt>Report Slave ID request</tt>
     * instance.
     */
    public ReportSlaveIDRequest() {
        super();

        setFunctionCode(Modbus.REPORT_SLAVE_ID);
		
		/*
		 * There is no additional data in this request.
		 */
        setDataLength(0);
    }
}
