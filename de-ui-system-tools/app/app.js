(function() {
    "use strict";

    angular.module('main', [
        'systemBuilder',
        'systemManager',
        'ngMaterial',
	    'plumbApp.directives'
    ]).run(['$rootScope', '$location', '$http', function($rootScope, $location, $http) {
        if ($location.host() === 'test') {
            $rootScope.dataService = 'test';
        } else {
            var promise = $http.get('config.json');
            promise.then(function (data) {
                $rootScope.dataService = data.data.hostname;
            });
        }
    }]);

    function dropDisable () {
        return {
            restrict: 'A',
            link: function (scope, element) {
                var handler = function (event) {
                    event.preventDefault();
                    return false;
                };
                element.on('dragenter', handler);
                element.on('dragover', handler);
                element.on('drop', handler);
            }
        };
    }

    angular.module('systemBuilder', [
        'ngRoute',
        'ngStorage',
        'dndLists',
        'plumbApp.directives',
        'ngMaterial',
        'debounce',
        'angular-websocket',
        'ui.bootstrap'
    ])
    .directive('dropDisable', dropDisable);

    angular.module('systemManager', [
        'ngRoute',
        'ngMaterial',
        'angular-websocket',
        'ui.bootstrap',
        'chart.js'
    ]);

})();
