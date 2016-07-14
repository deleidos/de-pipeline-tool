(function() {
    "use strict";

    angular.module('main')
        .config(['$routeProvider', '$mdThemingProvider', config]);

    function config($routeProvider, $mdThemingProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'contents.html',
                controller: 'ContentController'
            })
            .otherwise({
                redirectTo: '/'
            });
	    $mdThemingProvider.theme('default')
		    .primaryPalette('grey')
		    .accentPalette('blue');
    }
})();
