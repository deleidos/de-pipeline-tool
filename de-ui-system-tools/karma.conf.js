module.exports = function (config) {
    config.set({

        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '.',


        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine'],


        // list of files / patterns to load in the browser
        files: [
            // bower:js
            'app/bower_components/jquery/dist/jquery.js',
            'app/bower_components/angular/angular.js',
            'app/bower_components/jquery-ui/ui/jquery-ui.js',
            'app/bower_components/angular-ui-slider/src/slider.js',
            'app/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
            'app/bower_components/angular-animate/angular-animate.js',
            'app/bower_components/angular-aria/angular-aria.js',
            'app/bower_components/angular-messages/angular-messages.js',
            'app/bower_components/angular-material/angular-material.js',
            'app/bower_components/angular-route/angular-route.js',
            'app/bower_components/ngstorage/ngStorage.js',
            'app/bower_components/angular-drag-and-drop-lists/angular-drag-and-drop-lists.js',
            'app/bower_components/jsPlumb/dist/js/jsPlumb-2.0.7.js',
            'app/bower_components/ng-debounce/angular-debounce.js',
            'app/bower_components/angularjs-slider/dist/rzslider.js',
            'app/bower_components/angular-websocket/angular-websocket.min.js',
            'app/bower_components/chart.js/dist/Chart.js',
            'app/bower_components/angular-chart.js/dist/angular-chart.js',
            'app/bower_components/angular-hotkeys/build/hotkeys.js',
            'app/bower_components/ngSmoothScroll/lib/angular-smooth-scroll.js',
            'app/bower_components/angular-sanitize/angular-sanitize.js',
            'app/bower_components/ez-ng/dist/ez-ng.js',
            'app/bower_components/angular-ui-tour/dist/angular-ui-tour.js',
            'app/bower_components/Chart.js/dist/Chart.js',
            'app/bower_components/angular-smart-table/dist/smart-table.js',
            'app/bower_components/Split.js/split.js',
            'app/bower_components/angular-mocks/angular-mocks.js',
            // endbower
	        'app/bower_components/angular-websocket/angular-websocket-mock.js',

            'app/assets/lib/directives.js',

            'app/app.js',
            //'app/**/*.module.js',
            //'app/**/*.controller.js',
            //'app/**/*.service.js',
            //'app/**/*.directive.js',
	    //    'app/**/*.routes.js',
	    //    'app/**/*.run.js',

            //'app/**/*spec.js'
            'app/!(bower_components)/**/*.js'
        ],


        // list of files to exclude
        exclude: [
            'app/tests/*.js'
        ],


        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
            'app/!(bower_components)/**/!(*spec).js': 'coverage',
            'app/*.js': 'coverage'
        },

        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['progress', 'coverage'],

        coverageReporter: {
            dir: 'coverage/',
            subdir: 'report'
        },

        captureTimeout: 30000,

        // web server port
        port: 9876,


        // enable / disable colors in the output (reporters and logs)
        colors: true,


        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_INFO,


        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: false,


        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: [
            'Chrome',
            'Firefox',
            'IE',
            'PhantomJS'
        ],


        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: true,

        // Concurrency level
        // how many browser should be started simultaneous
        concurrency: Infinity


    });
};
