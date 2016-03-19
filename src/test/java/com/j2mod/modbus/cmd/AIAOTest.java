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
package com.j2mod.modbus.cmd;

import com.j2mod.modbus.Modbus;
import com.j2mod.modbus.io.ModbusTCPTransaction;
import com.j2mod.modbus.io.ModbusTransaction;
import com.j2mod.modbus.msg.ModbusRequest;
import com.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.j2mod.modbus.net.TCPMasterConnection;
import com.j2mod.modbus.procimg.SimpleRegister;
import com.j2mod.modbus.util.Logger;

import java.net.InetAddress;

/**
 * <p>
 * Class that implements a simple commandline tool which demonstrates how a
 * analog input can be bound with a analog output.
 *
 * <p>
 * Note that if you write to a remote I/O with a Modbus protocol stack, it will
 * most likely expect that the communication is <i>kept alive</i> after the
 * first write message.
 *
 * <p>
 * This can be achieved either by sending any kind of message, or by repeating
 * the write message within a given period of time.
 *
 * <p>
 * If the time period is exceeded, then the device might react by turning out
 * all signals of the I/O modules. After this timeout, the device might require
 * a reset message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class AIAOTest {

    private static final Logger logger = Logger.getLogger(AIAOTest.class);

    private static void printUsage() {
        logger.debug("java com.ghgande.j2mod.modbus.cmd.AIAOTest" + " <address{:<port>} [String]> <register a_in [int16]>" + " <register a_out [int16]>");
    }

    public static void main(String[] args) {

        InetAddress addr = null;
        TCPMasterConnection con = null;
        ModbusRequest ai_req;
        WriteSingleRegisterRequest ao_req;

        ModbusTransaction ai_trans;
        ModbusTransaction ao_trans;

        int ai_ref = 0;
        int ao_ref = 0;
        int port = Modbus.DEFAULT_PORT;
        int unit_in = 0;
        int unit_out = 0;

        // 1. Setup the parameters
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }
        try {

            try {
                String serverAddress = args[0];
                String parts[] = serverAddress.split(" *: *");

                String address = parts[0];
                if (parts.length > 1) {
                    port = Integer.parseInt(parts[1]);
                    if (parts.length > 2) {
                        unit_in = unit_out = Integer.parseInt(parts[2]);
                        if (parts.length > 3) {
                            unit_out = Integer.parseInt(parts[3]);
                        }
                    }
                }
                addr = InetAddress.getByName(address);
                ai_ref = Integer.parseInt(args[1]);
                ao_ref = Integer.parseInt(args[2]);
            }
            catch (Exception ex) {
                ex.printStackTrace();
                printUsage();
                System.exit(1);
            }

            // 2. Open the connection
            con = new TCPMasterConnection(addr);
            con.setPort(port);
            con.connect();
            logger.debug("Connected to " + addr.toString() + ":" + con.getPort());

            // 3. Prepare the requests
            ai_req = new ReadInputRegistersRequest(ai_ref, 1);
            ao_req = new WriteSingleRegisterRequest();
            ao_req.setReference(ao_ref);

            ai_req.setUnitID(unit_in);
            ao_req.setUnitID(unit_out);

            // 4. Prepare the transactions
            ai_trans = new ModbusTCPTransaction(con);
            ai_trans.setRequest(ai_req);
            ao_trans = new ModbusTCPTransaction(con);
            ao_trans.setRequest(ao_req);

            // 5. Prepare holders to update only on change
            SimpleRegister new_out = new SimpleRegister(0);
            ao_req.setRegister(new_out);
            int last_out = Integer.MIN_VALUE;

            // 5. Execute the transaction repeatedly
            do {
                ai_trans.execute();
                int new_in = ((ReadInputRegistersResponse)ai_trans.getResponse()).getRegister(0).getValue();

                // write only if differ
                if (new_in != last_out) {
                    new_out.setValue(new_in); // update register
                    ao_trans.execute();
                    last_out = new_in;
                    logger.debug("Updated Output Register with value from Input Register");
                }
            } while (true);

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            // 6. Close the connection
            if (con != null) {
                con.close();
            }
        }
    }
}
