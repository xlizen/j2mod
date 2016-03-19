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
import java.io.EOFException;
import java.io.IOException;

/**
 * Interface implementing a non word data handler for the read/write multiple
 * register commands.
 *
 * This interface can be used by any class which works with multiple words of
 * data for a non-standard data item. For example, message may involve data
 * items which are floating point values or string.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public interface NonWordDataHandler {

    /**
     * Returns the intermediate raw non-word data.
     *
     * <p>
     * An implementation would need to provide a means of converting between the
     * raw byte data and the registers that are present in actual messages.
     *
     * @return the raw data as <tt>byte[]</tt>.
     */
    byte[] getData();

    /**
     * Reads the non-word raw data based on an arbitrary implemented structure.
     *
     * @param in
     *            the <tt>DataInput</tt> to read from.
     * @param reference
     *            to specify the offset as <tt>int</tt>.
     * @param count
     *            to specify the amount of bytes as <tt>int</tt>.
     *
     * @throws IOException
     *             if I/O fails.
     * @throws EOFException
     *             if the stream ends before all data is read.
     */
    void readData(DataInput in, int reference, int count) throws IOException, EOFException;

    /**
     * Returns the word count of the data. Note that this should be the length
     * of the byte array divided by two.
     *
     * @return the number of words the data consists of.
     */
    int getWordCount();

    /**
     * Commits the data if it has been read into an intermediate repository.
     *
     * <p>
     * This method is called for a message (for example, a
     * <tt>WriteMultipleRegistersRequest</tt> instance) when finished with
     * reading, for creating a response.
     *
     * @return -1 if the commit was successful, a Modbus exception code valid
     *         for the read/write multiple registers commands otherwise.
     */
    int commitUpdate();

    /**
     * Prepares the raw data, putting it together from a backing data store.
     *
     * <p>
     * This method is called for a message (for example, * <tt>ReadMultipleRegistersRequest</tt>) when finished with reading, for
     * creating a response.
     *
     * @param reference
     *            to specify the offset as <tt>int</tt>.
     * @param count
     *            to specify the number of bytes as <tt>int</tt>.
     */
    void prepareData(int reference, int count);
}