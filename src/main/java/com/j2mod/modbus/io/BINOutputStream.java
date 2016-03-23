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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class implementing a specialized <tt>OutputStream</tt> which
 * duplicates bytes written to the stream that resemble a
 * frame token.
 *
 * Note that the "virtual" characters FRAME_START and FRAME_END
 * are exceptions, they are translated to the respective tokens
 * as given by the specification.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 * @see ModbusBINTransport#FRAME_START
 * @see ModbusBINTransport#FRAME_END
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class BINOutputStream
        extends FilterOutputStream {

    /**
     * Constructs a new <tt>BINOutputStream</tt> instance
     * writing to the given <tt>OutputStream</tt>.
     *
     * @param out a base output stream instance to be wrapped.
     */
    public BINOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Writes a byte to the raw output stream.
     * Bytes resembling a frame token will be duplicated.
     *
     * @param b the byte to be written as <tt>int</tt>.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    public void write(int b) throws IOException {
        if (b == ModbusASCIITransport.FRAME_START) {
            out.write(ModbusBINTransport.FRAME_START_TOKEN);
        }
        else if (b == ModbusASCIITransport.FRAME_END) {
            out.write(ModbusBINTransport.FRAME_END_TOKEN);
        }
        else if (b == ModbusBINTransport.FRAME_START_TOKEN || b == ModbusBINTransport.FRAME_END_TOKEN) {
            out.write(b);
            out.write(b);
        }
    }

    /**
     * Writes an array of bytes to the raw output stream.
     * Bytes resembling a frame token will be duplicated.
     *
     * @param data the <tt>byte[]</tt> to be written.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    public void write(byte[] data) throws IOException {
        for (byte aData : data) {
            write(aData);
        }
    }

    /**
     * Writes an array of bytes to the raw output stream.
     * Bytes resembling a frame token will be duplicated.  *
     *
     * @param data the <tt>byte[]</tt> to be written.
     * @param off  the offset into the data to start writing from.
     * @param len  the number of bytes to be written from off.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    public void write(byte[] data, int off, int len) throws IOException {
        for (int i = off; i < len; i++) {
            write(data[i]);
        }
    }
}