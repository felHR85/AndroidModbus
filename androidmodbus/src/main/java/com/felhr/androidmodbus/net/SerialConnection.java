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
package com.felhr.androidmodbus.net;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.util.Log;

import com.felhr.androidmodbus.Modbus;
import com.felhr.androidmodbus.io.AbstractModbusTransport;
import com.felhr.androidmodbus.io.ModbusASCIITransport;
import com.felhr.androidmodbus.io.ModbusRTUTransport;
import com.felhr.androidmodbus.io.ModbusSerialTransport;
import com.felhr.androidmodbus.util.SerialParameters;
import com.felhr.usbserial.SerialInputStream;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.utils.HexData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class that implements a serial connection which can be used for master and
 * slave implementations.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public class SerialConnection extends AbstractSerialConnection {

    private static final Logger logger = LoggerFactory.getLogger(SerialConnection.class);

    private SerialParameters parameters;
    private ModbusSerialTransport transport;
    private SerialInputStream inputStream;
    private int timeout = Modbus.DEFAULT_TIMEOUT;

    private UsbSerialDevice serialDevice;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection mUsbDeviceConnection;


    /**
     * Default constructor
     */
    public SerialConnection() {

    }

    /**
     * Creates a SerialConnection object and initializes variables passed in as
     * params.
     *
     * @param parameters A SerialParameters object.
     */
    public SerialConnection(UsbDevice device, UsbDeviceConnection usbDeviceConnection, SerialParameters parameters) {
        this.parameters = parameters;
        this.mUsbDevice = device;
        this.mUsbDeviceConnection = usbDeviceConnection;
    }


    @Override
    public AbstractModbusTransport getModbusTransport() {
        return transport;
    }

    @Override
    public void open() throws IOException {
        if (serialDevice == null) {
            serialDevice = UsbSerialDevice.createUsbSerialDevice(mUsbDevice, mUsbDeviceConnection);
            if (serialDevice == null) {
                throw new IOException(String.format("Usb serial port could not be opened"));
            }
        }

        setConnectionParameters();

        if (Modbus.SERIAL_ENCODING_ASCII.equals(parameters.getEncoding())) {
            transport = new ModbusASCIITransport();
        }
        else if (Modbus.SERIAL_ENCODING_RTU.equals(parameters.getEncoding())) {
            transport = new ModbusRTUTransport();
        }
        else {
            transport = new ModbusRTUTransport();
            logger.warn("Unknown transport encoding [{}] - reverting to RTU", parameters.getEncoding());
        }
        transport.setEcho(parameters.isEcho());
        transport.setTimeout(timeout);

        // Open the input and output streams for the connection. If they won't
        // open, close the port before throwing an exception.
        transport.setCommPort(this);

        // Open the port so that we can get it's input stream.
        if (serialDevice.syncOpen()) {
            serialDevice.setBaudRate(parameters.getBaudRate());
            serialDevice.setDataBits(parameters.getDatabits());
            serialDevice.setStopBits(parameters.getStopbits());
            serialDevice.setParity(parameters.getParity());
        }else {
            close();
            throw new IOException("Port could not be opened");
        }
        inputStream = serialDevice.getInputStream();
        inputStream.setTimeout(timeout);
    }

    @Override
    public void setConnectionParameters() {
        // Set connection parameters, if set fails return parameters object
        // to original state
    }

    @Override
    public void close() {
        // Check to make sure serial port has reference to avoid a NPE

        if (serialDevice != null) {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            catch (IOException e) {
                logger.debug(e.getMessage());
            }
            finally {
                // Close the port.
                serialDevice.syncOpen();
            }
        }
        serialDevice = null;
    }

    @Override
    public boolean isOpen() {
        return serialDevice != null && serialDevice.isOpen();
    }

    @Override
    public synchronized int getTimeout() {
        return timeout;
    }

    @Override
    public synchronized void setTimeout(int timeout) {
        this.timeout = timeout;
        if (transport != null) {
            transport.setTimeout(timeout);
        }
    }

    @Override
    public int readBytes(byte[] buffer, long bytesToRead) {
        for(int i=0;i<=bytesToRead-1;i++) {
            int value = inputStream.read();
            if(value != -1) {
                buffer[i] = (byte) inputStream.read();
            }else{
                return i;
            }
        }
        return (int) bytesToRead;
    }

    @Override
    public int writeBytes(byte[] buffer, long bytesToWrite) {
        return serialDevice.syncWrite(buffer, timeout);
    }

    @Override
    public int bytesAvailable() {
        try {
            return inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getBaudRate() {
        return parameters.getBaudRate();
    }

    @Override
    public void setBaudRate(int newBaudRate) {
        serialDevice.setBaudRate(newBaudRate);
    }

    @Override
    public int getNumDataBits() {
        return parameters.getDatabits();
    }

    @Override
    public int getNumStopBits() {
        return parameters.getStopbits();
    }

    @Override
    public int getParity() {
        return parameters.getParity();
    }

    @Override
    public String getDescriptivePortName() {
       return serialDevice.getPortName();
    }

    @Override
    public void setComPortTimeouts(int newTimeoutMode, int newReadTimeout, int newWriteTimeout) {
        //Let's see...
    }

    @Override
    public Set<String> getCommPorts() {
        Set<String> returnValue = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        return returnValue;
    }
}
