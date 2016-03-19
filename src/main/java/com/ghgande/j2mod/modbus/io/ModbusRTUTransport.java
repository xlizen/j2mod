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
package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusMessage;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.util.ModbusUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class that implements the ModbusRTU transport flavor.
 *
 * @author John Charlton
 * @author Dieter Wimberger
 * @author Julie Haugh
 * @version 1.05
 *
 *          20140426 - Implement serial slave support
 */
public class ModbusRTUTransport extends ModbusSerialTransport {

    private InputStream m_InputStream; // wrap into filter input
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
     * Writes the Modbus message to the comms port
     *
     * @param msg a <code>ModbusMessage</code> value
     *
     * @throws ModbusIOException If an error occurred bundling the message
     */
    public void writeMessage(ModbusMessage msg) throws ModbusIOException {
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
                m_CommPort.writeBytes(m_ByteOut.getBuffer(), m_ByteOut.size());
                if (Modbus.debug) {
                    System.err.println("Sent: " + ModbusUtil.toHex(m_ByteOut.getBuffer(), 0, m_ByteOut.size()));
                }
                // clears out the echoed message
                // for RS485
                if (m_Echo) {
                    readEcho(len);
                }
                lastRequest = new byte[len];
                System.arraycopy(m_ByteOut.getBuffer(), 0, lastRequest, 0, m_ByteOut.size());
            }
        }
        catch (Exception ex) {
            throw new ModbusIOException("I/O failed to write");
        }
    }

    /**
     * readRequestData -
     *
     * @throws IOException
     */

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
        int read = m_CommPort.readBytes(inpBuf, byteCount);
        out.write(inpBuf, 0, read);
        if (Modbus.debug && read != byteCount) {
            System.err.println("Error: looking for " + byteCount + " bytes, received " + read);
        }
    }

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
                        byteCount = m_InputStream.read();
                        out.write(byteCount);
                        readRequestData(byteCount, out);
                        break;

                    case Modbus.WRITE_MULTIPLE_COILS:
                    case Modbus.WRITE_MULTIPLE_REGISTERS:
                        byteCount = m_InputStream.read(inpBuf, 0, 4);
                        if (byteCount > 0) {
                            out.write(inpBuf, 0, byteCount);
                        }
                        byteCount = m_InputStream.read();
                        out.write(byteCount);
                        readRequestData(byteCount, out);
                        break;

                    case Modbus.READ_WRITE_MULTIPLE:
                        readRequestData(8, out);
                        byteCount = m_InputStream.read();
                        out.write(byteCount);
                        readRequestData(byteCount, out);
                        break;
                }
            }
        }
        catch (IOException e) {
            throw new IOException("getResponse serial port exception");
        }
    }

    /**
     * readRequest - Read a slave request.
     *
     * @return a <tt>ModbusRequest</tt> to be processed by the slave simulator
     */
    public ModbusRequest readRequest() throws ModbusIOException {
        ModbusCoupler coupler = ModbusCoupler.getReference();

        if (coupler == null || coupler.isMaster()) {
            throw new RuntimeException("Operation not supported.");
        }

        boolean done;
        ModbusRequest request;
        int dlength;

        try {
            do {
                // 1. read to function code, create request and read function
                // specific bytes
                synchronized (m_ByteIn) {
                    int uid = m_InputStream.read();
                    if (uid != -1) {
                        int fc = m_InputStream.read();
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
                        if (Modbus.debug) {
                            System.err.println("Response: " + ModbusUtil.toHex(m_ByteInOut.getBuffer(), 0, dlength + 2));
                        }

                        m_ByteIn.reset(m_InBuffer, dlength);

                        // check CRC
                        int[] crc = ModbusUtil.calculateCRC(m_InBuffer, 0, dlength); // does not include CRC
                        if (ModbusUtil.unsignedByteToInt(m_InBuffer[dlength]) != crc[0] &&
                                ModbusUtil.unsignedByteToInt(m_InBuffer[dlength + 1]) != crc[1]) {
                            if (Modbus.debug) {
                                System.err.println("CRC should be " + crc[0] + ", " + crc[1]);
                            }

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
        catch (Exception ex) {
			/*
			 * An exception mostly means there is no request. The master should
			 * retry the request.
			 */
            return null;
        }
    }

    /**
     * clearInput - Clear the input if characters are found in the input stream.
     *
     * @throws IOException
     */
    public void clearInput() throws IOException {
        if (m_InputStream.available() > 0) {
            int len = m_InputStream.available();
            byte buf[] = new byte[len];
            m_InputStream.read(buf, 0, len);
            if (Modbus.debug) {
                System.err.println("Clear input: " + ModbusUtil.toHex(buf, 0, len));
            }
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
        int byteCount;
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
                        byteCount = m_CommPort.readBytes(inpBuf, 1);
                        out.write(byteCount);
                        readRequestData(byteCount, out);
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
                        b1 = (byte)(m_InputStream.read() & 0xFF);
                        out.write(b1);
                        b2 = (byte)(m_InputStream.read() & 0xFF);
                        out.write(b2);
                        byteCount = ModbusUtil.makeWord(b1, b2);
                        readRequestData(byteCount, out);
                        break;

                    case Modbus.READ_MEI:
                        // read the subcode. We only support 0x0e.
                        int sc = m_InputStream.read();
                        if (sc != 0x0e) {
                            throw new IOException("Invalid subfunction code");
                        }
                        out.write(sc);
                        // next few bytes are just copied.
                        int id, fieldCount;
                        int cnt = m_InputStream.read(inpBuf, 0, 5);
                        out.write(inpBuf, 0, cnt);
                        fieldCount = (int)inpBuf[4];
                        for (int i = 0; i < fieldCount; i++) {
                            id = m_InputStream.read();
                            out.write(id);
                            int len = m_InputStream.read();
                            out.write(len);
                            len = m_InputStream.read(inpBuf, 0, len);
                            out.write(inpBuf, 0, len);
                        }
                        if (fieldCount == 0) {
                            int err = m_InputStream.read();
                            out.write(err);
                        }
                        // now get the 2 CRC bytes
                        readRequestData(0, out);
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
     * readResponse - Read the bytes for the response from the slave.
     *
     * @return a <tt>ModbusRespose</tt>
     */
    public ModbusResponse readResponse() throws ModbusIOException {
        boolean done;
        ModbusResponse response;
        int dlength;

        try {
            do {
                // 1. read to function code, create request and read function
                // specific bytes
                synchronized (m_ByteIn) {
                    int uid = m_InputStream.read();
                    if (uid != -1) {
                        int fc = m_InputStream.read();
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
                        if (Modbus.debug) {
                            System.err.println("Response: " + ModbusUtil.toHex(m_ByteInOut.getBuffer(), 0, dlength + 2));
                        }
                        m_ByteIn.reset(m_InBuffer, dlength);

                        // check CRC
                        int[] crc = ModbusUtil.calculateCRC(m_InBuffer, 0, dlength); // does not include CRC
                        if (ModbusUtil.unsignedByteToInt(m_InBuffer[dlength]) != crc[0] && ModbusUtil.unsignedByteToInt(m_InBuffer[dlength + 1]) != crc[1]) {
                            if (Modbus.debug) {
                                System.err.println("CRC should be " + crc[0] + ", " + crc[1]);
                            }
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
        catch (Exception ex) {
            if (Modbus.debug) {
                System.err.println("Last request: " + ModbusUtil.toHex(lastRequest));
                System.err.println(ex.getMessage());
            }
            throw new ModbusIOException("I/O exception - failed to read");
        }
    }

    /**
     * prepareStreams - Prepares the input and output streams of this
     * <tt>ModbusRTUTransport</tt> instance.
     *
     * @param in  the input stream to be read from.
     * @param out the output stream to write to.
     *
     * @throws IOException if an I\O error occurs.
     */
    public void prepareStreams(InputStream in, OutputStream out) throws IOException {
        m_InputStream = in;
    }

    /**
     * Closes the comms port and any streams associated with it
     *
     * @throws IOException
     */
    public void close() throws IOException {
        m_CommPort.closePort();
    }
}
