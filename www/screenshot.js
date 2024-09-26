var exec = require('cordova/exec');

exports.takeScreenshot = function(success, error) {
    exec(success, error, 'ScreenshotPlugin', 'takeScreenshot', []);
};

module.exports = exports;
