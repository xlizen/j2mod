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
package com.j2mod.modbus.cmd;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.ModbusException;
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.ModbusSlaveException;
import com.j2mod.modbus.io.ModbusTCPTransaction;
import com.j2mod.modbus.io.ModbusTransaction;
import com.j2mod.modbus.msg.*;
import com.j2mod.modbus.msg.ReadFileRecordRequest.RecordRequest;
import com.j2mod.modbus.msg.ReadFileRecordResponse.RecordResponse;
import com.j2mod.modbus.net.TCPMasterConnection;
import com.j2mod.modbus.util.ModbusLogger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * WriteRecordText -- Exercise the "WRITE FILE RECORD" Modbus
 * message.
 *
 * @author Julie
 * @version 0.96
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class TCPWriteRecordTest {

    private static final ModbusLogger logger = ModbusLogger.getLogger(TCPWriteRecordTest.class);

    /**
     * usage -- Print command line arguments and exit.
     */
    private static void usage() {
        logger.system("Usage: TCPWriteRecordTest address[:port[:unit]] file record registers [count]");

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
            logger.system("Invalid parameter");
            usage();
        }
        catch (UnknownHostException x) {
            logger.system("Unknown host: %s", hostName);
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

            logger.system("Connected to %s:%d", ipAddress.toString(), connection.getPort());

            for (int i = 0; i < requestCount; i++) {
				/*
				 * Setup the READ FILE RECORD request.  The record number
				 * will be incremented for each loop.
				 */
                rdRequest = new ReadFileRecordRequest();
                rdRequest.setUnitID(unit);

                RecordRequest recordRequest = new ReadFileRecordRequest.RecordRequest(file, record + i, registers);
                rdRequest.addRequest(recordRequest);

                logger.system("Request: %s", rdRequest.getHexMessage());

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
                    logger.error("Slave Exception: %s", x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusIOException x) {
                    logger.error("I/O Exception: %s", x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusException x) {
                    logger.error("Modbus Exception: %s", x.getLocalizedMessage());
                    continue;
                }

                short values[];

                wrRequest = new WriteFileRecordRequest();
                wrRequest.setUnitID(unit);

                ModbusResponse dummy = trans.getResponse();
                if (dummy == null) {
                    logger.system("No response for transaction %d", i);
                    continue;
                }
                if (dummy instanceof ExceptionResponse) {
                    ExceptionResponse exception = (ExceptionResponse)dummy;

                    logger.system(exception.toString());

                    continue;
                }
                else if (dummy instanceof ReadFileRecordResponse) {
                    rdResponse = (ReadFileRecordResponse)dummy;

                    logger.system("Response: %s", rdResponse.getHexMessage());

                    int count = rdResponse.getRecordCount();
                    for (int j = 0; j < count; j++) {
                        RecordResponse data = rdResponse.getRecord(j);
                        values = new short[data.getWordCount()];
                        for (int k = 0; k < data.getWordCount(); k++) {
                            values[k] = data.getRegister(k).toShort();
                        }

                        logger.system("read data[%d] = %s", j, Arrays.toString(values));

                        WriteFileRecordRequest.RecordRequest wrData = new WriteFileRecordRequest.RecordRequest(file, record + i, values);
                        wrRequest.addRequest(wrData);
                    }
                }
                else {
					/*
					 * Unknown message.
					 */
                    logger.system("Unknown Response: %s", dummy.getHexMessage());
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
                    logger.error("Slave Exception: %s", x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusIOException x) {
                    logger.error("I/O Exception: %s", x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusException x) {
                    logger.error("Modbus Exception: %s", x.getLocalizedMessage());
                    continue;
                }

                dummy = trans.getResponse();
                if (dummy == null) {
                    logger.system("No response for transaction %d", i);
                    continue;
                }
                if (dummy instanceof ExceptionResponse) {
                    ExceptionResponse exception = (ExceptionResponse)dummy;

                    logger.system(exception.toString());

                }
                else if (dummy instanceof WriteFileRecordResponse) {
                    wrResponse = (WriteFileRecordResponse)dummy;

                    logger.system("Response: %s", wrResponse.getHexMessage());

                    int count = wrResponse.getRequestCount();
                    for (int j = 0; j < count; j++) {
                        WriteFileRecordResponse.RecordResponse data = wrResponse.getRecord(j);
                        values = new short[data.getWordCount()];
                        for (int k = 0; k < data.getWordCount(); k++) {
                            values[k] = data.getRegister(k).toShort();
                        }

                        logger.system("write response data[%d] = ", j, Arrays.toString(values));
                    }
                }
                else {
					/*
					 * Unknown message.
					 */
                    logger.system("Unknown Response: %s", dummy.getHexMessage());
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
