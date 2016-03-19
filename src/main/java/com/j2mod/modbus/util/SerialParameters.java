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
 * along with j2mod.  If not, see <http://www.gnu.org/licenses
 */
package com.j2mod.modbus.util;

import com.fazecast.jSerialComm.SerialPort;
import com.j2mod.modbus.Modbus;

import java.util.Properties;

/**
 * Helper class wrapping all serial port communication parameters.
 * Very similar to the javax.comm demos, however, not the same.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @version 1.2rc1 (09/11/2004)
 */
public class SerialParameters {

    private static final Logger logger = Logger.getLogger(SerialParameters.class);

    //instance attributes
    private String m_PortName;
    private int m_BaudRate;
    private int m_FlowControlIn;
    private int m_FlowControlOut;
    private int m_Databits;
    private int m_Stopbits;
    private int m_Parity;
    private String m_Encoding;
    private boolean m_Echo;

    /**
     * Constructs a new <tt>SerialParameters</tt> instance with
     * default values.
     */
    public SerialParameters() {
        m_PortName = "";
        m_BaudRate = 9600;
        m_FlowControlIn = SerialPort.FLOW_CONTROL_DISABLED;
        m_FlowControlOut = SerialPort.FLOW_CONTROL_DISABLED;
        m_Databits = 8;
        m_Stopbits = SerialPort.ONE_STOP_BIT;
        m_Parity = SerialPort.NO_PARITY;
        m_Encoding = Modbus.DEFAULT_SERIAL_ENCODING;
        m_Echo = false;
    }

    /**
     * Constructs a new <tt>SerialParameters<tt> instance with
     * given parameters.
     *
     * @param portName       The name of the port.
     * @param baudRate       The baud rate.
     * @param flowControlIn  Type of flow control for receiving.
     * @param flowControlOut Type of flow control for sending.
     * @param databits       The number of data bits.
     * @param stopbits       The number of stop bits.
     * @param parity         The type of parity.
     * @param echo           Flag for setting the RS485 echo mode.
     */
    public SerialParameters(String portName, int baudRate,
                            int flowControlIn,
                            int flowControlOut,
                            int databits,
                            int stopbits,
                            int parity,
                            boolean echo) {
        m_PortName = portName;
        m_BaudRate = baudRate;
        m_FlowControlIn = flowControlIn;
        m_FlowControlOut = flowControlOut;
        m_Databits = databits;
        m_Stopbits = stopbits;
        m_Parity = parity;
        m_Echo = echo;
    }

    /**
     * Constructs a new <tt>SerialParameters</tt> instance with
     * parameters obtained from a <tt>Properties</tt> instance.
     *
     * @param props  a <tt>Properties</tt> instance.
     * @param prefix a prefix for the properties keys if embedded into
     *               other properties.
     */
    public SerialParameters(Properties props, String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        setPortName(props.getProperty(prefix + "portName", ""));
        setBaudRate(props.getProperty(prefix + "baudRate", "" + 9600));
        setFlowControlIn(props.getProperty(prefix + "flowControlIn", "" + SerialPort.FLOW_CONTROL_DISABLED));
        setFlowControlOut(props.getProperty(prefix + "flowControlOut", "" + SerialPort.FLOW_CONTROL_DISABLED));
        setParity(props.getProperty(prefix + "parity", "" + SerialPort.NO_PARITY));
        setDatabits(props.getProperty(prefix + "databits", "8"));
        setStopbits(props.getProperty(prefix + "stopbits", "" + SerialPort.ONE_STOP_BIT));
        setEncoding(props.getProperty(prefix + "encoding", Modbus.DEFAULT_SERIAL_ENCODING));
        setEcho("true".equals(props.getProperty(prefix + "echo")));
    }

    /**
     * Returns the port name.
     *
     * @return the port name.
     */
    public String getPortName() {
        return m_PortName;
    }

    /**
     * Sets the port name.
     *
     * @param name the new port name.
     */
    public void setPortName(String name) {
        m_PortName = name;
    }

    /**
     * Sets the baud rate.
     *
     * @param rate the new baud rate.
     */
    public void setBaudRate(int rate) {
        m_BaudRate = rate;
    }

    /**
     * Return the baud rate as <tt>int</tt>.
     *
     * @return the baud rate as <tt>int</tt>.
     */
    public int getBaudRate() {
        return m_BaudRate;
    }

    /**
     * Sets the baud rate.
     *
     * @param rate the new baud rate.
     */
    public void setBaudRate(String rate) {
        m_BaudRate = Integer.parseInt(rate);
    }

    /**
     * Returns the baud rate as a <tt>String</tt>.
     *
     * @return the baud rate as <tt>String</tt>.
     */
    public String getBaudRateString() {
        return Integer.toString(m_BaudRate);
    }

