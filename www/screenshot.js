var exec = require('cordova/exec');

var ScreenshotPlugin = {
    takeScreenshot: function(successCallback, errorCallback) {
        exec(successCallback, errorCallback, "ScreenshotPlugin", "takeScreenshot", []);
    }
};

// Register the plugin for OutSystems
if (typeof OS !== 'undefined') {
    OS.plugins = OS.plugins || {};
    OS.plugins.ScreenshotPlugin = ScreenshotPlugin;
}
