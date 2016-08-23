(function() {
    angular.module('main')
        .controller('ContentController', ['$scope', '$rootScope', '$localStorage', 'uiTourService', 'tourSteps', ContentController])
        .directive('systemManager', [SystemManagerDirective])
        .directive('systemBuilder', [SystemBuilderDirective]);

    function SystemBuilderDirective() {
        return {
            templateUrl: 'system-builder/system-builder.html',
            scope: {
                tour: '=tour'
            }
        };
    }

    function SystemManagerDirective() {
        return {
            templateUrl: 'system-manager/system-manager.html',
            scope: {
                tour: '=tour'
            }
        };
    }

    function ContentController($scope, $rootScope, $localStorage, TourService, tourSteps) {
        $scope.curr = 'systemBuilder';

        $scope.$on('Builder ID', function(data, id) {
            $scope.curr = 'systemBuilder';
            $scope.$broadcast('ID for Builder', id);
        });

        $scope.$on('refresh', function() {
            $scope.$broadcast('refreshManager');
        });

        $scope.$on('Unique names', function(data, names) {
            $scope.$broadcast('SystemNames', names);
        });

        $scope.$on('Active saved operator', function(data, operator) {
            $scope.$broadcast('Sending saved operator', operator);
        });

        $rootScope.changeView = function() {
            if ($scope.curr === 'systemManager') {
                $scope.curr = 'systemBuilder';
            } else {
                $scope.curr = 'systemManager';
            }
        };

        $rootScope.setStart = function() {
            $scope.curr = 'systemBuilder';
            $rootScope.inTour = true;
        };

        $scope.startDetached = function () {
            tourSteps.start();
            TourService.getTourByName('tour').on('ended', function () {
                console.log("Ending tour");
                $localStorage.toured = true;
            });
            $scope.curr = 'systemBuilder';
        };

        $scope.next = function() {
            tourSteps.next();
        };

        $scope.prev = function() {
            tourSteps.prev();
        };

        $scope.end = function() {
            tourSteps.end();
        };

        $rootScope.inTour = false;
    }
})();