    /**
     * Sets the type of flow control for the input
     * as given by the passed in <tt>int</tt>.
     *
     * @param flowcontrol the new flow control type.
     */
    public void setFlowControlIn(int flowcontrol) {
        m_FlowControlIn = flowcontrol;
    }

    /**
     * Returns the input flow control type as <tt>int</tt>.
     *
     * @return the input flow control type as <tt>int</tt>.
     */
    public int getFlowControlIn() {
        return m_FlowControlIn;
    }

    /**
     * Sets the type of flow control for the input
     * as given by the passed in <tt>String</tt>.
     *
     * @param flowcontrol the flow control for reading type.
     */
    public void setFlowControlIn(String flowcontrol) {
        m_FlowControlIn = stringToFlow(flowcontrol);
    }

    /**
     * Returns the input flow control type as <tt>String</tt>.
     *
     * @return the input flow control type as <tt>String</tt>.
     */
    public String getFlowControlInString() {
        return flowToString(m_FlowControlIn);
    }

    /**
     * Sets the output flow control type as given
     * by the passed in <tt>int</tt>.
     *
     * @param flowControlOut new output flow control type as <tt>int</tt>.
     */
    public void setFlowControlOut(int flowControlOut) {
        m_FlowControlOut = flowControlOut;
    }

    /**
     * Returns the output flow control type as <tt>int</tt>.
     *
     * @return the output flow control type as <tt>int</tt>.
     */
    public int getFlowControlOut() {
        return m_FlowControlOut;
    }

    /**
     * Sets the output flow control type as given
     * by the passed in <tt>String</tt>.
     *
     * @param flowControlOut the new output flow control type as <tt>String</tt>.
     */
    public void setFlowControlOut(String flowControlOut) {
        m_FlowControlOut = stringToFlow(flowControlOut);
    }

    /**
     * Returns the output flow control type as <tt>String</tt>.
     *
     * @return the output flow control type as <tt>String</tt>.
     */
    public String getFlowControlOutString() {
        return flowToString(m_FlowControlOut);
    }

    /**
     * Sets the number of data bits.
     *
     * @param databits the new number of data bits.
     */
    public void setDatabits(int databits) {
        m_Databits = databits;
    }

    /**
     * Returns the number of data bits as <tt>int</tt>.
     *
     * @return the number of data bits as <tt>int</tt>.
     */
    public int getDatabits() {
        return m_Databits;
    }

    /**
     * Sets the number of data bits from the given <tt>String</tt>.
     *
     * @param databits the new number of data bits as <tt>String</tt>.
     */
    public void setDatabits(String databits) {
        if (databits != null && !databits.isEmpty()) {
            m_Databits = Integer.parseInt(databits);
        }
        else {
            m_Databits = 8;
        }
    }

    /**
     * Returns the number of data bits as <tt>String</tt>.
     *
     * @return the number of data bits as <tt>String</tt>.
     */
    public String getDatabitsString() {
        return m_Databits + "";
    }

    /**
     * Sets the number of stop bits.
     *
     * @param stopbits the new number of stop bits setting.
     */
    public void setStopbits(int stopbits) {
        m_Stopbits = stopbits;
    }

    /**
     * Returns the number of stop bits as <tt>int</tt>.
     *
     * @return the number of stop bits as <tt>int</tt>.
     */
    public int getStopbits() {
        return m_Stopbits;
    }

    /**
     * Sets the number of stop bits from the given <tt>String</tt>.
     *
     * @param stopbits the number of stop bits as <tt>String</tt>.
     */
    public void setStopbits(String stopbits) {
        if (stopbits.equals("1")) {
            m_Stopbits = SerialPort.ONE_STOP_BIT;
        }
        if (stopbits.equals("1.5")) {
            m_Stopbits = SerialPort.ONE_POINT_FIVE_STOP_BITS;
        }
        if (stopbits.equals("2")) {
            m_Stopbits = SerialPort.TWO_STOP_BITS;
        }
    }

    /**
     * Returns the number of stop bits as <tt>String</tt>.
     *
     * @return the number of stop bits as <tt>String</tt>.
     */
    public String getStopbitsString() {
        switch (m_Stopbits) {
            case SerialPort.ONE_STOP_BIT:
                return "1";
            case SerialPort.ONE_POINT_FIVE_STOP_BITS:
                return "1.5";
            case SerialPort.TWO_STOP_BITS:
                return "2";
            default:
                return "1";
        }
    }

    /**
     * Sets the parity schema.
     *
     * @param parity the new parity schema as <tt>int</tt>.
     */
    public void setParity(int parity) {
        m_Parity = parity;
    }

    /**
     * Returns the parity schema as <tt>int</tt>.
     *
     * @return the parity schema as <tt>int</tt>.
     */
    public int getParity() {
        return m_Parity;
    }

