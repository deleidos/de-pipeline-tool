(function() {
    "use strict";

    angular.module('systemBuilder')
        .directive('operatorRibbon', ['$timeout', operatorRibbon]);

    function operatorRibbon($timeout) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'system-builder/operatorRibbon.html',
            scope: {
                opList: '=opList'
            },
            link: function(scope) {
                scope.operatorLoading = true;
                $timeout(function() {
                    scope.operatorLoading = false;
                    var typeColors = [
                        '#63a0d4',
                        '#b77033',
                        '#b7b233',
                        '#b73338',
                        '#33b7b2',
	                    '#855093'
                    ];

                    if (scope.opList) {
                        scope.opList.forEach(function(category, index) {
                            var color = typeColors[index];
                            var icon = 'fa-circle';
                            angular.extend(category, {
                                color: color,
                                icon: icon,
                                index: index
                            });
                            if (category.name !== 'All') {
                                category.operators.forEach(function(operator) {
                                    angular.extend(operator, {
                                        color: color
                                    });
                                });
                            }
                        });
                    }

                    scope.setActiveCat = function(opType) {
                        if (opType) {
                            scope.activeCat = opType;
                            $('md-ink-bar').css('background-color', opType.color);
                            scope.listBorderColor = opType.color;
                        }
                    };

                    scope.displayList = scope.opList;
                    if (scope.displayList) {
	                    scope.setActiveCat(scope.displayList[0]);
                    }

                    scope.addOperator = function() {
                        console.log('addOperator');
                    };

                    scope.focusSearch = function() {
                        $("#operator-search").focus();
                    };

                    scope.$watch('search', function(searchStr) {
                        if (!searchStr || !searchStr.display_name || searchStr.display_name === '') {
                            // this is so that categories with no operators will still display when the search string is empty
                            scope.displayList = scope.opList;
                        } else if (scope.opList) {
                            scope.displayList = scope.opList.filter(function(opCat) {
                                return opCat.operators.some(function(operator) {
                                    return operator.display_name.toLowerCase().indexOf(searchStr.display_name.toLowerCase()) > -1;
                                });
                            });
                        }
                        if (scope.displayList) {
	                        scope.setActiveCat(scope.displayList[0]);
                        }
                    });
                }, 2000);
            }
        };
    }

})();
