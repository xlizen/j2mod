package com.ghgande.j2mod.modbus.util;

import com.ghgande.j2mod.modbus.facade.ModbusTCPServerMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.sun.deploy.util.StringUtils;

import java.util.Collections;

public class ModServerPoll {

    public static void main(String[] args) {
        ModbusTCPServerMaster modbusTCPServerMaster = new ModbusTCPServerMaster("127.0.0.1", 1280, false);


        try {
            modbusTCPServerMaster.connect();

            Thread.sleep(3000);

            do {
                Thread.sleep(100);
            }while (!modbusTCPServerMaster.isConnected());

            InputRegister[] inputRegisters = modbusTCPServerMaster.readInputRegisters(1, 0, 5);

            System.out.println(StringUtils.join(Collections.singleton(inputRegisters),","));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
