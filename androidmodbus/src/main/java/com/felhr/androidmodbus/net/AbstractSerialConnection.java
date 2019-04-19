package com.felhr.androidmodbus.net;

import com.felhr.androidmodbus.io.AbstractModbusTransport;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.IOException;
import java.util.Set;

/**
 * Interface that represents a public abstract serial port connection
 *
 * @author Felipe Herranz
 * @version 2.0 (March 2016)
 */
public abstract class AbstractSerialConnection {

    /**
     * Parity values
     */
    public static final int NO_PARITY = UsbSerialInterface.PARITY_NONE;
    public static final int ODD_PARITY = UsbSerialInterface.PARITY_ODD;
    public static final int EVEN_PARITY = UsbSerialInterface.PARITY_EVEN;
    public static final int MARK_PARITY = UsbSerialInterface.PARITY_MARK;
    public static final int SPACE_PARITY = UsbSerialInterface.PARITY_SPACE;

    /**
     * Stop bits values
     */
    public static final int ONE_STOP_BIT = UsbSerialInterface.STOP_BITS_1;
    public static final int ONE_POINT_FIVE_STOP_BITS = UsbSerialInterface.STOP_BITS_15;
    public static final int TWO_STOP_BITS = UsbSerialInterface.STOP_BITS_2;

    /**
     * Flow control values
     */
    public static final int FLOW_CONTROL_DISABLED = UsbSerialInterface.FLOW_CONTROL_OFF;
    public static final int FLOW_CONTROL_RTS_CTS_ENABLED = UsbSerialInterface.FLOW_CONTROL_RTS_CTS;
    public static final int FLOW_CONTROL_DSR_DTR_ENABLED = UsbSerialInterface.FLOW_CONTROL_DSR_DTR;
    public static final int FLOW_CONTROL_XONXOFF_ENABLED = UsbSerialInterface.FLOW_CONTROL_XON_XOFF;

    /**
     * Open delay (msec)
     */
    public static final int OPEN_DELAY = 0;

    /**
     * Timeout
     */
    static final public int TIMEOUT_NONBLOCKING = 0;
   	static final public int TIMEOUT_READ_SEMI_BLOCKING = 1;
   	static final public int TIMEOUT_READ_BLOCKING = 2;
   	static final public int TIMEOUT_WRITE_BLOCKING = 3;
   	static final public int TIMEOUT_SCANNER = 4;

    /**
     * Returns the <tt>ModbusTransport</tt> instance to be used for receiving
     * and sending messages.
     *
     * @throws IOException If the port is not available or cannot be opened
     */
    public abstract void open() throws IOException;

    /**
     * Returns the <tt>ModbusTransport</tt> instance to be used for receiving
     * and sending messages.
     *
     * @return a <tt>ModbusTransport</tt> instance.
     */
    public abstract AbstractModbusTransport getModbusTransport();

    /**
     * Read a specified number of bytes from the serial port
     *
     * @param buffer      Buffer to recieve bytes from the port
     * @param bytesToRead Number of bytes to read
     * @return number of currently bytes read.
     */
    public abstract int readBytes(byte[] buffer, long bytesToRead);

    /**
     * Write a specified number of bytes to the serial port
     *
     * @param buffer       Bytes to send to the port
     * @param bytesToWrite How many bytes to send
     * @return number of currently bytes written
     */
    public abstract int writeBytes(byte[] buffer, long bytesToWrite);

    /**
     * Bytes available to read
     *
     * @return number of bytes currently available to read
     */
    public abstract int bytesAvailable();

    /**
     * Sets the connection parameters to the setting in the parameters object.
     * If set fails return the parameters object to original settings and throw
     * exception.
     */
    public abstract void setConnectionParameters();

    /**
     * Close the port and clean up associated elements.
     */
    public abstract void close();

    /**
     * Returns current baud rate
     *
     * @return Baud rate
     */
    public abstract int getBaudRate();

    /**
     * Set new baud rate
     *
     * @param newBaudRate Baud rate
     */
    public abstract void setBaudRate(int newBaudRate);

    /**
     * Returns current data bits value
     *
     * @return Number of data bits
     */
    public abstract int getNumDataBits();

    /**
     * Returns current stop bits
     *
     * @return Number of stop bits
     */
    public abstract int getNumStopBits();

    /**
     * Returns current parity
     *
     * @return Parity type
     */
    public abstract int getParity();

    /**
     * Returns a descriptive name of the current port
     *
     * @return a <tt>String</tt> instance.
     */
    public abstract String getDescriptivePortName();

    /**
     * Set port timeouts
     *
     * @param newTimeoutMode  Timeout mode
     * @param newReadTimeout  Read timeout
     * @param newWriteTimeout Write timeout
     */
    public abstract void setComPortTimeouts(int newTimeoutMode, int newReadTimeout, int newWriteTimeout);

    /**
     * Reports the open status of the port.
     *
     * @return true if port is open, false if port is closed.
     */
    public abstract boolean isOpen();

    /**
     * Returns the timeout for this <tt>UDPMasterConnection</tt>.
     *
     * @return the timeout as <tt>int</tt>.
     */
    public abstract int getTimeout();

    /**
     * Sets the timeout for this <tt>UDPMasterConnection</tt>.
     *
     * @param timeout the timeout as <tt>int</tt>.
     */
    public abstract void setTimeout(int timeout);

    /**
     * Returns a set of all the available comm port names
     *
     * @return Set of comm port names
     */
    public abstract Set<String> getCommPorts();

}
