var exec = require('cordova/exec');

exports.connectPrinter = function(escPosCommands, success, error) {
    exec(success, error, 'PrinterPlugin', 'connectPrinter', [escPosCommands]);
};

module.exports = exports;
