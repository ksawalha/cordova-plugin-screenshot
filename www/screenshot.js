var exec = require('cordova/exec');

exports.takeScreenshot = function (successCallback, errorCallback) {
    exec(successCallback, errorCallback, "ScreenshotPlugin", "takeScreenshot", []);
};
