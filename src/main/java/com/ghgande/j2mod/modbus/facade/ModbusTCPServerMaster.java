package com.ghgande.j2mod.modbus.facade;

import com.ghgande.j2mod.modbus.io.AbstractModbusTransport;
import com.ghgande.j2mod.modbus.net.TCPListenerConnection;
import com.ghgande.j2mod.modbus.util.Observable;
import com.ghgande.j2mod.modbus.util.Observer;

import java.net.InetAddress;

public class ModbusTCPServerMaster extends AbstractModbusMaster implements Observer {

    private final boolean useRtuOverTcp;

    private TCPListenerConnection connection;

    private String addr;
    private int port;

    private volatile boolean connected;

    public ModbusTCPServerMaster(String addr, int port, boolean useRtuOverTcp) {
        this.addr = addr;
        this.port = port;
        this.useRtuOverTcp = useRtuOverTcp;
    }


    @Override
    public void connect() throws Exception {
        connection = new TCPListenerConnection(InetAddress.getByName(addr));
        connection.setPort(port);
        connection.addObserver(this);
        connection.listener(useRtuOverTcp);
    }

    @Override
    public void disconnect() {
        connection.close();
    }

    @Override
    public AbstractModbusTransport getTransport() {
        return connection == null ? null : connection.getModbusTransport();
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void update(Observable o, Object arg) {
        transaction = connection.getModbusTransport().createTransaction();
        connected = true;
    }
}
