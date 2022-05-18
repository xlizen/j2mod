package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.ModbusSlaveException;
import com.ghgande.j2mod.modbus.msg.ExceptionResponse;
import com.ghgande.j2mod.modbus.net.TCPListenerConnection;
import com.ghgande.j2mod.modbus.util.ModbusUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusTCPServerTransaction extends ModbusTransaction {

    private static final Logger logger = LoggerFactory.getLogger(ModbusUDPTransaction.class);

    private TCPListenerConnection connection;


    public ModbusTCPServerTransaction(TCPListenerConnection connection) {
        this.connection = connection;
        transport = connection.getModbusTransport();
    }

    @Override
    public void execute() throws ModbusException {
        if (request == null || connection == null) {
            throw new ModbusException("Invalid request or connection");
        }

        // Try sending the message up to retries time. Note that the message
        // is read immediately after being written, with no flushing of buffers.
        int retryCounter = 0;
        int retryLimit = (retries > 0 ? retries : Modbus.DEFAULT_RETRIES);
        boolean keepTrying = true;

        // While we haven't exhausted all the retry attempts
        while (keepTrying) {

            // Automatically connect if we aren't already connected
            if (!connection.isConnected()) {
                throw new ModbusIOException("No connection for %s:%d", connection.getAddress().toString(), connection.getPort());
            }

            // Make sure the timeout is set
            transport.setTimeout(connection.getTimeout());

            try {

                // Write the message to the endpoint
                if (logger.isDebugEnabled()) {
                    logger.debug("Writing request: {} (try: {}) request transaction ID = {} to {}:{}", request.getHexMessage(), retryCounter, request.getTransactionID(), connection.getAddress(), connection.getPort());
                }
                transport.writeRequest(request);

                // Read the response
                response = transport.readResponse();
                if (logger.isDebugEnabled()) {
                    logger.debug("Read response: {} (try: {}) response transaction ID = {} from {}:{}", response.getHexMessage(), retryCounter, response.getTransactionID(), connection.getAddress(), connection.getPort());
                }
                keepTrying = false;

                // The slave may have returned an exception -- check for that.
                if (response instanceof ExceptionResponse) {
                    throw new ModbusSlaveException(((ExceptionResponse) response).getExceptionCode());
                }

                // We need to keep retrying if;
                //   a) the response is empty OR
                //   b) we have been told to check the validity and the request/response transaction IDs don't match AND
                //   c) we haven't exceeded the maximum retry count
                if (responseIsInValid()) {
                    retryCounter++;
                    if (retryCounter >= retryLimit) {
                        throw new ModbusIOException("Executing transaction failed (tried %d times)", retryLimit);
                    }
                    keepTrying = true;
                    long sleepTime = getRandomSleepTime(retryCounter);
                    if (response == null) {
                        logger.debug("Failed to get any response (try: {}) - retrying after {} milliseconds", retryCounter, sleepTime);
                    } else {
                        logger.debug("Failed to get a valid response, transaction IDs do not match (try: {}) - retrying after {} milliseconds", retryCounter, sleepTime);
                    }
                    ModbusUtil.sleep(sleepTime);
                }
            } catch (ModbusIOException ex) {

                // Up the retry counter and check if we are exhausted
                retryCounter++;
                if (retryCounter >= retryLimit) {
                    throw new ModbusIOException("Executing transaction %s failed (tried %d times) %s", request.getHexMessage(), retryLimit, ex.getMessage());
                } else {
                    long sleepTime = getRandomSleepTime(retryCounter);
                    logger.debug("Failed transaction Request: {} (try: {}) - retrying after {} milliseconds", request.getHexMessage(), retryCounter, sleepTime);
                    ModbusUtil.sleep(sleepTime);
                }

                // If this has happened, then we should close and re-open the connection before re-trying
                logger.debug("Failed request {} (try: {}) request transaction ID = {} - {} closing and re-opening connection {}:{}", request.getHexMessage(), retryCounter, request.getTransactionID(), ex.getMessage(), connection.getAddress().toString(), connection.getPort());
                connection.close();
            }

            // Increment the transaction ID if we are still trying
            if (keepTrying) {
                incrementTransactionID();
            }
        }

        incrementTransactionID();
    }

    /**
     * Returns true if the response is not valid
     * This can be if the response is null or the transaction ID of the request
     * doesn't match the reponse
     *
     * @return True if invalid
     */
    private boolean responseIsInValid() {
        if (response == null) {
            return true;
        } else if (!response.isHeadless() && validityCheck) {
            return request.getTransactionID() != response.getTransactionID();
        } else {
            return false;
        }
    }

    /**
     * incrementTransactionID -- Increment the transaction ID for the next
     * transaction. Note that the caller must get the new transaction ID with
     * getTransactionID(). This is only done validity checking is enabled so
     * that dumb slaves don't cause problems. The original request will have its
     * transaction ID incremented as well so that sending the same transaction
     * again won't cause problems.
     */
    private synchronized void incrementTransactionID() {
        if (isCheckingValidity()) {
            if (transactionID >= Modbus.MAX_TRANSACTION_ID) {
                transactionID = Modbus.DEFAULT_TRANSACTION_ID;
            } else {
                transactionID++;
            }
        }
        request.setTransactionID(getTransactionID());
    }
}
