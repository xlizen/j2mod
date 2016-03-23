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

import com.j2mod.modbus.ModbusException;
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.ModbusSlaveException;
import com.j2mod.modbus.io.ModbusSerialTransport;
import com.j2mod.modbus.io.ModbusTransaction;
import com.j2mod.modbus.io.ModbusTransport;
import com.j2mod.modbus.msg.ExceptionResponse;
import com.j2mod.modbus.msg.ModbusResponse;
import com.j2mod.modbus.msg.ReadFIFOQueueRequest;
import com.j2mod.modbus.msg.ReadFIFOQueueResponse;
import com.j2mod.modbus.net.ModbusMasterFactory;
import com.j2mod.modbus.util.Logger;

/**
 * ReadFIFOTest -- Exercise the "READ FIFO" Modbus
 * message.
 *
 * @author Julie
 * @version 1.03
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public class ReadFIFOTest {

    private static final Logger logger = Logger.getLogger(ReadFIFOTest.class);

    /**
     * usage -- Print command line arguments and exit.
     */
    private static void usage() {
        logger.system("Usage: ReadFIFOTest connection unit fifo [repeat]");

        System.exit(1);
    }

    public static void main(String[] args) {
        ModbusTransport transport = null;
        ReadFIFOQueueRequest request;
        ReadFIFOQueueResponse response;
        ModbusTransaction trans;
        int unit = 0;
        int fifo = 0;
        int requestCount = 1;

		/*
         * Get the command line parameters.
		 */
        if (args.length < 3 || args.length > 4) {
            usage();
        }

        try {
            transport = ModbusMasterFactory.createModbusMaster(args[0]);
            if (transport instanceof ModbusSerialTransport) {
                ((ModbusSerialTransport)transport).setReceiveTimeout(500);
                if (System.getProperty("com.j2mod.modbus.baud") != null) {
                    ((ModbusSerialTransport)transport).setBaudRate(Integer.parseInt(System.getProperty("com.j2mod.modbus.baud")));
                }
                else {
                    ((ModbusSerialTransport)transport).setBaudRate(19200);
                }

                Thread.sleep(2000);
            }
            unit = Integer.parseInt(args[1]);
            fifo = Integer.parseInt(args[2]);

            if (args.length > 3) {
                requestCount = Integer.parseInt(args[3]);
            }
        }
        catch (NumberFormatException x) {
            logger.system("Invalid parameter");
            usage();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            usage();
            System.exit(1);
        }

        try {
            for (int i = 0; i < requestCount; i++) {
                /*
                 * Setup the READ FILE RECORD request.  The record number
				 * will be incremented for each loop.
				 */
                request = new ReadFIFOQueueRequest();
                request.setUnitID(unit);
                request.setReference(fifo);

                logger.system("Request: %s", request.getHexMessage());

				/*
				 * Setup the transaction.
				 */
                trans = transport.createTransaction();
                trans.setRequest(request);

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
                else if (dummy instanceof ReadFIFOQueueResponse) {
                    response = (ReadFIFOQueueResponse)dummy;

                    logger.system("Response: %s", response.getHexMessage());

                    int count = response.getWordCount();
                    logger.system("%d values", count);

                    for (int j = 0; j < count; j++) {
                        short value = (short)response.getRegister(j);
                        logger.system("data[%d] = %f", j, value);
                    }
                    continue;
                }

				/*
				 * Unknown message.
				 */
                logger.system("Unknown Response: %s", dummy.getHexMessage());
            }
			
			/*
			 * Teardown the connection.
			 */
            if (transport != null) {
                transport.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
}
