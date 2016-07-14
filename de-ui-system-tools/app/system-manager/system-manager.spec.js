describe('SystemManagerController', function () {
	var testScope, testSocket;
		beforeEach(function () {
		module('main', function ($provide) {
			$provide.value('$location', {
				host: function () {
					return 'http://localhost:63342/system-builder/app/index.html#/system-builder';
				}
			});
		});
		module('systemManager');
		angular.mock.module('ngWebSocket', 'ngWebSocketMock');
		inject(function ($controller, $rootScope, $websocketBackend) {
			testScope = $rootScope;
			testSocket = $websocketBackend;
			$controller('SystemBuilderController', {
				$scope: testScope
			});
		});
	});

});

