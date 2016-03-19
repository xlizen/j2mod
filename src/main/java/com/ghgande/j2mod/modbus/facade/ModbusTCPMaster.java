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
package com.ghgande.j2mod.modbus.facade;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.util.BitVector;
import com.ghgande.j2mod.modbus.util.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Modbus/TCP Master facade.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class ModbusTCPMaster {

    private static final Logger logger = Logger.getLogger(ModbusTCPMaster.class);

    private TCPMasterConnection m_Connection;
    private InetAddress m_SlaveAddress;
    private ModbusTCPTransaction m_Transaction;
    private ReadCoilsRequest m_ReadCoilsRequest;
    private ReadInputDiscretesRequest m_ReadInputDiscretesRequest;
    private WriteCoilRequest m_WriteCoilRequest;
    private WriteMultipleCoilsRequest m_WriteMultipleCoilsRequest;
    private ReadInputRegistersRequest m_ReadInputRegistersRequest;
    private ReadMultipleRegistersRequest m_ReadMultipleRegistersRequest;
    private WriteSingleRegisterRequest m_WriteSingleRegisterRequest;
    private WriteMultipleRegistersRequest m_WriteMultipleRegistersRequest;
    private boolean m_Reconnecting = false;

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param addr an internet address as resolvable IP name or IP number,
     *             specifying the slave to communicate with.
     */
    public ModbusTCPMaster(String addr) {
        try {
            m_SlaveAddress = InetAddress.getByName(addr);
            m_Connection = new TCPMasterConnection(m_SlaveAddress);
            m_ReadCoilsRequest = new ReadCoilsRequest();
            m_ReadInputDiscretesRequest = new ReadInputDiscretesRequest();
            m_WriteCoilRequest = new WriteCoilRequest();
            m_WriteMultipleCoilsRequest = new WriteMultipleCoilsRequest();
            m_ReadInputRegistersRequest = new ReadInputRegistersRequest();
            m_ReadMultipleRegistersRequest = new ReadMultipleRegistersRequest();
            m_WriteSingleRegisterRequest = new WriteSingleRegisterRequest();
            m_WriteMultipleRegistersRequest = new WriteMultipleRegistersRequest();

        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param addr an internet address as resolvable IP name or IP number,
     *             specifying the slave to communicate with.
     * @param port the port the slave is listening to.
     */
    public ModbusTCPMaster(String addr, int port) {
        this(addr);
        m_Connection.setPort(port);
    }

    /**
     * Connects this <tt>ModbusTCPMaster</tt> with the slave.
     *
     * @throws Exception if the connection cannot be established.
     */
    public synchronized void connect() throws Exception {
        if (m_Connection != null && !m_Connection.isConnected()) {
            m_Connection.connect();
            m_Transaction = new ModbusTCPTransaction(m_Connection);
            m_Transaction.setReconnecting(m_Reconnecting);
        }
    }

    /**
     * Disconnects this <tt>ModbusTCPMaster</tt> from the slave.
     */
    public synchronized void disconnect() {
        if (m_Connection != null && m_Connection.isConnected()) {
            m_Connection.close();
            m_Transaction = null;
        }
    }

    /**
     * Sets the flag that specifies whether to maintain a
     * constant connection or reconnect for every transaction.
     *
     * @param b true if a new connection should be established for each
     *          transaction, false otherwise.
     */
    public synchronized void setReconnecting(boolean b) {
        m_Reconnecting = b;
        if (m_Transaction != null) {
            m_Transaction.setReconnecting(b);
        }
    }

    /**
     * Tests if a constant connection is maintained or if a new
     * connection is established for every transaction.
     *
     * @return true if a new connection should be established for each
     * transaction, false otherwise.
     */
    public boolean isReconnecting() {
        return m_Reconnecting;
    }

    /**
     * Reads a given number of coil states from the slave.
     * <p/>
     * Note that the number of bits in the bit vector will be
     * forced to the number originally requested.
     *
     * @param ref   the offset of the coil to start reading from.
     * @param count the number of coil states to be read.
     *
     * @return a <tt>BitVector</tt> instance holding the
     * received coil states.
     *
     * @throws ModbusException if an I/O error, a slave exception or
     *                         a transaction error occurs.
     */
    public synchronized BitVector readCoils(int ref, int count) throws ModbusException {
        m_ReadCoilsRequest.setReference(ref);
        m_ReadCoilsRequest.setBitCount(count);
        m_Transaction.setRequest(m_ReadCoilsRequest);
        m_Transaction.execute();
        BitVector bv = ((ReadCoilsResponse)m_Transaction.getResponse()).getCoils();
        bv.forceSize(count);
        return bv;
    }

    /**
     * Writes a coil state to the slave.
     *
     * @param unitid the slave unit id.
     * @param ref    the offset of the coil to be written.
     * @param state  the coil state to be written.
     *
     * @return the state of the coil as returned from the slave.
     *
     * @throws ModbusException if an I/O error, a slave exception or
     *                         a transaction error occurs.
     */
    public synchronized boolean writeCoil(int unitid, int ref, boolean state) throws ModbusException {
        m_WriteCoilRequest.setUnitID(unitid);
        m_WriteCoilRequest.setReference(ref);
        m_WriteCoilRequest.setCoil(state);
        m_Transaction.setRequest(m_WriteCoilRequest);
        m_Transaction.execute();
        return ((WriteCoilResponse)m_Transaction.getResponse()).getCoil();
    }

    /**
     * Writes a given number of coil states to the slave.
     * <p/>
     * Note that the number of coils to be written is given
     * implicitly, through {@link BitVector#size()}.
     *
     * @param ref   the offset of the coil to start writing to.
     * @param coils a <tt>BitVector</tt> which holds the coil states to be written.
     *
     * @throws ModbusException if an I/O error, a slave exception or
     *                         a transaction error occurs.
     */
    public synchronized void writeMultipleCoils(int ref, BitVector coils) throws ModbusException {
        m_WriteMultipleCoilsRequest.setReference(ref);
        m_WriteMultipleCoilsRequest.setCoils(coils);
        m_Transaction.setRequest(m_WriteMultipleCoilsRequest);
        m_Transaction.execute();
    }

    /**
     * Reads a given number of input discrete states from the slave.
     * <p/>
     * Note that the number of bits in the bit vector will be
     * forced to the number originally requested.
     *
     * @param ref   the offset of the input discrete to start reading from.
     * @param count the number of input discrete states to be read.
     *
     * @return a <tt>BitVector</tt> instance holding the received input discrete
     * states.
     *
     * @throws ModbusException if an I/O error, a slave exception or
     *                         a transaction error occurs.
     */
    public synchronized BitVector readInputDiscretes(int ref, int count) throws ModbusException {
        m_ReadInputDiscretesRequest.setReference(ref);
        m_ReadInputDiscretesRequest.setBitCount(count);
        m_Transaction.setRequest(m_ReadInputDiscretesRequest);
        m_Transaction.execute();
        BitVector bv = ((ReadInputDiscretesResponse)m_Transaction.getResponse()).getDiscretes();
        bv.forceSize(count);
        return bv;
    }

    /**
     * Reads a given number of input registers from the slave.
     * <p/>
     * Note that the number of input registers returned (i.e. array length)
     * will be according to the number received in the slave response.
     *
     * @param ref   the offset of the input register to start reading from.
     * @param count the number of input registers to be read.
     *
     * @return a <tt>InputRegister[]</tt> with the received input registers.
     *
     * @throws ModbusException if an I/O error, a slave exception or
     *                         a transaction error occurs.
     */
    public synchronized InputRegister[] readInputRegisters(int ref, int count) throws ModbusException {
        m_ReadInputRegistersRequest.setReference(ref);
        m_ReadInputRegistersRequest.setWordCount(count);
        m_Transaction.setRequest(m_ReadInputRegistersRequest);
        m_Transaction.execute();
        return ((ReadInputRegistersResponse)m_Transaction.getResponse()).getRegisters();
    }

    /**
     * Reads a given number of registers from the slave.
     * <p/>
     * Note that the number of registers returned (i.e. array length)
     * will be according to the number received in the slave response.
     *
     * @param ref   the offset of the register to start reading from.
     * @param count the number of registers to be read.
     *
     * @return a <tt>Register[]</tt> holding the received registers.
     *
     * @throws ModbusException if an I/O error, a slave exception or
     *                         a transaction error occurs.
     */
    public synchronized Register[] readMultipleRegisters(int ref, int count) throws ModbusException {
        m_ReadMultipleRegistersRequest.setReference(ref);
        m_ReadMultipleRegistersRequest.setWordCount(count);
        m_Transaction.setRequest(m_ReadMultipleRegistersRequest);
        m_Transaction.execute();
        return ((ReadMultipleRegistersResponse)m_Transaction.getResponse()).getRegisters();
    }

    /**
     * Writes a single register to the slave.
     *
     * @param ref      the offset of the register to be written.
     * @param register a <tt>Register</tt> holding the value of the register
     *                 to be written.
     *
     * @throws ModbusException if an I/O error, a slave exception or
     *                         a transaction error occurs.
     */
    public synchronized void writeSingleRegister(int ref, Register register) throws ModbusException {
        m_WriteSingleRegisterRequest.setReference(ref);
        m_WriteSingleRegisterRequest.setRegister(register);
        m_Transaction.setRequest(m_WriteSingleRegisterRequest);
        m_Transaction.execute();
    }

    /**
     * Writes a number of registers to the slave.
     *
     * @param ref       the offset of the register to start writing to.
     * @param registers a <tt>Register[]</tt> holding the values of
     *                  the registers to be written.
     *
     * @throws ModbusException if an I/O error, a slave exception or
     *                         a transaction error occurs.
     */
    public synchronized void writeMultipleRegisters(int ref, Register[] registers) throws ModbusException {
        m_WriteMultipleRegistersRequest.setReference(ref);
        m_WriteMultipleRegistersRequest.setRegisters(registers);
        m_Transaction.setRequest(m_WriteMultipleRegistersRequest);
        m_Transaction.execute();
    }

}