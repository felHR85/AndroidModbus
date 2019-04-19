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
package com.felhr.androidmodbus.facade;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import com.felhr.androidmodbus.Modbus;
import com.felhr.androidmodbus.io.AbstractModbusTransport;
import com.felhr.androidmodbus.io.ModbusSerialTransaction;
import com.felhr.androidmodbus.net.AbstractSerialConnection;
import com.felhr.androidmodbus.net.SerialConnection;
import com.felhr.androidmodbus.util.SerialParameters;
import com.felhr.usbserial.UsbSerialDevice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Modbus/Serial Master facade.
 *
 * @author Dieter Wimberger
 * @author John Charlton
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public class ModbusSerialMaster extends AbstractModbusMaster {

    private static final Logger logger = LoggerFactory.getLogger(ModbusSerialMaster.class);
    private AbstractSerialConnection mConnection;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection mUsbDeviceConnection;
    private int transDelay = Modbus.DEFAULT_TRANSMIT_DELAY;

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param param SerialParameters specifies the serial port parameters to use
     *              to communicate with the slave device network.
     */
    public ModbusSerialMaster(UsbDevice device, UsbDeviceConnection usbDeviceConnection, SerialParameters param) {
        this(device, usbDeviceConnection, param, Modbus.DEFAULT_TIMEOUT, Modbus.DEFAULT_TRANSMIT_DELAY);
    }

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param param   SerialParameters specifies the serial port parameters to use
     *                to communicate with the slave device network.
     * @param timeout Receive timeout in milliseconds
     */
    public ModbusSerialMaster(UsbDevice device, UsbDeviceConnection usbDeviceConnection, SerialParameters param, int timeout) {
        this(device, usbDeviceConnection, param, timeout, Modbus.DEFAULT_TRANSMIT_DELAY);
    }

    /**
     * Constructs a new master facade instance for communication
     * with a given slave.
     *
     * @param param      SerialParameters specifies the serial port parameters to use
     *                   to communicate with the slave device network.
     * @param timeout    Receive timeout in milliseconds
     * @param transDelay The transmission delay to use between frames (milliseconds)
     */
    public ModbusSerialMaster(UsbDevice device, UsbDeviceConnection usbDeviceConnection, SerialParameters param, int timeout, int transDelay) {
        try {
            this.transDelay = transDelay > -1 ? transDelay : Modbus.DEFAULT_TRANSMIT_DELAY;
            mConnection = new SerialConnection(device, usbDeviceConnection, param);
            mConnection.setTimeout(timeout);
            this.mUsbDevice = device;
            this.mUsbDeviceConnection = usbDeviceConnection;
            this.timeout = timeout;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public AbstractSerialConnection getConnection() {
        return mConnection;
    }

    public static boolean isUsbSupported(UsbDevice usbDevice) {
        return UsbSerialDevice.isSupported(usbDevice);
    }

    /**
     * Connects this <tt>ModbusSerialMaster</tt> with the slave.
     *
     */
    public void connect() throws Exception {
        if (mConnection != null && !mConnection.isOpen()) {
            mConnection.open();
            transaction = mConnection.getModbusTransport().createTransaction();
            ((ModbusSerialTransaction) transaction).setTransDelayMS(transDelay);
            setTransaction(transaction);
        }
    }

    /**
     * Disconnects this <tt>ModbusSerialMaster</tt> from the slave.
     */
    public void disconnect() {
        if (mConnection != null && mConnection.isOpen()) {
            mConnection.close();
            transaction = null;
            setTransaction(null);
        }
    }

    @Override
    public void setTimeout(int timeout) {
        super.setTimeout(timeout);
        if (mConnection != null) {
            mConnection.setTimeout(timeout);
        }
    }

    @Override
    public AbstractModbusTransport getTransport() {
        return mConnection == null ? null : mConnection.getModbusTransport();
    }
}