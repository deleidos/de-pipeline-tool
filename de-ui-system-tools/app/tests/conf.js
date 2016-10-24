// An example configuration file.
//module.exports = function (config) {
//    config.set({
exports.config = {
    directConnect: true,
    chromeOnly: true,
    //chromeDriver: '/home/ubuntu/workspace/Build_Apex_UI/de-ui-system-tools/node_modules/protractor/selenium/chromedriver',

    // Capabilities to be passed to the webdriver instance.
    capabilities: {
        browserName: 'chrome'
    },

    // Framework to use. Jasmine is recommended.
    framework: 'jasmine',

    // Spec patterns are relative to the current working directory when
    // protractor is called.
    specs: ['test.js'],

    // Options to be passed to Jasmine.
    jasmineNodeOpts: {
        defaultTimeoutInterval: 30000
    },
    rootElement: '[ng-app]'
//    });
};
