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
package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.Modbus;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class implementing a specialized <tt>InputStream</tt> which
 * decodes characters read from the raw stream into bytes.
 *
 * Note that the characters denoting start and end of a frame
 * as given by the specification are exceptions;
 * They are translated to the "virtual" FRAME_START and FRAME_END.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 * @see com.ghgande.j2mod.modbus.io.ModbusASCIITransport#FRAME_START
 * @see com.ghgande.j2mod.modbus.io.ModbusASCIITransport#FRAME_END
 */
public class ASCIIInputStream
        extends FilterInputStream {

    /**
     * Constructs a new <tt>ASCIIInputStream</tt> instance
     * reading from the given <tt>InputStream</tt>.
     *
     * @param in a base input stream to be wrapped.
     */
    public ASCIIInputStream(InputStream in) {
        super(in);
    }

    /**
     * Reads a byte from the ASCII encoded stream.
     *
     * @return int the byte read from the stream.
     *
     * @throws IOException if an I/O error occurs.
     */
    public int read() throws IOException {
        StringBuffer sbuf = new StringBuffer(2);
        int ch = in.read();
        if (ch == -1) {
            return -1;
        }
        //System.out.println("Read "+ch+ "="+(char)ch);
        sbuf.append((char)ch);
        if (sbuf.charAt(0) == ':') {
            //System.out.println("FRAME START");
            return ModbusASCIITransport.FRAME_START;
        }
        else {
            if (sbuf.charAt(0) == '\r') {
                if (in.read() == 10) {
                    //System.out.println("FRAME END");
                    return ModbusASCIITransport.FRAME_END;
                }
                else {
                    //malformed stream
                    throw new IOException("Malformed Stream No Frame Delims");
                }
            }
            else {
                try {
                    sbuf.append((char)in.read());
                    //System.out.println("Read byte: " + sbuf.toString().toLowerCase());
                    return Integer.parseInt(sbuf.toString().toLowerCase(), 16);
                }
                catch (NumberFormatException ex) {
                    //malformed stream
                    if (Modbus.debug) {
                        System.out.println(sbuf.toString());
                    }
                    throw new IOException("Malformed Stream - Wrong Characters");
                }
            }
        }
    }

}