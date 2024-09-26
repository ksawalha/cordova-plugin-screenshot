package org.apache.cordova.screenshot;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import androidx.core.app.ActivityCompat;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class ScreenshotPlugin extends CordovaPlugin {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("takeScreenshot".equals(action)) {
            takeScreenshot(callbackContext);
            return true;
        } else if ("connectPrinter".equals(action)) {
            String escPosCommands = args.getString(0);
            connectPrinter(escPosCommands, callbackContext);
            return true;
        }
        return false;
    }

   private void takeScreenshot(CallbackContext callbackContext) {
        Activity activity = this.cordova.getActivity();
        Window window = activity.getWindow();
        View view = window.getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);
        callbackContext.success(base64Image);
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
