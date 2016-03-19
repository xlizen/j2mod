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
import com.ghgande.j2mod.modbus.net.UDPMasterConnection;
import com.ghgande.j2mod.modbus.net.UDPTerminal;

/**
 * Class implementing the <tt>ModbusTransaction</tt>
 * interface for the UDP transport mechanism.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ModbusUDPTransaction
        implements ModbusTransaction {

    //class attributes
    private static int c_TransactionID = Modbus.DEFAULT_TRANSACTION_ID;

    //instance attributes and associations
    private UDPTerminal m_Terminal;
    private ModbusTransport m_IO;
    private ModbusRequest m_Request;
    private ModbusResponse m_Response;
    private boolean m_ValidityCheck = Modbus.DEFAULT_VALIDITYCHECK;
    private int m_Retries = Modbus.DEFAULT_RETRIES;
    private int m_RetryCounter = 0;

    /**
     * Constructs a new <tt>ModbusUDPTransaction</tt>
     * instance.
     */
    public ModbusUDPTransaction() {
    }

    /**
     * Constructs a new <tt>ModbusUDPTransaction</tt>
     * instance with a given <tt>ModbusRequest</tt> to
     * be send when the transaction is executed.
     * <p>
     *
     * @param request a <tt>ModbusRequest</tt> instance.
     */
    public ModbusUDPTransaction(ModbusRequest request) {
        setRequest(request);
    }

    /**
     * Constructs a new <tt>ModbusUDPTransaction</tt>
     * instance with a given <tt>UDPTerminal</tt> to
     * be used for transactions.
     * <p>
     *
     * @param terminal a <tt>UDPTerminal</tt> instance.
     */
    public ModbusUDPTransaction(UDPTerminal terminal) {
        setTerminal(terminal);
    }

    /**
     * Constructs a new <tt>ModbusUDPTransaction</tt>
     * instance with a given <tt>ModbusUDPConnection</tt>
     * to be used for transactions.
     * <p>
     *
     * @param con a <tt>ModbusUDPConnection</tt> instance.
     */
    public ModbusUDPTransaction(UDPMasterConnection con) {
        setTerminal(con.getTerminal());
    }

    /**
     * Sets the terminal on which this <tt>ModbusTransaction</tt>
     * should be executed.<p>
     *
     * @param terminal a <tt>UDPSlaveTerminal</tt>.
     */
    public void setTerminal(UDPTerminal terminal) {
        m_Terminal = terminal;
        if (terminal.isActive()) {
            m_IO = terminal.getModbusTransport();
        }
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

    public int getTransactionID() {
        return c_TransactionID & 0x0000FFFF;
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

    public void execute() throws ModbusIOException, ModbusSlaveException,
            ModbusException {

        //1. assert executeability
        assertExecutable();
        //2. open the connection if not connected
        if (!m_Terminal.isActive()) {
            try {
                m_Terminal.activate();
                m_IO = m_Terminal.getModbusTransport();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                throw new ModbusIOException("Activation failed.");

            }
        }

        //3. Retry transaction m_Retries times, in case of
        //I/O Exception problems.
        m_RetryCounter = 0;
        while (m_RetryCounter <= m_Retries) {
            try {
                //3. write request, and read response,
                //   while holding the lock on the IO object
                synchronized (m_IO) {
                    //write request message
                    m_IO.writeMessage(m_Request);
                    //read response message
                    m_Response = m_IO.readResponse();
                    break;
                }
            }
            catch (ModbusIOException ex) {
                m_RetryCounter++;
            }
        }

        //4. deal with "application level" exceptions
        if (m_Response instanceof ExceptionResponse) {
            throw new ModbusSlaveException(((ExceptionResponse)m_Response).getExceptionCode()
            );
        }

        if (isCheckingValidity()) {
            checkValidity();
        }

        //toggle the id
        incrementTransactionID();
    }

    /**
     * Asserts if this <tt>ModbusTCPTransaction</tt> is
     * executable.
     *
     * @throws ModbusException if this transaction cannot be
     *                         asserted as executable.
     */
    private void assertExecutable() throws ModbusException {
        if (m_Request == null || m_Terminal == null) {
            throw new ModbusException("Assertion failed, transaction not executable");
        }
    }

    /**
     * Checks the validity of the transaction, by
     * checking if the values of the response correspond
     * to the values of the request.
     *
     * @throws ModbusException if this transaction has not been valid.
     */
    private void checkValidity() throws ModbusException {
        //1.check transaction number
        //if(m_Request.getTransactionID()!=m_Response.getTransactionID()) {

        //}

    }

    /**
     * Toggles the transaction identifier, to ensure
     * that each transaction has a distinctive
     * identifier.<br>
     * When the maximum value of 65535 has been reached,
     * the identifiers will start from zero again.
     */
    private void incrementTransactionID() {
        if (isCheckingValidity()) {
            if (c_TransactionID >= Modbus.MAX_TRANSACTION_ID) {
                c_TransactionID = 1;
            }
            else {
                c_TransactionID++;
            }
        }
        m_Request.setTransactionID(getTransactionID());
    }
}