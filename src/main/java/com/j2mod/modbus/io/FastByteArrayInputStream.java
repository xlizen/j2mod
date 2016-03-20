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

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is a replacement for ByteArrayInputStream that does not
 * synchronize every byte read.
 * <p/>
 *
 * @author Mark Hayes
 * @author Dieter Wimberger
 *
 * @version 1.2rc1 (09/11/2004)
 */
public class FastByteArrayInputStream extends InputStream {

    private static final Logger logger = Logger.getLogger(FastByteArrayInputStream.class);

    /**
     * Number of bytes in the input buffer.
     */
    protected int count;
    /**
     * Actual position pointer into the input buffer.
     */
    protected int pos;

    /**
     * Marked position pointer into the input buffer.
     */
    protected int mark;
    /**
     * Input buffer <tt>byte[]</tt>.
     */
    protected byte[] buf;

    /**
     * Creates an input stream.
     *
     * @param buffer
     *            the data to read.
     */
    public FastByteArrayInputStream(byte[] buffer) {
        buf = buffer;
        count = buffer.length;
        pos = 0;
        mark = 0;
    }

    /**
     * Creates an input stream.
     *
     * @param buffer the data to read.
     * @param offset the byte offset at which to begin reading.
     * @param length the number of bytes to read.
     */
    public FastByteArrayInputStream(byte[] buffer, int offset, int length) {
        buf = buffer;
        pos = offset;
        count = length;
    }

    // --- begin ByteArrayInputStream compatible methods ---

    public int read() throws IOException {
        logger.debug("read()");
        logger.debug("count=%d pos=%d", count, pos);
        return (pos < count) ? (buf[pos++] & 0xff) : (-1);
    }

    public int read(byte[] toBuf) throws IOException {
        logger.debug("read(byte[])");
        return read(toBuf, 0, toBuf.length);
    }

    public int read(byte[] toBuf, int offset, int length) throws IOException {
        logger.debug("read(byte[],int,int)");
        int avail = count - pos;
        if (avail <= 0) {
            return -1;
        }
        if (length > avail) {
            length = avail;
        }
        for (int i = 0; i < length; i++) {
            toBuf[offset++] = buf[pos++];
        }
        return length;
    }

    public long skip(long count) {
        int myCount = (int)count;
        if (myCount + pos > this.count) {
            myCount = this.count - pos;
        }
        pos += myCount;
        return myCount;
    }

    public int available() {
        return count - pos;
    }

    public void mark(int readlimit) {
        logger.debug("mark()");
        mark = pos;
        logger.debug("mark=%d pos=%d", mark, pos);
    }

    public void reset() {
        logger.debug("reset()");
        pos = mark;
        logger.debug("mark=%d pos=%d", mark, pos);
    }

    public boolean markSupported() {
        return true;
    }

    // --- end ByteArrayInputStream compatible methods ---

    public byte[] toByteArray() {
        byte[] toBuf = new byte[count];
        System.arraycopy(buf, 0, toBuf, 0, count);
        return toBuf;
    }

    /**
     * Returns the underlying data being read.
     *
     * @return the underlying data.
     */
    public synchronized byte[] getBufferBytes() {
        byte[] dest = new byte[count];
        System.arraycopy(buf, 0, dest, 0, dest.length);
        return dest;
    }

    /**
     * Returns the offset at which data is being read from the buffer.
     *
     * @return the offset at which data is being read.
     */
    public int getBufferOffset() {
        return pos;
    }

    /**
     * Returns the end of the buffer being read.
     *
     * @return the end of the buffer.
     */
    public int getBufferLength() {
        return count;
    }

}// class FastByteArrayInputStream
