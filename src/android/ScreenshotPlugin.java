org.apache.cordova.screenshot;

import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.ByteArrayOutputStream;

public class ScreenshotPlugin extends CordovaPlugin {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("takeScreenshot".equals(action)) {
            takeScreenshot(callbackContext);
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
}
