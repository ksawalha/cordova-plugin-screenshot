var exec = require('cordova/exec');

var screenshot = {
    takeScreenshot: function (successCallback, errorCallback) {
        exec(successCallback, errorCallback, "ScreenshotPlugin", "takeScreenshot", []);
    }
};

module.exports = screenshot;
