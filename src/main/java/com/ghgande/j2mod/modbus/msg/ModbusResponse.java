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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Abstract class implementing a <tt>ModbusResponse</tt>. This class provides
 * specialised implementations with the functionality they have in common.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Julie Haugh
 * @version 1.2rc1-ghpc (09/27/2010) Added READ_MEI support.
 *
 * @version 1.2rc1-ghpc (04/26/2011) Added proper support for Modbus exception
 *          messages.
 */
public abstract class ModbusResponse extends ModbusMessageImpl {

    /**
     * Utility method to set the raw data of the message. Should not be used
     * except under rare circumstances.
     * <p>
     *
     * @param msg
     *            the <tt>byte[]</tt> resembling the raw modbus response
     *            message.
     */
    protected void setMessage(byte[] msg) {
        try {
            readData(new DataInputStream(new ByteArrayInputStream(msg)));
        }
        catch (IOException ex) {

        }
    }

    /**
     * Factory method creating the required specialized <tt>ModbusResponse</tt>
     * instance.
     *
     * @param functionCode
     *            the function code of the response as <tt>int</tt>.
     * @return a ModbusResponse instance specific for the given function code.
     */
    public static ModbusResponse createModbusResponse(int functionCode) {
        ModbusResponse response;

        switch (functionCode) {
            case Modbus.READ_COILS:
                response = new ReadCoilsResponse();
                break;
            case Modbus.READ_INPUT_DISCRETES:
                response = new ReadInputDiscretesResponse();
                break;
            case Modbus.READ_MULTIPLE_REGISTERS:
                response = new ReadMultipleRegistersResponse();
                break;
            case Modbus.READ_INPUT_REGISTERS:
                response = new ReadInputRegistersResponse();
                break;
            case Modbus.WRITE_COIL:
                response = new WriteCoilResponse();
                break;
            case Modbus.WRITE_SINGLE_REGISTER:
                response = new WriteSingleRegisterResponse();
                break;
            case Modbus.WRITE_MULTIPLE_COILS:
                response = new WriteMultipleCoilsResponse();
                break;
            case Modbus.WRITE_MULTIPLE_REGISTERS:
                response = new WriteMultipleRegistersResponse();
                break;
            case Modbus.READ_EXCEPTION_STATUS:
                response = new ReadExceptionStatusResponse();
                break;
            case Modbus.READ_SERIAL_DIAGNOSTICS:
                response = new ReadSerialDiagnosticsResponse();
                break;
            case Modbus.READ_COMM_EVENT_COUNTER:
                response = new ReadCommEventCounterResponse();
                break;
            case Modbus.READ_COMM_EVENT_LOG:
                response = new ReadCommEventLogResponse();
                break;
            case Modbus.REPORT_SLAVE_ID:
                response = new ReportSlaveIDResponse();
                break;
            case Modbus.READ_FILE_RECORD:
                response = new ReadFileRecordResponse();
                break;
            case Modbus.WRITE_FILE_RECORD:
                response = new WriteFileRecordResponse();
                break;
            case Modbus.MASK_WRITE_REGISTER:
                response = new MaskWriteRegisterResponse();
                break;
            case Modbus.READ_WRITE_MULTIPLE:
                response = new ReadWriteMultipleResponse();
                break;
            case Modbus.READ_FIFO_QUEUE:
                response = new ReadFIFOQueueResponse();
                break;
            case Modbus.READ_MEI:
                response = new ReadMEIResponse();
                break;
            default:
                if ((functionCode & 0x80) != 0) {
                    response = new ExceptionResponse(functionCode);
                }
                else {
                    response = new ExceptionResponse();
                }
                break;
        }
        return response;
    }
}