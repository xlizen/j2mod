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
package com.j2mod.modbus.io;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.ModbusException;
import com.j2mod.modbus.ModbusIOException;
import com.j2mod.modbus.ModbusSlaveException;
import com.j2mod.modbus.msg.ExceptionResponse;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.msg.ModbusResponse;
import com.j2mod.modbus.net.SerialConnection;
import com.j2mod.modbus.util.Logger;

/**
 * Class implementing the <tt>ModbusTransaction</tt>
 * interface.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ModbusSerialTransaction implements ModbusTransaction {

    private static final Logger logger = Logger.getLogger(ModbusSerialTransaction.class);

    //class attributes
    private static int c_TransactionID = Modbus.DEFAULT_TRANSACTION_ID;

    //instance attributes and associations
    private ModbusTransport m_IO;
    private ModbusRequest m_Request;
    private ModbusResponse m_Response;
    private boolean m_ValidityCheck = Modbus.DEFAULT_VALIDITYCHECK;
    private int m_Retries = Modbus.DEFAULT_RETRIES;
    private int m_TransDelayMS = Modbus.DEFAULT_TRANSMIT_DELAY;

    /**
     * Constructs a new <tt>ModbusSerialTransaction</tt>
     * instance.
     */
    public ModbusSerialTransaction() {
    }

    /**
     * Constructs a new <tt>ModbusSerialTransaction</tt>
     * instance with a given <tt>ModbusRequest</tt> to
     * be send when the transaction is executed.
     * <p>
     *
     * @param request a <tt>ModbusRequest</tt> instance.
     */
    public ModbusSerialTransaction(ModbusRequest request) {
        setRequest(request);
    }

    /**
     * Constructs a new <tt>ModbusSerialTransaction</tt>
     * instance with a given <tt>ModbusRequest</tt> to
     * be send when the transaction is executed.
     * <p>
     *
     * @param con a <tt>TCPMasterConnection</tt> instance.
     */
    public ModbusSerialTransaction(SerialConnection con) {
        setSerialConnection(con);
    }

    /**
     * Sets the port on which this <tt>ModbusTransaction</tt>
     * should be executed.<p>
     * <p>
     *
     * @param con a <tt>SerialConnection</tt>.
     */
    public void setSerialConnection(SerialConnection con) {
        m_IO = con.getModbusTransport();
    }

    public void setTransport(ModbusSerialTransport transport) {
        m_IO = transport;
    }

    /**
     * Get the TransDelayMS value.
     *
     * @return the TransDelayMS value.
     */
    public int getTransDelayMS() {
        return m_TransDelayMS;
    }
/**
     * Set the TransDelayMS value.
     *
     * @param newTransDelayMS The new TransDelayMS value.
     */
    public void setTransDelayMS(int newTransDelayMS) {
        this.m_TransDelayMS = newTransDelayMS;
    }

    /**
     * Asserts if this <tt>ModbusTCPTransaction</tt> is
     * executable.
     *
     * @throws ModbusException if the transaction cannot be asserted.
     */
    private void assertExecutable() throws ModbusException {
        if (m_Request == null ||
                m_IO == null) {
            throw new ModbusException("Assertion failed, transaction not executable"
            );
        }
    }
public ModbusRequest getRequest() {
        return m_Request;
    }

    public void setRequest(ModbusRequest req) {
        m_Request = req;
        //m_Response = req.getResponse();
    }
public ModbusResponse getResponse() {
        return m_Response;
    }

    public int getTransactionID() {
        return c_TransactionID;
    }
    public int getRetries() {
        return m_Retries;
    }

        public void setRetries(int num) {
        m_Retries = num;
    }
    public boolean isCheckingValidity() {
        return m_ValidityCheck;
    }

public void setCheckingValidity(boolean b) {
        m_ValidityCheck = b;
    }

    public void execute() throws ModbusIOException, ModbusSlaveException,
            ModbusException {
        //1. assert executeability
        assertExecutable();

        //3. write request, and read response,
        //   while holding the lock on the IO object
        synchronized (m_IO) {
            int tries = 0;
            boolean finished = false;
            do {
                try {
                    if (m_TransDelayMS > 0) {
                        try {
                            Thread.sleep(m_TransDelayMS);
                        }
                        catch (InterruptedException ex) {
                            logger.debug("InterruptedException: %s", ex.getMessage());
                        }
                    }
                    //write request message
                    m_IO.writeMessage(m_Request);
                    //read response message
                    m_Response = m_IO.readResponse();
                    finished = true;
                }
                catch (ModbusIOException e) {
                    if (++tries >= m_Retries) {
                        throw e;
                    }
                    logger.debug("Execute try %d error: %s", tries, e.getMessage());
                }
            } while (!finished);
        }

        //4. deal with exceptions
        if (m_Response instanceof ExceptionResponse) {
            throw new ModbusSlaveException(((ExceptionResponse)m_Response).getExceptionCode()
            );
        }

        if (isCheckingValidity()) {
            checkValidity();
        }
        //toggle the id
        toggleTransactionID();
    }

/**
     * Checks the validity of the transaction, by
     * checking if the values of the response correspond
     * to the values of the request.
     *
     * @throws ModbusException if the transaction is not valid.
     */
    private void checkValidity() throws ModbusException {

    }

/**
     * Toggles the transaction identifier, to ensure
     * that each transaction has a distinctive
     * identifier.<br>
     * When the maximum value of 65535 has been reached,
     * the identifiers will start from zero again.
     */
    private void toggleTransactionID() {
        if (isCheckingValidity()) {
            if (c_TransactionID == (Short.MAX_VALUE * 2)) {
                c_TransactionID = 0;
            }
            else {
                c_TransactionID++;
            }
        }
        m_Request.setTransactionID(getTransactionID());
    }







}
