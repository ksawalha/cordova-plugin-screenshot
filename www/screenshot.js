var exec = require('cordova/exec');

var ScreenshotPlugin = {
    takeScreenshot: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, "ScreenshotPlugin", "takeScreenshot", []);
    }
};

// Register the plugin with the standard Cordova interface
if (typeof window !== 'undefined') {
    window.ScreenshotPlugin = ScreenshotPlugin;
}

module.exports = ScreenshotPlugin;
