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
package com.ghgande.j2mod.modbus.cmd;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.ModbusSlaveException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.msg.ReadFileRecordRequest.RecordRequest;
import com.ghgande.j2mod.modbus.msg.ReadFileRecordResponse.RecordResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * WriteRecordText -- Exercise the "WRITE FILE RECORD" Modbus
 * message.
 *
 * @author Julie
 * @version 0.96
 */
public class TCPWriteRecordTest {

    /**
     * usage -- Print command line arguments and exit.
     */
    private static void usage() {
        System.out.println("Usage: TCPWriteRecordTest address[:port[:unit]] file record registers [count]");

        System.exit(1);
    }

    public static void main(String[] args) {
        InetAddress ipAddress = null;
        int port = Modbus.DEFAULT_PORT;
        int unit = 0;
        TCPMasterConnection connection;
        ReadFileRecordRequest rdRequest;
        ReadFileRecordResponse rdResponse;
        WriteFileRecordRequest wrRequest;
        WriteFileRecordResponse wrResponse;
        ModbusTransaction trans;
        int file = 0;
        int record = 0;
        int registers = 0;
        int requestCount = 1;

		/*
         * Get the command line parameters.
		 */
        if (args.length < 4 || args.length > 5) {
            usage();
        }

        String serverAddress = args[0];
        String parts[] = serverAddress.split(" *: *");
        String hostName = parts[0];

        try {
            /*
             * Address is of the form
			 * 
			 * hostName:port:unitNumber
			 * 
			 * where
			 * 
			 * hostName -- Standard text host name
			 * port		-- Modbus port, 502 is the default
			 * unit		-- Modbus unit number, 0 is the default
			 */
            if (parts.length > 1) {
                port = Integer.parseInt(parts[1]);

                if (parts.length > 2) {
                    unit = Integer.parseInt(parts[2]);
                }
            }
            ipAddress = InetAddress.getByName(hostName);

            file = Integer.parseInt(args[1]);
            record = Integer.parseInt(args[2]);
            registers = Integer.parseInt(args[3]);

            if (args.length > 4) {
                requestCount = Integer.parseInt(args[4]);
            }
        }
        catch (NumberFormatException x) {
            System.err.println("Invalid parameter");
            usage();
        }
        catch (UnknownHostException x) {
            System.err.println("Unknown host: " + hostName);
            System.exit(1);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            usage();
            System.exit(1);
        }

        try {
			
			/*
			 * Setup the TCP connection to the Modbus/TCP Master
			 */
            connection = new TCPMasterConnection(ipAddress);
            connection.setPort(port);
            connection.connect();
            connection.setTimeout(500);

            if (Modbus.debug) {
                System.out.println("Connected to " + ipAddress.toString() + ":" + connection.getPort());
            }

            for (int i = 0; i < requestCount; i++) {
				/*
				 * Setup the READ FILE RECORD request.  The record number
				 * will be incremented for each loop.
				 */
                rdRequest = new ReadFileRecordRequest();
                rdRequest.setUnitID(unit);

                RecordRequest recordRequest = new ReadFileRecordRequest.RecordRequest(file, record + i, registers);
                rdRequest.addRequest(recordRequest);

                if (Modbus.debug) {
                    System.out.println("Request: " + rdRequest.getHexMessage());
                }

				/*
				 * Setup the transaction.
				 */
                trans = new ModbusTCPTransaction(connection);
                trans.setRequest(rdRequest);

				/*
				 * Execute the transaction.
				 */
                try {
                    trans.execute();
                }
                catch (ModbusSlaveException x) {
                    System.err.println("Slave Exception: " + x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusIOException x) {
                    System.err.println("I/O Exception: " + x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusException x) {
                    System.err.println("Modbus Exception: " + x.getLocalizedMessage());
                    continue;
                }

                short values[];

                wrRequest = new WriteFileRecordRequest();
                wrRequest.setUnitID(unit);

                ModbusResponse dummy = trans.getResponse();
                if (dummy == null) {
                    System.err.println("No response for transaction " + i);
                    continue;
                }
                if (dummy instanceof ExceptionResponse) {
                    ExceptionResponse exception = (ExceptionResponse)dummy;

                    System.err.println(exception);

                    continue;
                }
                else if (dummy instanceof ReadFileRecordResponse) {
                    rdResponse = (ReadFileRecordResponse)dummy;

                    if (Modbus.debug) {
                        System.out.println("Response: " + rdResponse.getHexMessage());
                    }

                    int count = rdResponse.getRecordCount();
                    for (int j = 0; j < count; j++) {
                        RecordResponse data = rdResponse.getRecord(j);
                        values = new short[data.getWordCount()];
                        for (int k = 0; k < data.getWordCount(); k++) {
                            values[k] = data.getRegister(k).toShort();
                        }

                        System.out.println("read data[" + j + "] = " + Arrays.toString(values));

                        WriteFileRecordRequest.RecordRequest wrData = new WriteFileRecordRequest.RecordRequest(file, record + i, values);
                        wrRequest.addRequest(wrData);
                    }
                }
                else {
					/*
					 * Unknown message.
					 */
                    System.out.println("Unknown Response: " + dummy.getHexMessage());
                }
				
				/*
				 * Setup the transaction.
				 */
                trans = new ModbusTCPTransaction(connection);
                trans.setRequest(wrRequest);

				/*
				 * Execute the transaction.
				 */
                try {
                    trans.execute();
                }
                catch (ModbusSlaveException x) {
                    System.err.println("Slave Exception: " + x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusIOException x) {
                    System.err.println("I/O Exception: " + x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusException x) {
                    System.err.println("Modbus Exception: " + x.getLocalizedMessage());
                    continue;
                }

                dummy = trans.getResponse();
                if (dummy == null) {
                    System.err.println("No response for transaction " + i);
                    continue;
                }
                if (dummy instanceof ExceptionResponse) {
                    ExceptionResponse exception = (ExceptionResponse)dummy;

                    System.err.println(exception);

                }
                else if (dummy instanceof WriteFileRecordResponse) {
                    wrResponse = (WriteFileRecordResponse)dummy;

                    if (Modbus.debug) {
                        System.out.println("Response: " + wrResponse.getHexMessage());
                    }

                    int count = wrResponse.getRequestCount();
                    for (int j = 0; j < count; j++) {
                        WriteFileRecordResponse.RecordResponse data = wrResponse.getRecord(j);
                        values = new short[data.getWordCount()];
                        for (int k = 0; k < data.getWordCount(); k++) {
                            values[k] = data.getRegister(k).toShort();
                        }

                        System.out.println("write response data[" + j + "] = " + Arrays.toString(values));
                    }
                }
                else {
					/*
					 * Unknown message.
					 */
                    System.out.println("Unknown Response: " + dummy.getHexMessage());
                }
            }
			
			/*
			 * Teardown the connection.
			 */
            connection.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
