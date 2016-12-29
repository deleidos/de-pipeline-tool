(function() {
    "use strict";

    angular.module('systemBuilder')
        .directive('operatorRibbon', ['$timeout', 'tourSteps', '$rootScope', operatorRibbon]);

    function operatorRibbon($timeout, tourSteps, $rootScope) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'system-builder/operatorRibbon.html',
            scope: {
                opList: '=opList',
                tour: '=tour'
            },
            link: function(scope) {
                scope.operatorLoading = true;
                $timeout(function() {
                    scope.next = function() {
                        tourSteps.next();
                    };

                    scope.prev = function() {
                        tourSteps.prev();
                    };

                    scope.end = function() {
                        tourSteps.end();
                    };

                    var typeColors = [
                        '#ffffff',
                        '#bb0773',
                        '#9b2743',
                        '#774135',
                        '#8c7732',
                        '#046a38',
                        '#00778b',
                        '#80479b',
                        '#ffffff'
                    ];

                    function sortOps(a, b) {
                        var aLeast = 99;
                        var bLeast = 99;
                        if (a.type === 'input') {
                            aLeast = 0;
                        } else if (a.type === 'binaryInput') {
                            aLeast = 1;
                        } else if (a.type === 'parser') {
                            aLeast = 2;
                        } else if (a.type === 'mapping') {
                            aLeast = 3;
                        } else if (a.type === 'enrichment') {
                            aLeast = 4;
                        } else if (a.type === 'output') {
                            aLeast = 5;
                        } else if (a.type === 'binaryOutput') {
                            aLeast = 6;
                        }

                        if (b.type === 'input') {
                            bLeast = 0;
                        } else if (b.type === 'binaryInput') {
                            bLeast = 1;
                        } else if (b.type === 'parser') {
                            bLeast = 2;
                        } else if (b.type === 'mapping') {
                            bLeast = 3;
                        } else if (b.type === 'enrichment') {
                            bLeast = 4;
                        } else if (b.type === 'output') {
                            bLeast = 5;
                        } else if (b.type === 'binaryOutput') {
                            bLeast = 6;
                        }

                        return aLeast - bLeast;
                    }

                    scope.setActiveCat = function(opType) {
                        if (opType) {
                            scope.activeCat = opType;
                            $('md-ink-bar').css('background-color', opType.color);
                            scope.listBorderColor = opType.color;
                        }
                    };

                    $rootScope.refreshOperators = function(opList) {
                        if (opList) {
                            opList.forEach(function(category, index) {
                                if (category.name !== 'All' && category.name !== 'Stored Operators') {
                                    var color = typeColors[index];
                                    var icon = 'fa-circle';
                                    angular.extend(category, {
                                        color: color,
                                        icon: icon,
                                        index: index
                                    });
                                    category.operators.forEach(function (operator) {
                                        angular.extend(operator, {
                                            color: color
                                        });
                                    });
                                }
                            });

                            opList[0].operators.sort(sortOps);

                            scope.displayList = opList;
                            if (scope.displayList) {
                                scope.setActiveCat(scope.displayList[0]);
                            }

                            scope.$apply();

                        }
                    };
                    $rootScope.refreshOperators(scope.opList);



                    scope.focusSearch = function() {
                        $("#operator-search").focus();
                    };

                    scope.setActive = function(operator) {
                        console.log(operator);
                        scope.$emit('Active saved operator', operator);
                    };

                    scope.$watch('search', function(searchStr) {
                        if (!searchStr || !searchStr.display_name || searchStr.display_name === '') {
                            // this is so that categories with no operators will still display when the search string is empty
                            scope.displayList = scope.opList;
                        } else if (scope.opList) {
                            scope.displayList = scope.opList.filter(function(opCat) {
                                return opCat.operators.some(function(operator) {
                                    return operator.displayName.toLowerCase().indexOf(searchStr.display_name.toLowerCase()) > -1;
                                });
                            });
                        }
                        if (scope.displayList) {
	                        scope.setActiveCat(scope.displayList[0]);
                        }
                    });

                    scope.operatorLoading = false;
                }, 4000);
            }
        };
    }

})();
