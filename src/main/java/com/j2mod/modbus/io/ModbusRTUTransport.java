/*
 * Copyright 2002-2016 jamod & j2mod development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.j2mod.modbus.io;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.ModbusCoupler;
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.msg.ModbusMessage;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.msg.ModbusResponse;
import com.j2mod.modbus.util.Logger;
import com.j2mod.modbus.util.ModbusUtil;

import java.io.IOException;

/**
 * Class that implements the ModbusRTU transport flavor.
 *
 * @author John Charlton
 * @author Dieter Wimberger
 * @author Julie Haugh
 * @version 1.05
 *
 *          20140426 - Implement serial slave support
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class ModbusRTUTransport extends ModbusSerialTransport {

    private static final Logger logger = Logger.getLogger(ModbusRTUTransport.class);

    private final byte[] m_InBuffer = new byte[Modbus.MAX_MESSAGE_LENGTH];
    private final BytesInputStream m_ByteIn = new BytesInputStream(m_InBuffer); // to read message from
    private final BytesOutputStream m_ByteInOut = new BytesOutputStream(m_InBuffer); // to buffer message to
    private final BytesOutputStream m_ByteOut = new BytesOutputStream(Modbus.MAX_MESSAGE_LENGTH); // write frames
    private byte[] lastRequest = null;

    /**
     * Creates a suitable transaction
     *
     * @return Transaction
     */
    public ModbusTransaction createTransaction() {
        ModbusSerialTransaction transaction = new ModbusSerialTransaction();
        transaction.setTransport(this);
        return transaction;
    }

    /**
     * Read the data for a request of a given fixed size
     *
     * @param byteCount Byte count excluding the 2 byte CRC
     * @param out       Output buffer to populate
     *
     * @throws IOException
     */
    private void readRequestData(int byteCount, BytesOutputStream out) throws IOException {
        byteCount += 2;
        byte inpBuf[] = new byte[byteCount];
        readBytes(inpBuf, byteCount);
        out.write(inpBuf, 0, byteCount);
    }

    /**
     * readRequestData -
     *
     * @throws IOException
     */

    /**
     * getRequest - Read a request, after the unit and function code
     *
     * @param function - Modbus function code
     * @param out      - Byte stream buffer to hold actual message
     */
    private void getRequest(int function, BytesOutputStream out) throws IOException {
        int byteCount;
        byte inpBuf[] = new byte[256];
        try {
            if ((function & 0x80) == 0) {
                switch (function) {
                    case Modbus.READ_EXCEPTION_STATUS:
                    case Modbus.READ_COMM_EVENT_COUNTER:
                    case Modbus.READ_COMM_EVENT_LOG:
                    case Modbus.REPORT_SLAVE_ID:
                        readRequestData(0, out);
                        break;

                    case Modbus.READ_FIFO_QUEUE:
                        readRequestData(2, out);
                        break;

                    case Modbus.READ_MEI:
                        readRequestData(3, out);
                        break;

                    case Modbus.READ_COILS:
                    case Modbus.READ_INPUT_DISCRETES:
                    case Modbus.READ_MULTIPLE_REGISTERS:
                    case Modbus.READ_INPUT_REGISTERS:
                    case Modbus.WRITE_COIL:
                    case Modbus.WRITE_SINGLE_REGISTER:
                        readRequestData(4, out);
                        break;
                    case Modbus.MASK_WRITE_REGISTER:
                        readRequestData(6, out);
                        break;

                    case Modbus.READ_FILE_RECORD:
                    case Modbus.WRITE_FILE_RECORD:
                        byteCount = readByte();
                        out.write(byteCount);
                        readRequestData(byteCount, out);
                        break;

                    case Modbus.WRITE_MULTIPLE_COILS:
                    case Modbus.WRITE_MULTIPLE_REGISTERS:
                        readBytes(inpBuf, 4);
                        out.write(inpBuf, 0, 4);
                        byteCount = readByte();
                        out.write(byteCount);
                        readRequestData(byteCount, out);
                        break;

                    case Modbus.READ_WRITE_MULTIPLE:
                        readRequestData(8, out);
                        byteCount = readByte();
                        out.write(byteCount);
                        readRequestData(byteCount, out);
                        break;

                    default:
                        throw new IOException(String.format("getResponse unrecognised function code [%s]", function));
                }
            }
        }
        catch (IOException e) {
            throw new IOException("getResponse serial port exception");
        }
    }

    /**
     * getResponse - Read a <tt>ModbusResponse</tt> from a slave.
     *
     * @param function The function code of the request
     * @param out      The output buffer to put the result
     *
     * @throws IOException
     */
    private void getResponse(int function, BytesOutputStream out) throws IOException {
        byte inpBuf[] = new byte[256];
        try {
            if ((function & 0x80) == 0) {
                switch (function) {
                    case Modbus.READ_COILS:
                    case Modbus.READ_INPUT_DISCRETES:
                    case Modbus.READ_MULTIPLE_REGISTERS:
                    case Modbus.READ_INPUT_REGISTERS:
                    case Modbus.READ_COMM_EVENT_LOG:
                    case Modbus.REPORT_SLAVE_ID:
                    case Modbus.READ_FILE_RECORD:
                    case Modbus.WRITE_FILE_RECORD:
                    case Modbus.READ_WRITE_MULTIPLE:
                        /*
                         * Read the data payload byte count. There will be two
                         * additional CRC bytes afterwards.
                         */
                        int cnt = readByte();
                        out.write(cnt);
                        readRequestData(cnt, out);
                        break;

                    case Modbus.WRITE_COIL:
                    case Modbus.WRITE_SINGLE_REGISTER:
                    case Modbus.READ_COMM_EVENT_COUNTER:
                    case Modbus.WRITE_MULTIPLE_COILS:
                    case Modbus.WRITE_MULTIPLE_REGISTERS:
                    case Modbus.READ_SERIAL_DIAGNOSTICS:
                        /*
                         * read status: only the CRC remains after the two data
                         * words.
                         */
                        readRequestData(4, out);
                        break;

                    case Modbus.READ_EXCEPTION_STATUS:
                        /*
                         * read status: only the CRC remains after exception status
                         * byte.
                         */
                        readRequestData(1, out);
                        break;

                    case Modbus.MASK_WRITE_REGISTER:
                        // eight bytes in addition to the address and function codes
                        readRequestData(6, out);
                        break;

                    case Modbus.READ_FIFO_QUEUE:
                        int b1, b2;
                        b1 = (byte)(readByte() & 0xFF);
                        out.write(b1);
                        b2 = (byte)(readByte() & 0xFF);
                        out.write(b2);
                        int byteCount = ModbusUtil.makeWord(b1, b2);
                        readRequestData(byteCount, out);
                        break;

                    case Modbus.READ_MEI:
                        // read the subcode. We only support 0x0e.
                        int sc = readByte();
                        if (sc != 0x0e) {
                            throw new IOException("Invalid subfunction code");
                        }
                        out.write(sc);
                        // next few bytes are just copied.
                        int id, fieldCount;
                        readBytes(inpBuf, 5);
                        out.write(inpBuf, 0, 5);
                        fieldCount = (int)inpBuf[4];
                        for (int i = 0; i < fieldCount; i++) {
                            id = readByte();
                            out.write(id);
                            int len = readByte();
                            out.write(len);
                            readBytes(inpBuf, len);
                            out.write(inpBuf, 0, len);
                        }
                        if (fieldCount == 0) {
                            int err = readByte();
                            out.write(err);
                        }
                        // now get the 2 CRC bytes
                        readRequestData(0, out);
                        break;

                    default:
                        throw new IOException(String.format("getResponse unrecognised function code [%s]", function));

                }
            }
            else {
                // read the exception code, plus two CRC bytes.
                readRequestData(1, out);

            }
        }
        catch (IOException e) {
            throw new IOException("getResponse serial port exception");
        }
    }

    /**
     * Writes the Modbus message to the comms port
     *
     * @param msg a <code>ModbusMessage</code> value
     *
     * @throws ModbusIOException If an error occurred bundling the message
     */
    protected void writeMessageOut(ModbusMessage msg) throws ModbusIOException {
        try {
            int len;
            synchronized (m_ByteOut) {
                // first clear any input from the receive buffer to prepare
                // for the reply since RTU doesn't have message delimiters
                clearInput();
                // write message to byte out
                m_ByteOut.reset();
                msg.setHeadless();
                msg.writeTo(m_ByteOut);
                len = m_ByteOut.size();
                int[] crc = ModbusUtil.calculateCRC(m_ByteOut.getBuffer(), 0, len);
                m_ByteOut.writeByte(crc[0]);
                m_ByteOut.writeByte(crc[1]);
                // write message
                writeBytes(m_ByteOut.getBuffer(), m_ByteOut.size());
                logger.debug("Sent: %s", ModbusUtil.toHex(m_ByteOut.getBuffer(), 0, m_ByteOut.size()));
                // clears out the echoed message
                // for RS485
                if (m_Echo) {
                    readEcho(len);
                }
                lastRequest = new byte[len];
                System.arraycopy(m_ByteOut.getBuffer(), 0, lastRequest, 0, len);
            }
        }
        catch (IOException ex) {
            throw new ModbusIOException("I/O failed to write");
        }
    }

    /**
     * readRequest - Read a slave request.
     *
     * @return a <tt>ModbusRequest</tt> to be processed by the slave simulator
     */
    protected ModbusRequest readRequestIn() throws ModbusIOException {
        ModbusCoupler coupler = ModbusCoupler.getReference();

        if (coupler == null || coupler.isMaster()) {
            throw new RuntimeException("Operation not supported");
        }

        boolean done;
        ModbusRequest request;
        int dlength;

        try {
            do {
                // 1. read to function code, create request and read function
                // specific bytes
                synchronized (m_ByteIn) {
                    int uid = readByte();
                    if (uid != -1) {
                        int fc = readByte();
                        m_ByteInOut.reset();
                        m_ByteInOut.writeByte(uid);
                        m_ByteInOut.writeByte(fc);

                        // create response to acquire length of message
                        request = ModbusRequest.createModbusRequest(fc);
                        request.setHeadless();

						/*
                         * With Modbus RTU, there is no end frame. Either we
						 * assume the message is complete as is or we must do
						 * function specific processing to know the correct
						 * length. To avoid moving frame timing to the serial
						 * input functions, we set the timeout and to message
						 * specific parsing to read a response.
						 */
                        getRequest(fc, m_ByteInOut);
                        dlength = m_ByteInOut.size() - 2; // less the crc
                        logger.debug("Response: %s", ModbusUtil.toHex(m_ByteInOut.getBuffer(), 0, dlength + 2));

                        m_ByteIn.reset(m_InBuffer, dlength);

                        // check CRC
                        int[] crc = ModbusUtil.calculateCRC(m_InBuffer, 0, dlength); // does not include CRC
                        if (ModbusUtil.unsignedByteToInt(m_InBuffer[dlength]) != crc[0] &&
                                ModbusUtil.unsignedByteToInt(m_InBuffer[dlength + 1]) != crc[1]) {
                            logger.debug("CRC should be %d, %d", crc[0], crc[1]);

							/*
                             * Drain the input in case the frame was misread and more
							 * was to follow.
							 */
                            clearInput();
                            throw new IOException("CRC Error in received frame: " + dlength + " bytes: " + ModbusUtil.toHex(m_ByteIn.getBuffer(), 0, dlength));
                        }
                    }
                    else {
                        throw new IOException("Error reading response");
                    }

                    // read response
                    m_ByteIn.reset(m_InBuffer, dlength);
                    request.readFrom(m_ByteIn);
                    done = true;
                }
            } while (!done);
            return request;
        }
        catch (IOException ex) {
			/*
			 * An exception mostly means there is no request. The master should
			 * retry the request.
			 */
            return null;
        }
    }

    /**
     * readResponse - Read the bytes for the response from the slave.
     *
     * @return a <tt>ModbusRespose</tt>
     */
    protected ModbusResponse readResponseIn() throws ModbusIOException {
        boolean done;
        ModbusResponse response;
        int dlength;

        try {
            do {
                // 1. read to function code, create request and read function
                // specific bytes
                synchronized (m_ByteIn) {
                    int uid = readByte();
                    if (uid != -1) {
                        int fc = readByte();
                        m_ByteInOut.reset();
                        m_ByteInOut.writeByte(uid);
                        m_ByteInOut.writeByte(fc);

                        // create response to acquire length of message
                        response = ModbusResponse.createModbusResponse(fc);
                        response.setHeadless();

						/*
						 * With Modbus RTU, there is no end frame. Either we
						 * assume the message is complete as is or we must do
						 * function specific processing to know the correct
						 * length. To avoid moving frame timing to the serial
						 * input functions, we set the timeout and to message
						 * specific parsing to read a response.
						 */
                        getResponse(fc, m_ByteInOut);
                        dlength = m_ByteInOut.size() - 2; // less the crc
                        logger.debug("Response: %s", ModbusUtil.toHex(m_ByteInOut.getBuffer(), 0, dlength + 2));
                        m_ByteIn.reset(m_InBuffer, dlength);

                        // check CRC
                        int[] crc = ModbusUtil.calculateCRC(m_InBuffer, 0, dlength); // does not include CRC
                        if (ModbusUtil.unsignedByteToInt(m_InBuffer[dlength]) != crc[0] && ModbusUtil.unsignedByteToInt(m_InBuffer[dlength + 1]) != crc[1]) {
                            logger.debug("CRC should be %d, %d", crc[0], crc[1]);
                            throw new IOException("CRC Error in received frame: " + dlength + " bytes: " + ModbusUtil.toHex(m_ByteIn.getBuffer(), 0, dlength));
                        }
                    }
                    else {
                        throw new IOException("Error reading response");
                    }

                    // read response
                    m_ByteIn.reset(m_InBuffer, dlength);
                    response.readFrom(m_ByteIn);
                    done = true;
                }
            } while (!done);
            return response;
        }
        catch (IOException ex) {
            logger.error("Last request: %s", ModbusUtil.toHex(lastRequest));
            logger.error(ex.getMessage());
            throw new ModbusIOException("I/O exception - failed to read");
        }
    }
}
