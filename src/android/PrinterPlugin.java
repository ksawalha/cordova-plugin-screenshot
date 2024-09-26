package org.apache.cordova.screenshot;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class PrinterPlugin extends CordovaPlugin {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("connectPrinter".equals(action)) {
            if (args.length() > 0) {
                String escPosCommands = args.getString(0);
                connectPrinter(escPosCommands, callbackContext);
                return true;
            } else {
                callbackContext.error("ESC/POS commands are required.");
                return false;
            }
        }
        return false;
    }

    private void connectPrinter(String escPosCommands, CallbackContext callbackContext) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            callbackContext.error("Bluetooth not supported.");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            cordova.startActivityForResult(this, enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        if (ActivityCompat.checkSelfPermission(cordova.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(cordova.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            callbackContext.error("Location permission required to discover Bluetooth devices.");
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                printToPrinter(device, escPosCommands, callbackContext);
                return;
            }
        } else {
            callbackContext.error("No paired Bluetooth devices found.");
        }
    }

    private void printToPrinter(BluetoothDevice device, String escPosCommands, CallbackContext callbackContext) {
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            socket.connect();
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(escPosCommands.getBytes());
            outputStream.flush();
            outputStream.close();
            socket.close();
            callbackContext.success("Printed successfully to " + device.getName());
        } catch (IOException e) {
            callbackContext.error("Failed to print: " + e.getMessage());
        }
    }
}
