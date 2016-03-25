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
package com.j2mod.modbus.net;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.ModbusCoupler;
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.io.ModbusTransport;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.msg.ModbusResponse;
import com.j2mod.modbus.util.ModbusLogger;
import com.j2mod.modbus.util.SerialParameters;

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

    private boolean m_Listening;
    private boolean m_Running = true;
    private SerialConnection m_SerialCon;
    private int m_Unit = 0;

    /**
     * Constructs a new <tt>ModbusSerialListener</tt> instance.
     *
     * @param params a <tt>SerialParameters</tt> instance.
     */
    public ModbusSerialListener(SerialParameters params) {
        m_SerialCon = new SerialConnection(params);
    }

    /**
     * run
     *
     * Listen for incoming messages and process.
     */
    public void run() {
        try {
            m_Listening = true;
            m_SerialCon.open();

            ModbusTransport transport = m_SerialCon.getModbusTransport();

            while (m_Running) {
                if (m_Listening) {
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

                        if (m_Unit != 0 && m_Unit != request.getUnitID()) {
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
            m_Listening = false;

            if (m_SerialCon != null) {
                m_SerialCon.close();
            }
        }
    }

    /**
     * Gets the Modbus unit number for this <tt>ModbusSerialListener</tt>
     *
     * @return Modbus unit number
     */
    public int getUnit() {
        return m_Unit;
    }

    /**
     * Sets the Modbus unit number for this <tt>ModbusSerialListener</tt>
     *
     * @param unit Modbus unit number
     */
    public void setUnit(int unit) {
        m_Unit = unit;
    }

    /**
     * Tests if this <tt>ModbusTCPListener</tt> is listening and accepting
     * incoming connections.
     *
     * @return true if listening (and accepting incoming connections), false
     * otherwise.
     */
    public boolean isListening() {
        return m_Listening;
    }

    /**
     * Sets the listening flag of this <tt>ModbusTCPListener</tt>.
     *
     * @param b true if listening (and accepting incoming connections), false
     *          otherwise.
     */
    public void setListening(boolean b) {
        m_Listening = b;
    }

    /**
     * Start the listener thread for this serial interface.
     */
    public Thread listen() {
        m_Listening = true;
        Thread result = new Thread(this);
        result.start();

        return result;
    }

    /**
     * Stops this interface.
     */
    public void stop() {
        m_Listening = false;
        m_Running = false;
    }

}
