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
package com.j2mod.modbus.utils;

import com.fazecast.jSerialComm.SerialPort;
import com.j2mod.modbus.ModbusCoupler;
import com.j2mod.modbus.net.ModbusTCPListener;
import com.j2mod.modbus.procimg.*;
import com.j2mod.modbus.util.Logger;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.*;
import java.io.File;

/**
 * This class is a collection of utility methods used by all test classes
 */
public class TestUtils {

    private static final Logger logger = Logger.getLogger(TestUtils.class);
    private static final int DEFAULT_BUFFER_SIZE = 32 * 1024;
    public static final int UNIT_ID = 15;
    public static final String LOCALHOST = "localhost";

    /**
     * This method will extract the appropriate Modbus master tool into the
     * temp folder so that it can be used later
     *
     * @throws Exception
     */
    public static void loadModPollTool() throws Exception {

        // Load the resource from the library

        String osName = System.getProperty("os.name");

        // Work out the correct name

        String exeName;
        if (osName.matches("(?is)windows.*")) {
            osName = "win32";
            exeName = "modpoll.exe";
        }
        else if (osName.matches("(?is)mac.*")) {
            osName = "macosx";
            exeName = "modpoll";
        }
        else {
            osName = "linux";
            exeName = "modpoll";
        }

        // Check to see if we already have the library available

        File nativeFile = new File(getTemporaryDirectory(), exeName);
        if (!nativeFile.exists()) {

            // Copy the library to the temporary folder

            InputStream in = null;
            String resourceName = String.format("/com/j2mod/modbus/native/%s/%s", osName, exeName);

            try {
                in = SerialPort.class.getResourceAsStream(resourceName);
                if (in == null) {
                    throw new Exception(String.format("Cannot find resource [%s]", resourceName));
                }
                pipeInputToOutputStream(in, nativeFile, false);

                // Set the correct privileges

                if (!nativeFile.setWritable(true, true)) {
                    logger.warn("Cannot set RxTx library to be writable");
                }
                if (!nativeFile.setReadable(true, false)) {
                    logger.warn("Cannot set RxTx library to be readable");
                }
            }
            catch (Exception e) {
                throw new Exception(String.format("Cannot locate native library [%s] - %s", exeName, e.getMessage()));
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    }
                    catch (IOException e) {
                        logger.error("Cannot close stream - %s", e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Returns a full path name of a suitable temporary filename
     *
     * @return String
     */
    public static String getTemporaryDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Returns a full path name of a suitable temporary filename
     *
     * @return File
     */
    public static File getTemporaryFile() {
        return new File(getTemporaryFilename(null));
    }

    /**
     * Returns a full path name of a suitable temporary filename using the
     * extension provided.  If sExtension is null then .tmp is used
     *
     * @param extension Extension to give the file
     *
     * @return File
     */
    public static File getTemporaryFile(String extension) {
        return new File(getTemporaryFilename(extension));
    }

    /**
     * Returns a full path name of a suitable temporary filename
     *
     * @return String
     */
    public static String getTemporaryFilename() {
        return getTemporaryFilename(null);
    }

    /**
     * Returns a full path name of a suitable temporary filename using the
     * extension provided.  If sExtension is null then .tmp is used
     *
     * @param extension Extension to give the file
     *
     * @return String
     */
    public static String getTemporaryFilename(String extension) {
        return getTemporaryDirectory() + File.separator + getTemporaryFilenameOnly(extension);
    }

    /**
     * Returns a temporary filename only using the extension if provided. if the provided value is null then .tmp is used
     *
     * @param extension Extension to give the file
     *
     * @return a {@link java.lang.String} object
     */
    public static String getTemporaryFilenameOnly(String extension) {
        String returnValue;
        if (extension != null) {
            returnValue = getTemporaryName() + '.' + extension.trim();
        }
        else {
            returnValue = getTemporaryName() + ".tmp";
        }

        return returnValue;
    }

    /**
     * Returns a temporary name which should be unique to this thread
     *
     * @return a {@link java.lang.String} object
     */
    public static String getTemporaryName() {
        return "j2mode-" + Thread.currentThread().getId() + '-' + System.nanoTime();
    }

    /**
     * Convenient way of sending data from an input stream to an output file
     * in the most efficient way possible
     *
     * @param in           Input stream to read from
     * @param fileOut      Output file to write to
     * @param ignoreErrors True if this method must not throw any socket errors
     *
     * @throws IOException if an error occurs
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void pipeInputToOutputStream(InputStream in, File fileOut, boolean ignoreErrors) throws IOException {
        if (fileOut == null) {
            logger.error("The output filename doesn't exist or is invalid");
            if (!ignoreErrors) {
                throw new IOException("The output filename doesn't exist or is invalid");
            }
        }
        else {

            // Create the parentage for the folders if they don't exist

            File parent = fileOut.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            OutputStream fileStream = null;
            try {
                fileStream = new FileOutputStream(fileOut);
                pipeInputToOutputStream(in, fileStream, true, ignoreErrors);
            }
            catch (IOException e) {
                if (fileStream != null) {
                    try {
                        fileStream.close();
                    }
                    catch (IOException ex) {
                        logger.error("Cannot close stream - %s", ex.getMessage());
                    }
                }
                if (!ignoreErrors) {
                    throw e;
                }
            }
        }
    }

    /**
     * Convenient way of sending data from an input stream to an output stream
     * in the most efficient way possible
     * If the bCloseOutput flag is false, then the output stream remains open
     * so that further writes can be made to the stream
     *
     * @param in           Input stream to read from
     * @param out          Output stream to write to
     * @param closeOutput  True if the output stream should be closed on exit
     * @param ignoreErrors True if this method must not throw any socket errors
     *
     * @throws IOException if an error occurs
     */
    public static void pipeInputToOutputStream(InputStream in, OutputStream out, boolean closeOutput, boolean ignoreErrors) throws IOException {

        OutputStream bufferedOut = out;
        InputStream bufferedIn = in;

        if (in != null && out != null) {
            try {
                // Buffer the streams if they aren't already

                if (!bufferedOut.getClass().equals(BufferedOutputStream.class)) {
                    bufferedOut = new BufferedOutputStream(bufferedOut, DEFAULT_BUFFER_SIZE);
                }
                if (!bufferedIn.getClass().equals(BufferedInputStream.class)) {
                    bufferedIn = new BufferedInputStream(bufferedIn, DEFAULT_BUFFER_SIZE);
                }

                // Push the data

                int iTmp;
                while ((iTmp = bufferedIn.read()) != -1) {
                    bufferedOut.write((byte)iTmp);
                }
                bufferedOut.flush();
                out.flush();
            }
            catch (IOException e) {
                if (!ignoreErrors && !(e instanceof java.net.SocketException)) {
                    logger.error(e.getMessage());
                    throw e;
                }
                else {
                    logger.debug(e.getMessage());
                }
            }
            finally {
                bufferedIn.close();
                if (closeOutput) {
                    bufferedOut.close();
                }
            }
        }
    }

    /**
     * Creates a Slave to use for testing
     *
     * @return Listener of the slave
     *
     * @throws IOException
     */
    public static ModbusTCPListener createTCPSlave() throws Exception {
        ModbusTCPListener listener = null;
        SimpleProcessImage spi;
        try {
            // Create a Slave that we can use to exercise each and every register type

            spi = new SimpleProcessImage();
            spi.addDigitalOut(new SimpleDigitalOut(true));
            spi.addDigitalOut(new SimpleDigitalOut(false));

            spi.addDigitalIn(new SimpleDigitalIn(false));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(false));
            spi.addDigitalIn(new SimpleDigitalIn(true));

            // allow checking LSB/MSB order
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(true));

            spi.addFile(new com.j2mod.modbus.procimg.File(0, 10)
                    .setRecord(0, new Record(0, 10))
                    .setRecord(1, new Record(1, 10))
                    .setRecord(2, new Record(2, 10))
                    .setRecord(3, new Record(3, 10))
                    .setRecord(4, new Record(4, 10))
                    .setRecord(5, new Record(5, 10))
                    .setRecord(6, new Record(6, 10))
                    .setRecord(7, new Record(7, 10))
                    .setRecord(8, new Record(8, 10))
                    .setRecord(9, new Record(9, 10)));

            spi.addFile(new com.j2mod.modbus.procimg.File(1, 20)
                    .setRecord(0, new Record(0, 10))
                    .setRecord(1, new Record(1, 20))
                    .setRecord(2, new Record(2, 20))
                    .setRecord(3, new Record(3, 20))
                    .setRecord(4, new Record(4, 20))
                    .setRecord(5, new Record(5, 20))
                    .setRecord(6, new Record(6, 20))
                    .setRecord(7, new Record(7, 20))
                    .setRecord(8, new Record(8, 20))
                    .setRecord(9, new Record(9, 20))
                    .setRecord(10, new Record(10, 10))
                    .setRecord(11, new Record(11, 20))
                    .setRecord(12, new Record(12, 20))
                    .setRecord(13, new Record(13, 20))
                    .setRecord(14, new Record(14, 20))
                    .setRecord(15, new Record(15, 20))
                    .setRecord(16, new Record(16, 20))
                    .setRecord(17, new Record(17, 20))
                    .setRecord(18, new Record(18, 20))
                    .setRecord(19, new Record(19, 20)));

            spi.addRegister(new SimpleRegister(251));
            spi.addRegister(new SimpleRegister(1111));
            spi.addRegister(new SimpleRegister(2222));
            spi.addRegister(new SimpleRegister(3333));
            spi.addRegister(new SimpleRegister(4444));
            spi.addInputRegister(new SimpleInputRegister(45));
            spi.addInputRegister(new SimpleInputRegister(9999));
            spi.addInputRegister(new SimpleInputRegister(8888));
            spi.addInputRegister(new SimpleInputRegister(7777));
            spi.addInputRegister(new SimpleInputRegister(6666));

            // 2. create the coupler holding the image
            ModbusCoupler.getReference().setProcessImage(spi);
            ModbusCoupler.getReference().setMaster(false);
            ModbusCoupler.getReference().setUnitID(UNIT_ID);

            // 3. create a TCP listener with 5 threads in pool, default address
            listener = new ModbusTCPListener(5);
            listener.setListening(true);
            Thread result = new Thread(listener);
            result.start();
        }
        catch (Exception x) {
            if (listener != null) {
                listener.stop();
            }
            throw new Exception(x.getMessage());
        }
        return listener;
    }

    /**
     * Runs a command line task and returns the screen output or throws and
     * error if something bad happened
     *
     * @param command Command to run
     *
     * @return Screen output
     *
     * @throws Exception
     */
    public static String execToString(String command) throws Exception {

        // Prepare the command line

        CommandLine commandline = CommandLine.parse(command);

        // Prepare the output stream

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);

        // Prepare the executor

        DefaultExecutor exec = new DefaultExecutor();
        exec.setExitValues(null);
        exec.setStreamHandler(streamHandler);
        exec.setWatchdog(new ExecuteWatchdog(5000));

        // Execute the command
        try {
            exec.execute(commandline);
            return (outputStream.toString());
        }
        catch (Exception e) {
            throw new Exception(String.format("%s - %s", outputStream.toString(), e.getMessage()));
        }
    }

}
