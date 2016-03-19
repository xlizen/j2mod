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

/**
 * Abstract class implementing a <tt>ModbusRequest</tt>. This class provides
 * specialised implementations with the functionality they have in common.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author jfhaugh (jfh@ghgande.com)
 * @version 1.2rc1-ghpc (09/27/2010) Added READ_MEI stuff.
 *
 * @version 1.2rc1-ghpc (02/14/2011) Added REPORT_SLAVE_ID stuff.
 *
 * @version 1.0-jamod (7/7/2012)
 * 	Added new messages.
 */
public abstract class ModbusRequest extends ModbusMessageImpl {

    /**
     * Factory method creating the required specialized <tt>ModbusRequest</tt>
     * instance.
     *
     * @param functionCode
     *            the function code of the request as <tt>int</tt>.
     * @return a ModbusRequest instance specific for the given function type.
     */
    public static ModbusRequest createModbusRequest(int functionCode) {
        ModbusRequest request;

        switch (functionCode) {
            case Modbus.READ_COILS:
                request = new ReadCoilsRequest();
                break;
            case Modbus.READ_INPUT_DISCRETES:
                request = new ReadInputDiscretesRequest();
                break;
            case Modbus.READ_MULTIPLE_REGISTERS:
                request = new ReadMultipleRegistersRequest();
                break;
            case Modbus.READ_INPUT_REGISTERS:
                request = new ReadInputRegistersRequest();
                break;
            case Modbus.WRITE_COIL:
                request = new WriteCoilRequest();
                break;
            case Modbus.WRITE_SINGLE_REGISTER:
                request = new WriteSingleRegisterRequest();
                break;
            case Modbus.WRITE_MULTIPLE_COILS:
                request = new WriteMultipleCoilsRequest();
                break;
            case Modbus.WRITE_MULTIPLE_REGISTERS:
                request = new WriteMultipleRegistersRequest();
                break;
            case Modbus.READ_EXCEPTION_STATUS:
                request = new ReadExceptionStatusRequest();
                break;
            case Modbus.READ_SERIAL_DIAGNOSTICS:
                request = new ReadSerialDiagnosticsRequest();
                break;
            case Modbus.READ_COMM_EVENT_COUNTER:
                request = new ReadCommEventCounterRequest();
                break;
            case Modbus.READ_COMM_EVENT_LOG:
                request = new ReadCommEventLogRequest();
                break;
            case Modbus.REPORT_SLAVE_ID:
                request = new ReportSlaveIDRequest();
                break;
            case Modbus.READ_FILE_RECORD:
                request = new ReadFileRecordRequest();
                break;
            case Modbus.WRITE_FILE_RECORD:
                request = new WriteFileRecordRequest();
                break;
            case Modbus.MASK_WRITE_REGISTER:
                request = new MaskWriteRegisterRequest();
                break;
            case Modbus.READ_WRITE_MULTIPLE:
                request = new ReadWriteMultipleRequest();
                break;
            case Modbus.READ_FIFO_QUEUE:
                request = new ReadFIFOQueueRequest();
                break;
            case Modbus.READ_MEI:
                request = new ReadMEIRequest();
                break;
            default:
                request = new IllegalFunctionRequest(functionCode);
                break;
        }
        return request;
    }

    /**
     * Returns the <tt>ModbusResponse</tt> that correlates with this
     * <tt>ModbusRequest</tt>.
     *
     * <p>
     * The response must include the unit number, function code, as well as any
     * transport-specific header information.
     *
     * <p>
     * This method is used to create an empty response which must be populated
     * by the caller. It is commonly used to un-marshal responses from Modbus
     * slaves.
     *
     * @return the corresponding <tt>ModbusResponse</tt>.
     */
    public abstract ModbusResponse getResponse();

    /**
     * Returns the <tt>ModbusResponse</tt> that represents the answer to this
     * <tt>ModbusRequest</tt>.
     *
     * <p>
     * The implementation should take care about assembling the reply to this
     * <tt>ModbusRequest</tt>.
     *
     * <p>
     * This method is used to create responses from the process image associated
     * with the ModbusCoupler. It is commonly used to implement Modbus slave
     * instances.
     *
     * @return the corresponding <tt>ModbusResponse</tt>.
     */
    public abstract ModbusResponse createResponse();

    /**
     * Factory method for creating exception responses with the given exception
     * code.
     *
     * @param code
     *            the code of the exception.
     * @return a ModbusResponse instance representing the exception response.
     */
    public ModbusResponse createExceptionResponse(int code) {
        ExceptionResponse response = new ExceptionResponse(getFunctionCode(), code);
        if (!isHeadless()) {
            response.setTransactionID(getTransactionID());
            response.setProtocolID(getProtocolID());
        }
        else {
            response.setHeadless();
        }
        response.setUnitID(getUnitID());
        return response;
    }
}
