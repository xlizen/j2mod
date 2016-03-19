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
package com.j2mod.modbus.io;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.msg.ModbusMessage;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.msg.ModbusResponse;
import com.j2mod.modbus.net.UDPTerminal;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Arrays;

/**
 * Class that implements the Modbus UDP transport
 * flavor.
 *
 * @author Dieter Wimberger
 * @version 1.0 (29/04/2002)
 */
public class ModbusUDPTransport implements ModbusTransport {

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
            throw new ModbusIOException("I/O exception - failed to write");
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

    public boolean getDebug() {
        return "true".equals(System.getProperty("com.ghgande.j2mod.modbus.debug"));
    }

}