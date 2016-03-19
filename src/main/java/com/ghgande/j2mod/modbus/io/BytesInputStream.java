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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Class implementing a byte array input stream with
 * a DataInput interface.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class BytesInputStream
        extends FastByteArrayInputStream
        implements DataInput {

    DataInputStream m_Din;

    /**
     * Constructs a new <tt>BytesInputStream</tt> instance,
     * with an empty buffer of a given size.
     *
     * @param size the size of the input buffer.
     */
    public BytesInputStream(int size) {
        super(new byte[size]);
        m_Din = new DataInputStream(this);
    }

    /**
     * Constructs a new <tt>BytesInputStream</tt> instance,
     * that will read from the given data.
     *
     * @param data a byte array containing data to be read.
     */
    public BytesInputStream(byte[] data) {
        super(data);
        m_Din = new DataInputStream(this);
    }

    /**
     * Resets this <tt>BytesInputStream</tt> using the given
     * byte[] as new input buffer.
     *
     * @param data a byte array with data to be read.
     */
    public void reset(byte[] data) {
        pos = 0;
        mark = 0;
        buf = data;
        count = data.length;
    }

    /**
     * Resets this <tt>BytesInputStream</tt> using the given
     * byte[] as new input buffer and a given length.
     *
     * @param data   a byte array with data to be read.
     * @param length the length of the buffer to be considered.
     */
    public void reset(byte[] data, int length) {
        pos = 0;
        mark = 0;
        count = length;
        buf = data;
    }

    /**
     * Resets this <tt>BytesInputStream</tt>  assigning the input buffer
     * a new length.
     *
     * @param length the length of the buffer to be considered.
     */
    public void reset(int length) {
        pos = 0;
        count = length;
    }

    /**
     * Skips the given number of bytes or all bytes till the end
     * of the assigned input buffer length.
     *
     * @param n the number of bytes to be skipped as <tt>int</tt>.
     *
     * @return the number of bytes skipped.
     */
    public int skip(int n) {
        mark(pos);
        pos += n;
        return n;
    }

    /**
     * Returns the reference to the input buffer.
     *
     * @return the reference to the <tt>byte[]</tt> input buffer.
     */
    public synchronized byte[] getBuffer() {
        byte[] dest = new byte[buf.length];
        System.arraycopy(buf, 0, dest, 0, dest.length);
        return dest;
    }

    public int getBufferLength() {
        return buf.length;
    }

    public void readFully(byte b[]) throws IOException {
        m_Din.readFully(b);
    }

    public void readFully(byte b[], int off, int len) throws IOException {
        m_Din.readFully(b, off, len);
    }

    public int skipBytes(int n) throws IOException {
        return m_Din.skipBytes(n);
    }

    public boolean readBoolean() throws IOException {
        return m_Din.readBoolean();
    }

    public byte readByte() throws IOException {
        return m_Din.readByte();
    }

    public int readUnsignedByte() throws IOException {
        return m_Din.readUnsignedByte();
    }

    public short readShort() throws IOException {
        return m_Din.readShort();
    }

    public int readUnsignedShort() throws IOException {
        return m_Din.readUnsignedShort();
    }

    public char readChar() throws IOException {
        return m_Din.readChar();
    }

    public int readInt() throws IOException {
        return m_Din.readInt();
    }

    public long readLong() throws IOException {
        return m_Din.readLong();
    }

    public float readFloat() throws IOException {
        return m_Din.readFloat();
    }

    public double readDouble() throws IOException {
        return m_Din.readDouble();
    }

    public String readLine() throws IOException {
        throw new IOException("Not supported.");
    }

    public String readUTF() throws IOException {
        return m_Din.readUTF();
    }

}
