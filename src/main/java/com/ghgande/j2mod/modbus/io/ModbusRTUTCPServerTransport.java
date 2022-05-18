package com.ghgande.j2mod.modbus.io;

import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;

import java.io.IOException;
import java.net.Socket;

public class ModbusRTUTCPServerTransport extends ModbusTCPSeverTransport {

    /**
     * Default constructor
     */
    public ModbusRTUTCPServerTransport() {
        // RTU over TCP is headless by default
        setHeadless();
    }

    /**
     * Constructs a new <tt>ModbusTransport</tt> instance, for a given
     * <tt>Socket</tt>.
     * <p>
     *
     * @param socket the <tt>Socket</tt> used for message transport.
     */
    public ModbusRTUTCPServerTransport(Socket socket) throws IOException {
        super(socket);
        // RTU over TCP is headless by default
        setHeadless();
    }

    @Override
    public void writeResponse(ModbusResponse msg) throws ModbusIOException {
        writeMessage(msg, true);
    }

    @Override
    public void writeRequest(ModbusRequest msg) throws ModbusIOException {
        writeMessage(msg, true);
    }
}
