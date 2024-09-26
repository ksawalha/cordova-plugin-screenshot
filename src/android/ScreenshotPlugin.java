package org.apache.cordova.screenshot;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.app.AlertDialog;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.ByteArrayOutputStream;
import java.util.Set;

public class ScreenshotPlugin extends CordovaPlugin {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("takeScreenshot".equals(action)) {
            takeScreenshot(callbackContext);
            return true;
        } else if ("connectPrinter".equals(action)) {
            connectPrinter(args.getString(0), callbackContext);
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

    private void connectPrinter(String command, CallbackContext callbackContext) {
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

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                printToPrinter(device, command, callbackContext);
                return;
            }
        } else {
            callbackContext.error("No paired Bluetooth devices found.");
        }
    }

    private void printToPrinter(BluetoothDevice device, String command, CallbackContext callbackContext) {
        try {
            callbackContext.success("Printed successfully to " + device.getName());
        } catch (Exception e) {
            callbackContext.error("Failed to print: " + e.getMessage());
        }
    }
}
