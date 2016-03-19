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
 * along with j2mod.  If not, see <http://www.gnu.org/licenses
 */
package com.j2mod.modbus.io;

import com.j2mod.modbus.util.Logger;
import com.j2mod.modbus.util.ModbusUtil;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class implementing a specialized <tt>OutputStream</tt> which
 * encodes bytes written to the stream into two hexadecimal
 * characters each.
 * Note that the "virtual" characters FRAME_START and FRAME_END
 * are exceptions, they are translated to the respective characters
 * as given by the specification.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 * @see com.j2mod.modbus.io.ModbusASCIITransport#FRAME_START
 * @see com.j2mod.modbus.io.ModbusASCIITransport#FRAME_END
 */
public class ASCIIOutputStream extends FilterOutputStream {

    private static final Logger logger = Logger.getLogger(ASCIIOutputStream.class);

    /**
     * Constructs a new <tt>ASCIIOutputStream</tt> instance
     * writing to the given <tt>OutputStream</tt>.
     *
     * @param out a base output stream instance to be wrapped.
     */
    public ASCIIOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Writes a byte encoded as two hexadecimal characters to
     * the raw output stream.
     *
     * @param b the byte to be written as <tt>int</tt>.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void write(int b) throws IOException {
        if (b == ModbusASCIITransport.FRAME_START) {
            out.write(58);
            logger.debug("Wrote FRAME_START");
        }
        else if (b == ModbusASCIITransport.FRAME_END) {
            out.write(13);
            out.write(10);
            logger.debug("Wrote FRAME_END");
        }
        else {
            out.write(ModbusUtil.toHex(b));
            logger.debug("Wrote byte "+b+"="+new String(ModbusUtil.toHex(b)));
        }
    }

    /**
     * Writes an array of bytes encoded as two hexadecimal
     * characters to the raw output stream.
     *
     * @param data the <tt>byte[]</tt> to be written.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void write(byte[] data) throws IOException {
        for (byte aData : data) {
            write(aData);
        }
    }

    /**
     * Writes an array of bytes encoded as two hexadecimal
     * characters to the raw output stream.
     *
     * @param data the <tt>byte[]</tt> to be written.
     * @param off  the offset into the data to start writing from.
     * @param len  the number of bytes to be written from off.
     *
     * @throws IOException if an I/O error occurs.
     */
    public void write(byte[] data, int off, int len) throws IOException {
        for (int i = off; i < len; i++) {
            write(data[i]);
        }
    }

}