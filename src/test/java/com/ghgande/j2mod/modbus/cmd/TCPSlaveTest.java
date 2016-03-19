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
package com.ghgande.j2mod.modbus.cmd;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.net.ModbusTCPListener;
import com.ghgande.j2mod.modbus.procimg.*;

import java.net.Inet4Address;

/**
 * Class implementing a simple Modbus/TCP slave. A simple process image is
 * available to test functionality and behaviour of the implementation.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 */
public class TCPSlaveTest {

    private static final Logger logger = Logger.getLogger(PortChangeNotificationsMonitor.class);
    public static void main(String[] args) {

        ModbusTCPListener listener;
        SimpleProcessImage spi;
        int port = Modbus.DEFAULT_PORT;
        int unit = 0;

        try {
            if (args != null && args.length == 1) {
                port = Integer.parseInt(args[0]);
            }
            System.out.println("j2mod Modbus Slave (Server) v0.97");

			/*
             * Create the process image for this test.
			 */
            spi = new SimpleProcessImage();

            spi.addDigitalOut(new SimpleDigitalOut(true));
            spi.addDigitalOut(new SimpleDigitalOut(true));

            spi.addDigitalIn(new SimpleDigitalIn(false));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(false));
            spi.addDigitalIn(new SimpleDigitalIn(true));

            spi.addFile(new File(0, 10).setRecord(0, new Record(0, 10))
                    .setRecord(1, new Record(1, 10))
                    .setRecord(2, new Record(2, 10))
                    .setRecord(3, new Record(3, 10))
                    .setRecord(4, new Record(4, 10))
                    .setRecord(5, new Record(5, 10))
                    .setRecord(6, new Record(6, 10))
                    .setRecord(7, new Record(7, 10))
                    .setRecord(8, new Record(8, 10))
                    .setRecord(9, new Record(9, 10)));

            spi.addFile(new File(1, 20)
                    .setRecord(0, new Record(0, 10))
                    .setRecord(1, new Record(1, 20))
                    .setRecord(2, new Record(2, 20))
                    .setRecord(3, new Record(3, 20))
                    .setRecord(4, new Record(4, 20))
                    .setRecord(5, new Record(5, 20))
                    .setRecord(6, new Record(6, 20))
                    .setRecord(7, new Record(7, 20))
                    .setRecord(8, new Record(8, 20))
                    .setRecord(9, new Record(9, 20))
                    .setRecord(10, new Record(10, 10))
                    .setRecord(11, new Record(11, 20))
                    .setRecord(12, new Record(12, 20))
                    .setRecord(13, new Record(13, 20))
                    .setRecord(14, new Record(14, 20))
                    .setRecord(15, new Record(15, 20))
                    .setRecord(16, new Record(16, 20))
                    .setRecord(17, new Record(17, 20))
                    .setRecord(18, new Record(18, 20))
                    .setRecord(19, new Record(19, 20))
            );

            // allow checking LSB/MSB order
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(true));

            spi.addRegister(new SimpleRegister(251));
            spi.addInputRegister(new SimpleInputRegister(45));

            // 2. create the coupler holding the image
            ModbusCoupler.getReference().setProcessImage(spi);
            ModbusCoupler.getReference().setMaster(false);
            ModbusCoupler.getReference().setUnitID(15);

            // 3. create a listener with 3 threads in pool
            if (Modbus.debug) {
                System.out.println("Listening...");
            }

            listener = new ModbusTCPListener(3, Inet4Address.getByName("0.0.0.0"));
            listener.setPort(port);
            listener.setUnit(unit);
            listener.listen();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}// class TCPSlaveTest
