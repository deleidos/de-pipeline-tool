(function() {
    "use strict";

    angular.module('main', [
        'systemBuilder',
        'systemManager',
        'ngMaterial',
	    'plumbApp.directives',
        'bm.uiTour'
    ])
        .run(['$rootScope', '$location', '$http', function($rootScope, $location, $http) {
        if ($location.host() === 'test') {
            $rootScope.dataService = 'test';
        } else {
            var promise = $http.get('config.json');
            promise.then(function (data) {
                $rootScope.dataService = data.data.hostname;
            });
        }
    }])
        .config(['TourConfigProvider', function (TourConfigProvider) {

            TourConfigProvider.set('scrollOffset', 50);

            TourConfigProvider.set('onStart', function () {
                console.log('Started Tour main');
            });

            TourConfigProvider.set('onNext', function () {
                console.log('Moving on...');
            });

            TourConfigProvider.set('templateUrl', 'tour/tour-template.html');

        }])
        .run(['uiTourService', function (TourService) {
            TourService.createDetachedTour('tour');
        }]).factory('tourSteps', ['$rootScope', 'uiTourService', tourStepsFactory]);

    function tourStepsFactory ($rootScope, TourService) {
        return {
            next: function() {
                console.log("test");
                TourService.getTourByName('tour').next();
                console.log(TourService.getTourByName('tour').getCurrentStep().order);
                /*if (TourService.getTourByName('tour').getCurrentStep().order == 30) {
                    console.log("change view")
                    $rootScope.changeView();
                }*/

                if (TourService.getTourByName('tour').getCurrentStep().order === 40) {
                    $rootScope.setTempOperator();
                }
                if (TourService.getTourByName('tour').getCurrentStep().order === 50) {
                    $rootScope.removeTempOperator();
                }
                if (TourService.getTourByName('tour').getCurrentStep().order === 110) {
                    $rootScope.changeView();
                }
                if (TourService.getTourByName('tour').getCurrentStep().order === 140) {
                    $rootScope.changeView();
                }

                console.log("next");
            },

            prev: function () {
                console.log("prev");
                TourService.getTourByName('tour').prev();
                console.log(TourService.getTourByName('tour').getCurrentStep().order);

                if (TourService.getTourByName('tour').getCurrentStep().order === 50) {
                    $rootScope.removeTempOperator();
                }
                if (TourService.getTourByName('tour').getCurrentStep().order === 60) {
                    $rootScope.setTempOperator();
                }
                if (TourService.getTourByName('tour').getCurrentStep().order === 120) {
                    $rootScope.changeView();
                }
                if (TourService.getTourByName('tour').getCurrentStep().order === 150) {
                    $rootScope.changeView();
                }
            },

            end: function () {
                console.log("end");
                TourService.getTourByName('tour').end();
                $rootScope.inTour = false;
            },

            start: function () {
                $rootScope.setStart();
                $rootScope.removeTempOperator();
                TourService.getTourByName('tour').start();
                console.log(TourService.getTourByName('tour'));
            }
        };
    }

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
        'ui.bootstrap',
        'cfp.hotkeys',
        'bm.uiTour'
    ])/*.config(['TourConfigProvider', function (TourConfigProvider) {

            TourConfigProvider.set('scrollOffset', 50);

            TourConfigProvider.set('onStart', function () {
                console.log('Started Tour');
            });
            TourConfigProvider.set('onNext', function () {
                console.log('Moving on...');
            });
        }])
        .run(['uiTourService', function (TourService) {
            TourService.createDetachedTour('detachedDemoTour');
            console.log(TourService.getTourByName('detachedDemoTour'));
        }])*/
    .directive('dropDisable', [dropDisable])
        .factory('tourSteps', ['$rootScope', 'uiTourService', tourStepsFactory])
        .directive('elemReady', ['$parse', function($parse) {
            return {
                restrict: 'A',
                link: function($scope, elem, attrs) {
                    elem.ready(function() {
                        $scope.$apply(function() {
                            var func = $parse(attrs.elemReady);
                            func($scope);
                        });
                    });
                }
            };
        }]);

    angular.module('systemManager', [
        'ngRoute',
        'ngMaterial',
        'angular-websocket',
        'ui.bootstrap',
        'chart.js',
        'bm.uiTour'
    ]).factory('tourSteps', ['$rootScope', 'uiTourService', tourStepsFactory]);
})();
