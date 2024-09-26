var exec = require('cordova/exec');

exports.takeScreenshot = function(success, error) {
    exec(success, error, 'ScreenshotPlugin', 'takeScreenshot', []);
};

exports.connectPrinter = function(escPosCommands, success, error) {
    exec(success, error, 'ScreenshotPlugin', 'connectPrinter', [escPosCommands]);
};

module.exports = exports;
