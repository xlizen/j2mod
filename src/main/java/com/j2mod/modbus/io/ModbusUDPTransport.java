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
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.msg.ModbusMessage;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.msg.ModbusResponse;
import com.j2mod.modbus.net.UDPTerminal;
import com.j2mod.modbus.util.Logger;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Arrays;

/**
 * Class that implements the Modbus UDP transport
 * flavor.
 *
 * @author Dieter Wimberger
 * @version 1.0 (29/04/2002)
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class ModbusUDPTransport implements ModbusTransport {

    private static final Logger logger = Logger.getLogger(ModbusUDPTransport.class);

    //instance attributes
    private UDPTerminal m_Terminal;
    private BytesOutputStream m_ByteOut;
    private BytesInputStream m_ByteIn;

    /**
     * Constructs a new <tt>ModbusTransport</tt> instance,
     * for a given <tt>UDPTerminal</tt>.
     * <p>
     *
     * @param terminal the <tt>UDPTerminal</tt> used for message transport.
     */
    public ModbusUDPTransport(UDPTerminal terminal) {
        m_Terminal = terminal;
        m_ByteOut = new BytesOutputStream(Modbus.MAX_MESSAGE_LENGTH);
        m_ByteIn = new BytesInputStream(Modbus.MAX_MESSAGE_LENGTH);
    }

    public void close() throws IOException {
        //?
    }

    public ModbusTransaction createTransaction() {
        ModbusUDPTransaction trans = new ModbusUDPTransaction();
        trans.setTerminal(m_Terminal);

        return trans;
    }

    public void writeMessage(ModbusMessage msg) throws ModbusIOException {
        try {
            synchronized (m_ByteOut) {
                int len = msg.getOutputLength();
                m_ByteOut.reset();
                msg.writeTo(m_ByteOut);
                byte data[] = m_ByteOut.getBuffer();
                data = Arrays.copyOf(data, len);
                m_Terminal.sendMessage(data);
            }
        }
        catch (Exception ex) {
            throw new ModbusIOException(String.format("I/O exception - failed to write - %s", ex.getMessage()));
        }
    }

    public ModbusRequest readRequest() throws ModbusIOException {
        try {
            ModbusRequest req;
            synchronized (m_ByteIn) {
                m_ByteIn.reset(m_Terminal.receiveMessage());
                m_ByteIn.skip(7);
                int functionCode = m_ByteIn.readUnsignedByte();
                m_ByteIn.reset();
                req = ModbusRequest.createModbusRequest(functionCode);
                req.readFrom(m_ByteIn);
            }
            return req;
        }
        catch (Exception ex) {
            throw new ModbusIOException("I/O exception - failed to read");
        }
    }

    public ModbusResponse readResponse() throws ModbusIOException {

        try {
            ModbusResponse res;
            synchronized (m_ByteIn) {
                m_ByteIn.reset(m_Terminal.receiveMessage());
                m_ByteIn.skip(7);
                int functionCode = m_ByteIn.readUnsignedByte();
                m_ByteIn.reset();
                res = ModbusResponse.createModbusResponse(functionCode);
                res.readFrom(m_ByteIn);
            }
            return res;
        }
        catch (InterruptedIOException ioex) {
            throw new ModbusIOException("Socket timed out");
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new ModbusIOException("I/O exception - failed to read");
        }
    }

}