    /**
     * Sets the parity schema from the given
     * <tt>String</tt>.
     *
     * @param parity the new parity schema as <tt>String</tt>.
     */
    public void setParity(String parity) {
        parity = parity.toLowerCase();
        if (parity.equals("none")) {
            m_Parity = SerialPort.NO_PARITY;
        }
        if (parity.equals("even")) {
            m_Parity = SerialPort.EVEN_PARITY;
        }
        if (parity.equals("odd")) {
            m_Parity = SerialPort.ODD_PARITY;
        }
        if (parity.equals("mark")) {
            m_Parity = SerialPort.MARK_PARITY;
        }
        if (parity.equals("space")) {
            m_Parity = SerialPort.SPACE_PARITY;
        }
    }

    /**
     * Returns the parity schema as <tt>String</tt>.
     *
     * @return the parity schema as <tt>String</tt>.
     */
    public String getParityString() {
        switch (m_Parity) {
            case SerialPort.NO_PARITY:
                return "none";
            case SerialPort.EVEN_PARITY:
                return "even";
            case SerialPort.ODD_PARITY:
                return "odd";
            case SerialPort.MARK_PARITY:
                return "mark";
            case SerialPort.SPACE_PARITY:
                return "space";
            default:
                return "none";
        }
    }

    /**
     * Returns the encoding to be used.
     *
     * @return the encoding as string.
     *
     * @see Modbus#SERIAL_ENCODING_ASCII
     * @see Modbus#SERIAL_ENCODING_RTU
     * @see Modbus#SERIAL_ENCODING_BIN
     */
    public String getEncoding() {
        return m_Encoding;
    }

    /**
     * Sets the encoding to be used.
     *
     * @param enc the encoding as string.
     *
     * @see Modbus#SERIAL_ENCODING_ASCII
     * @see Modbus#SERIAL_ENCODING_RTU
     * @see Modbus#SERIAL_ENCODING_BIN
     */
    public void setEncoding(String enc) {
        enc = enc.toLowerCase();
        if (enc.equals(Modbus.SERIAL_ENCODING_ASCII) ||
                enc.equals(Modbus.SERIAL_ENCODING_RTU) ||
                enc.equals(Modbus.SERIAL_ENCODING_BIN)
                ) {
            m_Encoding = enc;
        }
        else {
            m_Encoding = Modbus.DEFAULT_SERIAL_ENCODING;
        }
    }

    /**
     * Get the Echo value.
     *
     * @return the Echo value.
     */
    public boolean isEcho() {
        return m_Echo;
    }

    /**
     * Set the Echo value.
     *
     * @param newEcho The new Echo value.
     */
    public void setEcho(boolean newEcho) {
        m_Echo = newEcho;
    }

    /**
     * Converts a <tt>String</tt> describing a flow control type to the
     * <tt>int</tt> which is defined in SerialPort.
     *
     * @param flowcontrol the <tt>String</tt> describing the flow control type.
     *
     * @return the <tt>int</tt> describing the flow control type.
     */
    private int stringToFlow(String flowcontrol) {
        flowcontrol = flowcontrol.toLowerCase();
        if (flowcontrol.equals("none")) {
            return SerialPort.FLOW_CONTROL_DISABLED;
        }
        if (flowcontrol.equals("xon/xoff out")) {
            return SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
        }
        if (flowcontrol.equals("xon/xoff in")) {
            return SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED;
        }
        if (flowcontrol.equals("rts/cts")) {
            return SerialPort.FLOW_CONTROL_CTS_ENABLED | SerialPort.FLOW_CONTROL_RTS_ENABLED;
        }
        if (flowcontrol.equals("dsr/dtr")) {
            return SerialPort.FLOW_CONTROL_DSR_ENABLED | SerialPort.FLOW_CONTROL_DTR_ENABLED;
        }
        return SerialPort.FLOW_CONTROL_DISABLED;
    }

    /**
     * Converts an <tt>int</tt> describing a flow control type to a
     * String describing a flow control type.
     *
     * @param flowcontrol the <tt>int</tt> describing the
     *                    flow control type.
     *
     * @return the <tt>String</tt> describing the flow control type.
     */
    private String flowToString(int flowcontrol) {
        switch (flowcontrol) {
            case SerialPort.FLOW_CONTROL_DISABLED:
                return "none";
            case SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED:
                return "xon/xoff out";
            case SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED:
                return "xon/xoff in";
            case SerialPort.FLOW_CONTROL_CTS_ENABLED:
                return "rts/cts";
            case SerialPort.FLOW_CONTROL_DTR_ENABLED:
                return "dsr/dtr";
            default:
                return "none";
        }
    }

}