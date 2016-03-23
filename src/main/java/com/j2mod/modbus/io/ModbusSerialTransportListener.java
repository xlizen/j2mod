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

import com.fazecast.jSerialComm.SerialPort;

/**
 * Any class that wants to listen for the begining and ending of read/writes
 * to the Serial channel need to implement this interface
 *
 * @author Steve O'Hara (4energy)
 * @version 2.0 (March 2016)
 *
 */
public interface ModbusSerialTransportListener {

    enum EventType {
        BEFORE_WRITE_MESSAGE, AFTER_WRITE_MESSAGE, BEFORE_READ_REQUEST, AFTER_READ_REQUEST, BEFORE_READ_RESPONSE, AFTER_READ_RESPONSE
    }

    /**
     * Called whenever any of the events occur
     *
     * @param eventType The type of the event
     * @param port      The comms port
     */
    void event(EventType eventType, SerialPort port);
}
