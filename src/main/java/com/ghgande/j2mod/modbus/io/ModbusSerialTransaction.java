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
package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.ModbusSlaveException;
import com.ghgande.j2mod.modbus.msg.ExceptionResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;

/**
 * Class implementing the <tt>ModbusTransaction</tt>
 * interface.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ModbusSerialTransaction
        implements ModbusTransaction {

    //class attributes
    private static int c_TransactionID = Modbus.DEFAULT_TRANSACTION_ID;

    //instance attributes and associations
    private ModbusTransport m_IO;
    private ModbusRequest m_Request;
    private ModbusResponse m_Response;
    private boolean m_ValidityCheck = Modbus.DEFAULT_VALIDITYCHECK;
    private int m_Retries = Modbus.DEFAULT_RETRIES;
    private int m_TransDelayMS = Modbus.DEFAULT_TRANSMIT_DELAY;
    private SerialConnection m_SerialCon;
    private final Object MUTEX = new Object();

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
        m_SerialCon = con;
        m_IO = m_SerialCon.getModbusTransport();
    }

    public void setTransport(ModbusSerialTransport transport) {
        m_IO = transport;
    }

    public int getTransactionID() {
        return c_TransactionID;
    }

    public void setRequest(ModbusRequest req) {
        m_Request = req;
        //m_Response = req.getResponse();
    }

    public ModbusRequest getRequest() {
        return m_Request;
    }

    public ModbusResponse getResponse() {
        return m_Response;
    }

    public void setCheckingValidity(boolean b) {
        m_ValidityCheck = b;
    }

    public boolean isCheckingValidity() {
        return m_ValidityCheck;
    }

    public int getRetries() {
        return m_Retries;
    }

    public void setRetries(int num) {
        m_Retries = num;
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
                            if (Modbus.debug) {
                                System.err.println("InterruptedException: " + ex.getMessage());
                            }
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
                    if (Modbus.debug) {
                        System.err.println("Execute try " + tries + " error: " + e.getMessage());
                    }
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
