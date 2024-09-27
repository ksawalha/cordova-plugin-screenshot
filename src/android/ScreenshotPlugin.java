package org.apache.cordova.screenshot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.widget.ScrollView;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;

public class ScreenshotPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("takeScreenshot".equals(action)) {
            cordova.getActivity().runOnUiThread(() -> takeScreenshot(callbackContext));
            return true;
        }
        return false;
    }

    private void takeScreenshot(CallbackContext callbackContext) {
        Activity activity = cordova.getActivity();
        View rootView = activity.getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap bitmap = getFullScrollViewBitmap(rootView);
        rootView.setDrawingCacheEnabled(false);

        if (bitmap != null) {
            String base64Image = convertBitmapToBase64(bitmap);
            callbackContext.success(base64Image);
        } else {
            callbackContext.error("Failed to capture screenshot.");
        }
    }

    private Bitmap getFullScrollViewBitmap(View rootView) {
        int totalHeight = rootView.getHeight();
        int totalWidth = rootView.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        rootView.draw(canvas);
        return bitmap;
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }
}
