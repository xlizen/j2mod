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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class implementing a specialized <tt>InputStream</tt> which
 * handles binary transmitted messages.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 * @see ModbusBINTransport#FRAME_START
 * @see ModbusBINTransport#FRAME_END
 */
public class BINInputStream
        extends FilterInputStream {

    /**
     * Constructs a new <tt>BINInputStream</tt> instance
     * reading from the given <tt>InputStream</tt>.
     *
     * @param in a base input stream to be wrapped.
     */
    public BINInputStream(InputStream in) {
        super(in);
        if (!in.markSupported()) {
            throw new RuntimeException("Accepts only input streams that support marking");
        }
    }

    /**
     * Reads a byte from the BIN encoded stream.
     *
     * @return int the byte read from the stream.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    public int read() throws IOException {
        int ch = in.read();
        if (ch == -1) {
            return -1;
        }
        else if (ch == ModbusBINTransport.FRAME_START_TOKEN) {
            in.mark(1);
            //read next
            ch = in.read();
            if (ch == ModbusBINTransport.FRAME_START_TOKEN) {
                return ch;
            }
            else {
                in.reset();
                return ModbusBINTransport.FRAME_START;
            }
        }
        else if (ch == ModbusBINTransport.FRAME_END_TOKEN) {
            in.mark(1);
            //read next
            ch = in.read();
            if (ch == ModbusBINTransport.FRAME_END_TOKEN) {
                return ch;
            }
            else {
                in.reset();
                return ModbusBINTransport.FRAME_END;
            }
        }
        else {
            return ch;
        }
    }

}