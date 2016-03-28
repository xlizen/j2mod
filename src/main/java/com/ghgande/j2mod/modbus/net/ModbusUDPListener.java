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
import com.ghgande.j2mod.modbus.io.ModbusUDPTransport;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.util.ModbusLogger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class that implements a ModbusUDPListener.<br>
 *
 * @author Dieter Wimberger
 * @author Julie Haugh
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class ModbusUDPListener implements ModbusListener {

    private static final ModbusLogger logger = ModbusLogger.getLogger(ModbusUDPListener.class);

    private int port = Modbus.DEFAULT_PORT;
    private boolean listening = false;
    private boolean running = false;
    private InetAddress address;
    private UDPSlaveTerminal terminal;
    private String error;

    /**
     * Create a new <tt>ModbusUDPListener</tt> instance listening to the given
     * interface address.
     *
     * @param ifc an <tt>InetAddress</tt> instance.
     */
    public ModbusUDPListener(InetAddress ifc) {
        address = ifc;
        listening = true;
    }

    /**
     * Constructs a new ModbusUDPListener instance. The address will be set to a
     * default value of the wildcard local address and the default Modbus port.
     */
    public ModbusUDPListener() {
        try {
            address = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
        }
        catch (UnknownHostException e) {
            // Can't happen -- length is fixed by code.
        }
    }

    /**
     * Returns the number of the port this <tt>ModbusUDPListener</tt> is
     * listening to.
     *
     * @return the number of the IP port as <tt>int</tt>.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the number of the port this <tt>ModbusUDPListener</tt> is listening
     * to.
     *
     * @param port the number of the IP port as <tt>int</tt>.
     */
    public void setPort(int port) {
        this.port = ((port > 0) ? port : Modbus.DEFAULT_PORT);
    }

    /**
     * Starts this <tt>ModbusUDPListener</tt>.
     */
    @Override
    public void run() {
        ModbusUDPTransport transport;
        try {
            if (address == null) {
                terminal = new UDPSlaveTerminal(InetAddress.getByAddress(new byte[]{0, 0, 0, 0}));
            }
            else {
                terminal = new UDPSlaveTerminal(address);
            }
            terminal.setLocalPort(port);
            terminal.activate();
            transport = new ModbusUDPTransport(terminal);
        }

        // Catch any fatal errors and set the listening flag to false to indicate an error
        catch (Exception e) {
            error = String.format("Cannot start UDP listener - %s", e.getMessage());
            listening = false;
            return;
        }

        listening = true;
        running = true;
        try {
            while (running) {
                // Get the request from the transport. It will be processed
                // using an associated process image.
                ModbusRequest request = transport.readRequest();
                ModbusResponse response;

                // Test if Process image exists and has a correct unit ID
                ProcessImage spi = ModbusCoupler.getReference().getProcessImage();
                if (spi == null ||
                        (spi.getUnitID() != 0 && request.getUnitID() != spi.getUnitID())) {
                    response = request.createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
                }
                else {
                    response = request.createResponse();
                }
                logger.debug("Request:%s", request.getHexMessage());
                logger.debug("Response:%s", response.getHexMessage());

                // Write the response
                transport.writeMessage(response);
            }
        }
        catch (ModbusIOException ex) {
            if (!ex.isEOF()) {
                logger.error(ex);
            }
        }
        finally {
            try {
                terminal.deactivate();
                transport.close();
            }
            catch (Exception ex) {
                // ignore
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
     * Sets if this <tt>ModbusUDPListener</tt> is listening and accepting
     * incoming connections.
     *
     * @param listen true if the <tt>ModbusUDPListener</tt> should listen, false
     *               otherwise.
     */
    @Override
    public void setListening(boolean listen) {
        listening = listen;
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
     * Stops this <tt>ModbusUDPListener</tt>.
     */
    @Override
    public void stop() {
        terminal.deactivate();
        listening = false;
        running = false;
    }

    /**
     * Returns any startup errors that may have aoccurred
     *
     * @return Error string
     */
    public String getError() {
        return error;
    }
}
