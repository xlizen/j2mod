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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Class implementing a byte array output stream with
 * a DataInput interface.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class BytesOutputStream extends FastByteArrayOutputStream implements DataOutput {

    private DataOutputStream m_Dout;

    /**
     * Constructs a new <tt>BytesOutputStream</tt> instance with
     * a new output buffer of the given size.
     *
     * @param size the size of the output buffer as <tt>int</tt>.
     */
    public BytesOutputStream(int size) {
        super(size);
        m_Dout = new DataOutputStream(this);
    }

    /**
     * Constructs a new <tt>BytesOutputStream</tt> instance with
     * a given output buffer.
     *
     * @param buffer the output buffer as <tt>byte[]</tt>.
     */
    public BytesOutputStream(byte[] buffer) {
        buf = buffer;
        count = 0;
        m_Dout = new DataOutputStream(this);
    }

    /**
     * Returns the reference to the output buffer.
     *
     * @return the reference to the <tt>byte[]</tt> output buffer.
     */
    public synchronized byte[] getBuffer() {
        byte[] dest = new byte[buf.length];
        System.arraycopy(buf, 0, dest, 0, dest.length);
        return dest;
    }

    public void reset() {
        count = 0;
    }

    public void writeBoolean(boolean v) throws IOException {
        m_Dout.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        m_Dout.writeByte(v);
    }

    public void writeShort(int v) throws IOException {
        m_Dout.writeShort(v);
    }

    public void writeChar(int v) throws IOException {
        m_Dout.writeChar(v);
    }

    public void writeInt(int v) throws IOException {
        m_Dout.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        m_Dout.writeLong(v);
    }

    public void writeFloat(float v) throws IOException {
        m_Dout.writeFloat(v);
    }

    public void writeDouble(double v) throws IOException {
        m_Dout.writeDouble(v);
    }

    public void writeBytes(String s) throws IOException {
        int len = s.length();
        for (int i = 0; i < len; i++) {
            this.write((byte)s.charAt(i));
        }
    }

    public void writeChars(String s) throws IOException {
        m_Dout.writeChars(s);
    }

    public void writeUTF(String str) throws IOException {
        m_Dout.writeUTF(str);
    }

}