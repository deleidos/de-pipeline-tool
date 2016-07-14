(function() {
    angular.module('main')
        .controller('ContentController', ['$scope', ContentController])
        .directive('systemManager', [SystemManagerDirective])
        .directive('systemBuilder', [SystemBuilderDirective]);

    function SystemBuilderDirective() {
        return {
            templateUrl: 'system-builder/system-builder.html'
        };
    }

    function SystemManagerDirective() {
        return {
            templateUrl: 'system-manager/system-manager.html'
        };
    }

    function ContentController($scope) {
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
    }
})();
