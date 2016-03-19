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
 */
public class ReadFIFOTest {

    private static final Logger logger = Logger.getLogger(ReadFIFOTest.class);

    /**
     * usage -- Print command line arguments and exit.
     */
    private static void usage() {
        logger.debug("Usage: ReadFIFOTest connection unit fifo [repeat]");

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
                if (System.getProperty("com.ghgande.j2mod.modbus.baud") != null) {
                    ((ModbusSerialTransport)transport).setBaudRate(Integer.parseInt(System.getProperty("com.ghgande.j2mod.modbus.baud")));
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
            logger.debug("Invalid parameter");
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

                logger.debug("Request: " + request.getHexMessage());

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
                    logger.debug("Slave Exception: " + x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusIOException x) {
                    logger.debug("I/O Exception: " + x.getLocalizedMessage());
                    continue;
                }
                catch (ModbusException x) {
                    logger.debug("Modbus Exception: " + x.getLocalizedMessage());
                    continue;
                }

                ModbusResponse dummy = trans.getResponse();
                if (dummy == null) {
                    logger.debug("No response for transaction " + i);
                    continue;
                }
                if (dummy instanceof ExceptionResponse) {
                    ExceptionResponse exception = (ExceptionResponse)dummy;

                    logger.debug(exception);

                    continue;
                }
                else if (dummy instanceof ReadFIFOQueueResponse) {
                    response = (ReadFIFOQueueResponse)dummy;

                    logger.debug("Response: " + response.getHexMessage());

                    int count = response.getWordCount();
                    logger.debug(count + " values");

                    for (int j = 0; j < count; j++) {
                        short value = (short)response.getRegister(j);

                        logger.debug("data[" + j + "] = " + value);
                    }
                    continue;
                }

				/*
				 * Unknown message.
				 */
                logger.debug("Unknown Response: " + dummy.getHexMessage());
            }
			
			/*
			 * Teardown the connection.
			 */
            transport.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
}
