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
 * Class that implements the Modbus/ASCII transport
 * flavor.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class ModbusASCIITransport extends ModbusSerialTransport {

    private static final Logger logger = Logger.getLogger(ModbusASCIITransport.class);
    private final byte[] m_InBuffer = new byte[Modbus.MAX_MESSAGE_LENGTH];
    private final BytesInputStream m_ByteIn = new BytesInputStream(m_InBuffer);         //to read message from
    private final BytesOutputStream m_ByteInOut = new BytesOutputStream(m_InBuffer);     //to buffer message to
    private final BytesOutputStream m_ByteOut = new BytesOutputStream(Modbus.MAX_MESSAGE_LENGTH);      //write frames

    /**
     * Constructs a new <tt>MobusASCIITransport</tt> instance.
     */
    public ModbusASCIITransport() {
    }

    protected void writeMessageOut(ModbusMessage msg) throws ModbusIOException {

        try {
            synchronized (m_ByteOut) {
                //write message to byte out
                msg.setHeadless();
                msg.writeTo(m_ByteOut);
                byte[] buf = m_ByteOut.getBuffer();
                int len = m_ByteOut.size();

                //write message
                writeAsciiByte(FRAME_START);               //FRAMESTART
                writeAsciiBytes(buf, len);                 //PDU
                logger.debug("Writing: %s", ModbusUtil.toHex(buf, 0, len));
                writeAsciiByte(calculateLRC(buf, 0, len)); //LRC
                writeAsciiByte(FRAME_END);                 //FRAMEEND
                m_ByteOut.reset();
                // clears out the echoed message
                // for RS485
                if (m_Echo) {
                    // read back the echoed message
                    readEcho(len + 3);
                }
            }
        }
        catch (IOException ex) {
            throw new ModbusIOException("I/O failed to write");
        }
    }

    public ModbusRequest readRequestIn() throws ModbusIOException {

        boolean done = false;
        ModbusRequest request = null;

        int in;

        try {
            do {
                //1. Skip to FRAME_START
                while ((readAsciiByte()) != FRAME_START) {
                    // Nothing to do
                }

                //2. Read to FRAME_END
                synchronized (m_InBuffer) {
                    m_ByteInOut.reset();
                    while ((in = readAsciiByte()) != FRAME_END) {
                        if (in == -1) {
                            throw new IOException("I/O exception - Serial port timeout");
                        }
                        m_ByteInOut.writeByte(in);
                    }
                    //check LRC
                    if (m_InBuffer[m_ByteInOut.size() - 1] != calculateLRC(m_InBuffer, 0, m_ByteInOut.size(), 1)) {
                        continue;
                    }
                    m_ByteIn.reset(m_InBuffer, m_ByteInOut.size());
                    in = m_ByteIn.readUnsignedByte();
                    //check message with this slave unit identifier
                    if (in != ModbusCoupler.getReference().getUnitID()) {
                        continue;
                    }
                    in = m_ByteIn.readUnsignedByte();
                    //create request
                    request = ModbusRequest.createModbusRequest(in);
                    request.setHeadless();
                    //read message
                    m_ByteIn.reset(m_InBuffer, m_ByteInOut.size());
                    request.readFrom(m_ByteIn);
                }
                done = true;
            } while (!done);
            return request;
        }
        catch (Exception ex) {
            logger.debug(ex.getMessage());
            throw new ModbusIOException("I/O exception - failed to read");
        }

    }

    protected ModbusResponse readResponseIn() throws ModbusIOException {

        boolean done = false;
        ModbusResponse response = null;
        int in;

        try {
            do {
                //1. Skip to FRAME_START
                while ((in = readAsciiByte()) != FRAME_START) {
                    if (in == -1) {
                        throw new IOException("I/O exception - Serial port timeout");
                    }
                }
                //2. Read to FRAME_END
                synchronized (m_InBuffer) {
                    m_ByteInOut.reset();
                    while ((in = readAsciiByte()) != FRAME_END) {
                        if (in == -1) {
                            throw new IOException("I/O exception - Serial port timeout");
                        }
                        m_ByteInOut.writeByte(in);
                    }
                    int len = m_ByteInOut.size();
                    logger.debug("Received: %s", ModbusUtil.toHex(m_InBuffer, 0, len));
                    //check LRC
                    if (m_InBuffer[len - 1] != calculateLRC(m_InBuffer, 0, len, 1)) {
                        continue;
                    }

                    m_ByteIn.reset(m_InBuffer, m_ByteInOut.size());
                    m_ByteIn.readUnsignedByte();
                    // JDC: To check slave unit identifier in a response we need to know
                    // the slave id in the request.  This is not tracked since slaves
                    // only respond when a master request is made and there is only one
                    // master.  We are the only master, so we can assume that this
                    // response message is from the slave responding to the last request.
                    in = m_ByteIn.readUnsignedByte();
                    //create request
                    response = ModbusResponse.createModbusResponse(in);
                    response.setHeadless();
                    //read message
                    m_ByteIn.reset(m_InBuffer, m_ByteInOut.size());
                    response.readFrom(m_ByteIn);
                }
                done = true;
            } while (!done);
            return response;
        }
        catch (Exception ex) {
            logger.debug(ex.getMessage());
            throw new ModbusIOException("I/O exception - failed to read");
        }
    }

    private static int calculateLRC(byte[] data, int off, int length) {
        return calculateLRC(data, off, length, 0);
    }

    private static byte calculateLRC(byte[] data, int off, int length, int tailskip) {
        int lrc = 0;
        for (int i = off; i < length - tailskip; i++) {
            lrc += ((int)data[i]) & 0xFF;
        }
        return (byte)((-lrc) & 0xff);
    }

}
