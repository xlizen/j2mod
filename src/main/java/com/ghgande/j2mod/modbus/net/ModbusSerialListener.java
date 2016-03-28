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
package com.ghgande.j2mod.modbus.net;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.util.ModbusLogger;
import com.ghgande.j2mod.modbus.util.SerialParameters;

/**
 * Class that implements a ModbusSerialListener.<br>
 * If listening, it accepts incoming requests passing them on to be handled.
 *
 * @author Dieter Wimberger
 * @author Julie Haugh Code cleanup in prep to refactor with ModbusListener
 *         interface
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class ModbusSerialListener implements ModbusListener {

    private static final ModbusLogger logger = ModbusLogger.getLogger(ModbusSerialListener.class);

    private boolean listening;
    private boolean running = true;
    private SerialConnection serialCon;
    private int unitID = 0;

    /**
     * Constructs a new <tt>ModbusSerialListener</tt> instance.
     *
     * @param params a <tt>SerialParameters</tt> instance.
     */
    public ModbusSerialListener(SerialParameters params) {
        serialCon = new SerialConnection(params);
    }

    /**
     * run
     *
     * Listen for incoming messages and process.
     */
    @Override
    public void run() {
        try {
            listening = true;
            serialCon.open();

            ModbusTransport transport = serialCon.getModbusTransport();

            while (running) {
                if (listening) {
                    try {

						/*
                         * Read the request from the serial interface. If this
						 * instance has been assigned a unit number, it must be
						 * enforced.
						 */
                        ModbusRequest request = transport.readRequest();
                        if (request == null) {
                            continue;
                        }

                        if (unitID != 0 && unitID != request.getUnitID()) {
                            continue;
                        }

						/*
                         * Create the response using a ProcessImage. A Modbus
						 * ILLEGAL FUNCTION exception will be thrown if there is
						 * no ProcessImage.
						 */
                        ModbusResponse response;
                        if (ModbusCoupler.getReference().getProcessImage() == null) {
                            response = request.createExceptionResponse(Modbus.ILLEGAL_FUNCTION_EXCEPTION);
                        }
                        else {
                            response = request.createResponse();
                        }

						/*
                         * Log the Request and Response messages.
						 */
                        try {
                            logger.debug("Request (%s): %s", request.getClass().getName(), request.getHexMessage());
                            logger.debug("Response (%s): %s", response.getClass().getName(), response.getHexMessage());
                        }
                        catch (RuntimeException x) {
                            // Ignore.
                        }

						/*
                         * Write the response.
						 */
                        transport.writeMessage(response);
                    }
                    catch (ModbusIOException ex) {
                        logger.debug(ex);
                    }
                }
                else {
					/*
					 * Not listening -- read and discard the request so the
					 * input doesn't get clogged up.
					 */
                    transport.readRequest();
                }
            }
        }
        catch (Exception e) {
			/*
			 * TODO -- Make sure methods are throwing reasonable exceptions, and
			 * not just throwing "Exception".
			 */
            e.printStackTrace();
        }
        finally {
            listening = false;

            if (serialCon != null) {
                serialCon.close();
            }
        }
    }

    /**
     * Tests if this <tt>ModbusTCPListener</tt> is listening and accepting
     * incoming connections.
     *
     * @return true if listening (and accepting incoming connections), false
     * otherwise.
     */
    @Override
    public boolean isListening() {
        return listening;
    }

    /**
     * Sets the listening flag of this <tt>ModbusTCPListener</tt>.
     *
     * @param b true if listening (and accepting incoming connections), false
     *          otherwise.
     */
    @Override
    public void setListening(boolean b) {
        listening = b;
    }

    /**
     * Start the listener thread for this serial interface.
     */
    @Override
    public Thread listen() {
        listening = true;
        Thread result = new Thread(this);
        result.start();

        return result;
    }

    /**
     * Stops this interface.
     */
    @Override
    public void stop() {
        listening = false;
        running = false;
    }

}
