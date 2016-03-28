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
import com.ghgande.j2mod.modbus.util.ModbusLogger;
import com.ghgande.j2mod.modbus.util.ThreadPool;

import java.io.IOException;
import java.net.*;

/**
 * Class that implements a ModbusTCPListener.
 *
 * <p>
 * If listening, it accepts incoming requests passing them on to be handled.
 * If not listening, silently drops the requests.
 *
 * @author Dieter Wimberger
 * @author Julie Haugh
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 */
public class ModbusTCPListener implements ModbusListener {

    private static final ModbusLogger logger = ModbusLogger.getLogger(ModbusTCPListener.class);

    private ServerSocket serverSocket = null;
    private ThreadPool threadPool;
    private Thread listener;
    private int port = Modbus.DEFAULT_PORT;
    private boolean listening;
    private InetAddress address;

    /**
     * Constructs a ModbusTCPListener instance.<br>
     *
     * @param poolsize the size of the <tt>ThreadPool</tt> used to handle incoming
     *                 requests.
     * @param addr     the interface to use for listening.
     */
    public ModbusTCPListener(int poolsize, InetAddress addr) {
        threadPool = new ThreadPool(poolsize);
        address = addr;
    }

    /**
     * /**
     * Constructs a ModbusTCPListener instance.  This interface is created
     * to listen on the wildcard address (0.0.0.0), which will accept TCP packets
     * on all available adapters/interfaces
     *
     * @param poolsize the size of the <tt>ThreadPool</tt> used to handle incoming
     *                 requests.
     */
    public ModbusTCPListener(int poolsize) {
        threadPool = new ThreadPool(poolsize);
        try {
            address = InetAddress.getByAddress(new byte[]{0, 0, 0, 0});
        }
        catch (UnknownHostException ex) {
            // Can't happen -- size is fixed.
        }
    }

    /**
     * Sets the port to be listened to.
     *
     * @param port the number of the IP port as <tt>int</tt>.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the address of the interface to be listened to.
     *
     * @param addr an <tt>InetAddress</tt> instance.
     */
    public void setAddress(InetAddress addr) {
        address = addr;
    }

    /**
     * Starts this <tt>ModbusTCPListener</tt>.
     *
     * @deprecated
     */
    public void start() {
        listening = true;
        listener = new Thread(this);
        listener.start();
    }

    /**
     * Accepts incoming connections and handles then with
     * <tt>TCPConnectionHandler</tt> instances.
     */
    @Override
    public void run() {
        try {
            /*
             * A server socket is opened with a connectivity queue of a size
			 * specified in int floodProtection. Concurrent login handling under
			 * normal circumstances should be alright, denial of service
			 * attacks via massive parallel program logins can probably be
			 * prevented.
			 */
            int floodProtection = 5;
            serverSocket = new ServerSocket(port, floodProtection, address);
            logger.debug("Listening to %s (Port %d)", serverSocket.toString(), port);

			/*
             * Infinite loop, taking care of resources in case of a lot of
			 * parallel logins
			 */
            listening = true;
            while (listening) {
                Socket incoming = serverSocket.accept();
                logger.debug("Making new connection %s", incoming.toString());
                if (listening) {
                    threadPool.execute(new TCPConnectionHandler(new TCPSlaveConnection(incoming)));
                }
                else {
                    incoming.close();
                }
            }
        }
        catch (SocketException iex) {
            if (listening) {
                logger.debug(iex);
            }
        }
        catch (IOException e) {
            // FIXME: this is a major failure, how do we handle this
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
     * Set the listening state of this <tt>ModbusTCPListener</tt> object.
     * A <tt>ModbusTCPListener</tt> will silently drop any requests if the
     * listening state is set to <tt>false</tt>.
     *
     * @param b
     */
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
     * Stops this <tt>ModbusTCPListener</tt>.
     */
    @Override
    public void stop() {
        listening = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (listener != null) {
                listener.join();
            }
            if (threadPool != null) {
                threadPool.close();
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

}
