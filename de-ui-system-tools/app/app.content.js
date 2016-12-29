(function() {
    angular.module('main')
        .controller('ContentController', ['$scope', '$rootScope', '$localStorage', 'uiTourService', 'tourSteps', ContentController])
        .directive('systemManager', [SystemManagerDirective])
        .directive('systemBuilder', [SystemBuilderDirective])
        .directive('operatorLibrary', [OperatorLibraryDirective]);

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

    function OperatorLibraryDirective() {
        return {
            templateUrl: 'operator-library/operator-library.html',
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

        $scope.$on('connection made', function() {
            $scope.$broadcast('Sending connection notification');
        });

        $scope.$on('Sending online systems',function(data, systems) {
            $scope.$broadcast('Receiving online systems', systems);
        });
        $scope.$on('About to send operators', function(data, operators) {
           $scope.$broadcast('Sending operators', operators);
        });
        $scope.$on('Update metadata', function() {
            $scope.$broadcast('Updating metadata');
        });


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

        $scope.launchHelp = function() {
            var childWindowForHelp = window.open('assets/helpDocs/Default.htm', "", "width=950,height=850");
            childWindowForHelp.moveTo(300, 50);
        }; // launchHelp


        $rootScope.changeView = function(view) {
            $scope.curr = view;
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
