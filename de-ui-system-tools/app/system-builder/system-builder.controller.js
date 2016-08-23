(function () {
    "use strict";

    angular.module('systemBuilder')
        .controller('SystemBuilderController', ['$scope', '$window', '$localStorage', '$timeout', '$rootScope', '$websocket', '$uibModal', 'hotkeys', 'uiTourService', 'tourSteps', SystemBuilderController])
        .controller('ToolTip', ['$scope', '$uibModalInstance', ToolTip]);



    function ToolTip($scope, $uibModalInstance) {
        $scope.ok = function() {
            $uibModalInstance.close();
        };
    }

    function SystemBuilderController($scope, $window, $localStorage, $timeout, $rootScope, $websocket, $uibModal, hotkeys, TourService, tourSteps) {

	    var dataStream = $websocket($rootScope.dataService);
        var getStream = $websocket($rootScope.dataService);
        var saveStream = $websocket($rootScope.dataService);

        $scope.buildInvalid = true;
        $scope.updating = false;
        $scope.systemName = '';
        $scope.systemNameRegEx = new RegExp("^[A-Za-z0-9-_]+$");
        $scope.systemNamesIds = {};
        $scope.activeState = null;
        $scope.loading = true;
        $scope._id = $localStorage._id;        //add $location parameter to start with id
        $scope.stateConnections = [];
        if (!$scope.canvas) {
            $scope.canvas = {
                zoomlevel: 30,
                x: 100,
                y: 100
            };
        }
        $scope.stateObjects = [];
        $scope.targetEndpointStyle = {
          endpoint: "Dot",
          cssClass: 'ep-target',
          isSource: true,
          isTarget: true,
          maxConnections: -1,
          connector: ["Bezier", {
              curviness: 150
          }],
          connectorStyle: {
              lineWidth: 4,
              strokeStyle: "#61B7CF",
              joinstyle: "round",
              outlineColor: "white",
              outlineWidth: 2
          }
        };
        $scope.sourceEndpointStyle = {
            endpoint: "Dot",
            cssClass: 'ep-src',
            isSource: true,
            isTarget: true,
            maxConnections: -1,
            connector: ["Bezier", {
                curviness: 150
            }],
            connectorStyle: {
                lineWidth: 4,
                strokeStyle: "#61B7CF",
                joinstyle: "round",
                outlineColor: "white",
                outlineWidth: 2
            }
        };
        $scope.jsonConfig = {
            "request": "saveSystemDescriptor",
            "system_descriptor": {
                "name": $scope.systemName,
                "application": {
                    "description": "Example Application",
                    "operators": [],
                    "streams": []
                },
                "mappings": {}
            }
        };
        $scope.refreshing = false;
        $scope.invalid = [];
        $scope.invalid_text = [];
        $scope.hideErrors = true;
        if (!$rootScope.mode) {
            $rootScope.mode = 'select';
        }
        $scope.mode = $rootScope.mode;
        $scope.searchOp = {display_name: ''};
        $scope.canvasRefresh = false;
        $scope.showModes = false;
        $scope.mousePos = {};
        $scope.oldZoom = 30;

        $scope.$on('SystemNames', function(data, names) {
            $scope.systemNamesIds = names;
            $scope.$apply();
        });

        saveStream.onMessage(function(message) {
            console.log(JSON.parse(message.data));
            $scope._id = JSON.parse(message.data)._id;
            $localStorage._id = $scope._id;
            $scope.$emit('refresh');
        });
        dataStream.send({
            "request": "getOperatorMetadata"
        });
        dataStream.onMessage(function(message) {
            if (message.data) {
                var data = JSON.parse(message.data);

                $scope.operatorList = [
                    {
                        name: 'All',
                        operators: data
                    }, {
                        name: 'Input',
                        matchName: 'input',
                        operators: []
                    }, {
                        name: 'Parsers',
                        matchName: 'parser',
                        operators: []
                    }, {
                        name: 'Mappings',
                        matchName: 'mapping',
                        operators: []
                    }, {
                        name: 'Enrichments',
                        matchName: 'enrichment',
                        operators: []
                    }, {
                        name: 'Output',
                        matchName: 'output',
                        operators: []
                    }];
                if ($localStorage.savedOperators) {
                    $scope.operatorList.push({
                        name: 'Saved Operators',
                        operators: $localStorage.savedOperators
                    });
                } else {
                    $scope.operatorList.push({
                        name: 'Saved Operators',
                        operators: []
                    });
                }

                //Parses OP list based off of operator type
                var deIndex = -1;
                var nsIndex = -1;
                var opIndex = -1;
                angular.forEach(data, function (op) {
                    for (var i = 0; i < $scope.operatorList.length; i++) {
                        if ($scope.operatorList[i].name !== 'All' && $scope.operatorList[i].name !== 'Saved Operators' && op.class_name.toUpperCase().indexOf($scope.operatorList[i].matchName.toUpperCase()) > -1) {
                            op.classType = $scope.operatorList[i].matchName;
                            //Checks for StringList and turns their value into an array if so
                            if (op.properties !== undefined && op.properties !== null) {
                                for (var j = 0; j < op.properties.length; j++) {
                                    if (op.properties[j].type === 'StringList') {
                                        op.properties[j].value = [''];
                                    } else if ($scope.operatorList[i].name === 'Enrichments') {
                                        if (op.properties[j].name === 'namespace') {
                                            deIndex = i;
                                            nsIndex = j;
                                            opIndex = $scope.operatorList[i].operators.length;
                                        }
                                    }
                                }
                            }
                            $scope.operatorList[i].operators.push(op);
                            i = $scope.operatorList.length;
                        }
                    }
                });

                if (deIndex > -1 && nsIndex > -1 && opIndex > -1) {
                    var enrichmentSocket = $websocket($rootScope.dataService);
                    enrichmentSocket.onMessage(function (message) {
                        $scope.operatorList[deIndex].operators[opIndex].properties[nsIndex].choices = JSON.parse(message.data);
                    });
                    enrichmentSocket.send(JSON.stringify({"request": "getRedisDimensionalEnrichmentNamespaces"}));
                }

                console.log($scope.operatorList);
                $scope.$apply();
                $scope.loading = false;
            }
        });

        $scope.helpModal = function() {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'system-builder/tool-tip.html',
                controller: 'ToolTip',
                size: 'sm',
                resolve: {}
            });

            modalInstance.result.then(function() {
            }, function() {
            });
        };

        $scope.resetPosition = function() {
          $timeout(function() {
            var ds = function(x) {
	            return x * $scope.canvas.zoomlevel / 100;
            };
            var cx = $(".panel-primary")[0].offsetWidth / 2;
            var cy = $(".panel-primary")[0].offsetHeight / 2;
              var ax = ds($scope.stateObjects.reduce(
                      function(s, x, i, a) {
                          return s + x.x / a.length;
                      }, 0)) + $("js-plumb-canvas").position().left;
              var ay = ds($scope.stateObjects.reduce(
                      function(s, x, i, a) {
                          return s + x.y / a.length;
                      }, 0)) + $("js-plumb-canvas").position().top;
            var xOffset = ax - cx;
            var yOffset = ay - cy;

            $("js-plumb-canvas").offset(function(i, coords) {
                if ($scope.stateObjects.length > 0) {
                    $scope.canvas.x = coords.left - xOffset;
                    $scope.canvas.y = coords.top - yOffset - 180;
                    return {
                        left: coords.left - xOffset,
                        top: coords.top - yOffset
                    };
                } else {
                    $scope.canvas.x = coords.left - xOffset - $("js-plumb-canvas").width() / 2;
                    $scope.canvas.y = coords.top - yOffset - $("js-plumb-canvas").height() / 2 - 180;
                    return {
                        left: coords.left - xOffset - $("js-plumb-canvas").width() / 2,
                        top: coords.top - yOffset - $("js-plumb-canvas").height() / 2
                    };
                }
            });
              $scope.oldZoom = $scope.canvas.zoomlevel;
              $scope.update();
          }, 100);
        };

        $scope.$on('ID for Builder', function(data, id) {
            $scope._id = id;
            getStream.send({
                "request": "getSystemDescriptor",
                "id": $scope._id
            });
        });

        getStream.onMessage(function(message) {
            $scope.jsonConfig = JSON.parse(message.data);
	        if ($scope.jsonConfig.state) {
		        $scope.stateObjects = JSON.parse($scope.jsonConfig.state);
                $scope.systemName = $scope.jsonConfig.name;
                $scope.resetPosition();
	        } else {
		        console.log("No State Present");
	        }
        });

        $scope.dragWorkaround = function() {
            $timeout(function() {
                $scope.canvas.zoomlevel = $scope.canvas.zoomlevel - 1;
                $scope.$apply();
                $timeout(function() {
                    $scope.canvas.zoomlevel = $scope.canvas.zoomlevel + 1;
                    $scope.$apply();
                }, 10);
            }, 100);
        };

        if ($scope._id !== undefined && $scope._id !== null) {
            $scope.jsonConfig.system_descriptor._id = $scope._id;
        }

        $scope.$watch('canvas.zoomlevel', function() {
            $localStorage.canvas = $scope.canvas;
        });

        $scope.drop = function(e) {
          var item = JSON.parse(e.dataTransfer.getData('text/plain'));
            if (Object.keys(item).length === 0) {
                return;
            }

            var obj = null;
            var form = {
                $invalid: true,
                operatorName: {
                    $invalid: false
                }
            };
            angular.forEach(item.properties, function(property) {
                if (property.type === 'StringList') {
                    if (property.value) {
                        angular.forEach(property.value, function(value, index) {
                            if (value) {
                                form[property.name + '' + index] = {
                                    '$invalid': false
                                };
                            } else {
                                form[property.name + '' + index] = {
                                    '$invalid': true
                                };
                            }
                        });
                    }
                } else {
                    if (property.value) {
                        form[property.name] = {
                            $invalid: false
                        };
                    } else {
                        form[property.name] = {
                            $invalid: true
                        };
                    }

                }
            });
            if (item.classType === 'input') {
                obj  =  angular.extend(item, {
                    'sources': [{
                        uuid: getNextUUID()
                    }],
                    'targets': [{
                        uuid: getNextUUID()
                    }],
                    x: e.offsetX,
                    y: e.offsetY,
                    color: item.color,
                    form: form
                });
            } else if (item.classType === 'output') {
                obj  =  angular.extend(item, {
                    'targets': [{
                        uuid: getNextUUID()
                    }],
                    x: e.offsetX,
                    y: e.offsetY,
                    color: item.color,
                    form: form
                });
            } else {
                obj  =  angular.extend(item, {
                    'sources': [{
                        uuid: getNextUUID()
                    }],
                    'targets': [{
                        uuid: getNextUUID()
                    }],
                    x: e.offsetX,
                    y: e.offsetY,
                    color: item.color,
                    form: form
                });
            }
            console.log(obj);
            if (obj.name) {
                obj.name = obj.name.substring(0, 13);
            } else {
                obj.name = '';
            }
            $scope.stateObjects.push(obj);
            $scope.updateJSON();
            $scope.validateAll();
            $scope.$apply();
        };

        $scope.log = function(event) {
            console.log(event.x + ' x ' + event.y);
            return true;
        };

        $scope.removeAll = function() {
            $scope.stateObjects = [];
            $scope.activeState = null;
            $scope.jsonConfig = {
                "description": "DefaultJSON",
                "operators": [],
                "streams": []
            };
	        $scope.buildInvalid = true;
            $scope.systemName = '';
            $scope.canvas.zoomlevel = 30;
            $scope.oldZoom = 30;
            $scope.resetPosition();
        };

        $scope.clearSelected = function() {
            delete $scope.activeState;
        };

        $scope.removeIndex = function(index, object) {
            object.splice(index, 1);
            if ($scope.activeState === $scope.stateObjects[index]) {
                $scope.activeState = null;
            }
        };

        $scope.refreshStates = function() {
          $scope.refreshing = true;
          $timeout(function() {
            $scope.refreshing = false;
          }, 10);
        };

        $scope.removeState = function(state) {
            var index = $scope.stateObjects.indexOf(state);
            if (index !== -1) {
                $scope.stateObjects.splice(index, 1);
                if ($scope.activeState === state) {
                    $scope.activeState = null;
                }
            }
            $scope.removeAllConnections(state);
            $scope.updateJSON();
            $scope.updateConnections();
            $scope.refreshStates();
        };

        $scope.removeAllConnections = function(removedState) {
            console.log(removedState);
            if (removedState.targets !== undefined && removedState.targets.length > 0) {
                var uuids = [];
                angular.forEach(removedState.targets, function(target) {
                    uuids.push(target.uuid);
                });
                angular.forEach($scope.stateObjects, function(state) {
                    angular.forEach(state.sources, function(source) {
                        angular.forEach(source.connections, function(connection) {
                            for (var i = 0; i < uuids.length; i++) {
                                if (parseInt(connection.uuid) === uuids[i]) {
                                    source.connections.splice(source.connections.indexOf(connection), 1);
                                    i = uuids.length;
                                }
                            }
                        });
                    });
                });
            }
        };

        $scope.removeStateConnection = function(state, epIndex, cntIndex) {
          console.log("Removing state connection from ", state, epIndex, cntIndex);
            state.sources[epIndex].connections.splice(cntIndex, 1);
        };

        $scope.equalizeZoom = function() {
            if ($scope.oldZoom !== $scope.canvas.zoomlevel) {
                $scope.canvas.x += ($scope.canvas.zoomlevel - $scope.oldZoom) / 5 * -1368.1;
                $scope.canvas.y += ($scope.canvas.zoomlevel - $scope.oldZoom) / 5 * -1364.65;
                $scope.oldZoom = $scope.canvas.zoomlevel;
            }
        };

        $scope.load = function() {
            $scope.stateObjects = $localStorage.stateObjects;
            $scope.systemName = $localStorage.systemName;
            $scope.canvas = $localStorage.canvas;
            $scope.mode = $localStorage.mode;
            $scope.oldZoom = $localStorage.oldZoom;
            $rootScope.mode = $scope.mode;
            $scope.equalizeZoom();
        };

        if (typeof $localStorage.stateObjects !== 'undefined') {
            $scope.load();
        } else {
            $timeout(function() {
                $scope.resetPosition();
            }, 1);
        }

        var loadingMode = false;
        $timeout(function () {
            if ($scope.mode === 'select') {
                loadingMode = true;
                $scope.toggleMode('select');
            }
        }, 100);

        $scope.toggleMode = function(mode) {
            $scope.mode = mode;
            if (!loadingMode) {
                $scope.update();
            } else {
                loadingMode = false;
            }
            $scope.canvasRefresh = true;
            $scope.refreshing = true;
            //$localStorage.stateObjects = $scope.stateObjects;
            /*var boolean;
            if ($rootScope.mode === 'move') {
                boolean = false;
            } else if ($rootScope.mode === 'select') {
                boolean = true;
            }*/
            $timeout(function() {
                $scope.canvasRefresh = false;
                $scope.refreshing = false;
                $scope.load();
                $scope.equalizeZoom();
                //$scope.stateObjects = $localStorage.stateObjects;
                /*angular.forEach($scope.stateObjects, function (stateObject) {
                    jsPlumb.setDraggable(stateObject.id, boolean);
                });*/
            }, 30);
        };

        $scope.update = function() {
	        console.log('Saving...');
            $localStorage.systemName = $scope.systemName;
            $localStorage.stateObjects = $scope.stateObjects;
            $localStorage.canvas = $scope.canvas;
            $localStorage._id = $scope._id;
            $localStorage.mode = $scope.mode;
            $localStorage.oldZoom = $scope.oldZoom;
            if ($scope.operatorList) {
                $localStorage.savedOperators = $scope.operatorList[$scope.operatorList.length - 1].operators;
            }
            $rootScope.mode = $scope.mode;
            $scope.updateJSON();
            $scope.updateConnections();
	        $scope.validateAll();
            $scope.parseInvalidText();
            console.log($scope.jsonConfig);
            console.log($scope.stateObjects);
        };

        $scope.$watch('stateObjects', function() {
            if (!$scope.updating) {
                $scope.updating = !$scope.updating;
                $scope.update();
                $scope.updating = !$scope.updating;
            }
        }, true);

	    $scope.updateJSON = function() {
		    $scope.jsonConfig = {
			    "system_descriptor":{
				    "state": JSON.stringify($scope.stateObjects),
				    "name": $scope.systemName,
				    "application": {
					    "description": "Example Application",
					    "operators": [],
					    "streams": []
				    },
				    "mappings": {}
			    },
			    "request": "saveSystemDescriptor"
		    };
		    if ($scope._id !== undefined && $scope._id !== null) {
			    $scope.jsonConfig.system_descriptor._id = $scope._id;
		    }
		    var mappings = {};
			angular.forEach($scope.stateObjects, function(state) {
				var operator = {
					"name": state.name,
					"class_name": state.class_name,
					"properties": {
						"type": state.type
					}
				};
				angular.forEach(state.properties, function(property) {
                    if (property.value && property.value !== undefined) {
                        if (property.type === 'File') {
                            operator.properties[property.name] = property.fileName;
                            if (property.value !== undefined) {
                                mappings[state.name] = property.value;
                            }
                        } else {
                            operator.properties[property.name] = property.value !== undefined ? property.value.toString() : undefined;
                        }
                    }
				});
				$scope.jsonConfig.system_descriptor.application.operators.push(operator);
			});
		    $scope.jsonConfig.system_descriptor.mappings = mappings;
	    };

	    $scope.updateConnections = function() {
		    $scope.jsonConfig.system_descriptor.application.streams = [];
		    var streamNum = 1;
		    angular.forEach($scope.stateObjects, function(state) {
			    angular.forEach(state.sources, function(source) {
				    var sinks = [];
                    var operatorNames = [];
			        angular.forEach(source.connections, function(connection) {
                        var sink = {
                            operator_name: '',
                            port_name: "input"
                        };
                        var targetUUID = parseInt(connection.uuid);
                        //stuff
                        angular.forEach($scope.stateObjects, function (state2) {
                            angular.forEach(state2.targets, function (target) {
                                if (targetUUID === target.uuid) {
                                    sink.operator_name = state2.name;
                                }
                            });
                        });
                        if (operatorNames.indexOf(sink.operator_name) < 0) {
                            operatorNames.push(sink.operator_name);
                            sinks.push(sink);
                        }
				    });
				    if (source.connections !== undefined && source.connections.length > 0) {
					    var stream = {
						    name: 'Stream ' + streamNum,
						    source: {
							    operator_name: state.name,
							    "port_name": "output"
						    },
						    sinks: sinks
					    };
					    streamNum++;
					    $scope.jsonConfig.system_descriptor.application.streams.push(stream);
				    }
			    });
		    });

	    };

        $scope.$watch('canvas', function (canvas) {
            $localStorage.canvas = canvas;
        });

        if (typeof $localStorage.lastUUID === 'undefined') {
            $localStorage.lastUUID = 2000;
        }

        function getNextUUID() {
            $localStorage.lastUUID++;
            return $localStorage.lastUUID;
        }

        $scope.$on('Sending saved operator', function(data, operator) {
            $scope.activeState = operator;
            $scope.update();
            console.log(operator);
        });

        $scope.setActiveState = function(state) {
            if ($rootScope.mode === 'select') {
                $scope.activeState = state;
                $scope.update();
            }
        };

        $scope.setActiveStateFromEvent = function(e) {
            if (e.target.tagName !== "JS-PLUMB-CANVAS") {
                var scope = angular.element(e.target).scope();
                $scope.setActiveState(scope ? scope.state : undefined);
            } else {
                $scope.setActiveState(undefined);
            }
        };

        $scope.hasEventualTarget = function(instance, source, target) {
            if (!source) {
                console.log("No source node!");
                return false;
            }
            var cyclical = false;
                angular.forEach(source.connections, function(c) {
                    angular.forEach(c.endpoints, function(e) {
                    if (e.canvas.classList.contains("ep-target")) {
                        if (e._jsPlumb.uuid === target._jsPlumb.uuid) {
                            cyclical = true;
                        } else {
                            var ret = $scope.hasEventualTarget(instance, instance.getEndpoint($(e.element).parent().parent().children(".endpoint-source").children().attr("uuid")), target);
                            if (ret) {
                                cyclical = true;
                            }
                        }
                    }
                });
            });
                return cyclical;
            };


            $scope.isCyclical = function(instance, sourceUUID) {
                var e = instance.getEndpoint(sourceUUID);
                if (e) {
                    return $scope.hasEventualTarget(instance, e, instance.getEndpoint($(e.element).parent().parent().children(".endpoint-target").children().attr("uuid")));
                }
                return false;
        };

        $scope.onConnection = function(instance, connection, targetUUID, sourceUUID) {
            angular.forEach($scope.stateObjects, function(state) {
                angular.forEach(state.sources, function(source) {
                    if (source.uuid === +sourceUUID) {
                        if (typeof source.connections === 'undefined') {
                            source.connections = [];
                        }
                        if (source.connections.some(function(x) {
                                return x.uuid === targetUUID;
                            })) {
                            return;
                        }
                        source.connections.push({
                            'uuid': targetUUID
                        });
                        if ($scope.isCyclical(instance, sourceUUID)) {
                          source.connections.pop();
                        }
                        $scope.$apply();
                        $scope.updateConnections();

                    }
                });
            });
            $scope.validateAll();
        };

        $scope.saveToManager = function() {
            $scope.updateJSON();
            $scope.updateConnections();
            saveStream.send($scope.jsonConfig);
            console.log("Saved to manager");
            console.log($scope.jsonConfig);
        };

        $scope.validateForm = function(state, index, type, listIndex) {
            if (state.properties[index].required) {
                var value = state.properties[index].value;
                var name = state.properties[index].name;
	            if (type === 'list') {
		            value = value[listIndex];
		            name = name + '' + listIndex;
	            }
	            var preValid = state.form[name].$invalid;
	            state.form[name].$invalid = value === undefined || value === null || value.toString().length < 1;

	            if (type === 'integer') {
		            state.form[name].$invalid = isNaN(value) || state.form[name].$invalid;
	            } else if (type === 'file' && state.display_name.toLowerCase() === 'json mapping') {
                    var jsonMappingSocket = $websocket($rootScope.dataService);
                    jsonMappingSocket.onMessage(function(message) {
                        state.properties[index].value = JSON.parse(message.data);
                    });
                    jsonMappingSocket.send(JSON.stringify({'request': 'decodeBase64', 'base64': state.properties[index].value.substr(13)}));
                }
	            if (preValid !== state.form[name].$invalid) {
                    //validate entire thing
                    $scope.validateState(state);

                }

            }

        };

        $scope.validateState = function(state) {
            var invalid = [];
            state.form.$invalid = false;
            if (state.form.operatorName.$invalid) {
                state.form.$invalid = true;
                invalid.push('operatorName');
            }
	        if (state.properties !== undefined && state.properties !== null) {
		        for (var i = 0; i < state.properties.length; i++) {
			        var name = state.properties[i].name;
			        if (state.properties[i].required && state.properties[i].type === 'StringList') {
				        for (var j = 0; j < state.properties[i].value.length; j++) {
					        if (state.form[name + j].$invalid) {
						        state.form.$invalid = true;
						        invalid.push(name + j);
					        }
				        }
			        } else if (state.properties[i].required && state.form[name].$invalid) {
				        state.form.$invalid = true;
				        invalid.push(name);
			        }
		        }
	        }

            return invalid;
        };

        $scope.validateAll = function() {
            var names = [];
            $scope.invalid = [];
            if (!$scope.systemNameRegEx.exec($scope.systemName)) {
                $scope.invalid.push('System name does not conform to style guidelines (only alpha-numeric characters, dashes, and underscores allowed)');
            }
            if ($scope.systemNamesIds && $scope.systemNamesIds.hasOwnProperty($scope.systemName) && $scope.systemNamesIds[$scope.systemName] !== $scope._id) {
                $scope.invalid.push('System name is not unique');
            }

	        if ($scope.stateObjects.length < 1) {
                $scope.invalid.push('No operators are present');
	        } else {
		        var noInput = "No input operators";
                var noParser = "No parser operator";
		        var noOutput = "No output operators";
                var input = [];
                var output = [];
                var middle = [];
                var operatorValidateList = {};
                var i = 0;

                angular.forEach($scope.stateObjects, function(state) {
                    if (!state.form) {
                        $scope.invalid.push(state + ' is undefined');

                    } else if (state.form.$invalid) {
                        $scope.invalid.push([state.name].concat($scope.validateState(state)));
                    }

                    if (names.indexOf(state.name) < 0) {
                        names.push(state.name);
                        operatorValidateList[state.name] = {'classType': state.classType, 'index': i};
                    } else {
                        $scope.invalid.push('\"' + state.name + '\" is being used as two different operator names');
                    }

                    if (state.classType === 'input') {
                        input.push(state.name);
                        noInput = null;
                    } else if (state.classType === 'output') {
                        output.push(state.name);
                        noOutput = null;
                    } else {
                        middle.push(state.name);
                        middle.push(state.name);
                        if (state.classType === 'parser') {
                            noParser = null;
                        }
                    }
                    i++;
                });

                if (noInput) {
                    $scope.invalid.push(noInput);
                }
                if (noParser) {
                    $scope.invalid.push(noParser);
                }
                if (noOutput) {
                    $scope.invalid.push(noOutput);
                }

                angular.forEach($scope.jsonConfig.system_descriptor.application.streams, function(stream) {
                    if (input.indexOf(stream.source.operator_name) > -1) {
                        input.splice(input.indexOf(stream.source.operator_name), 1);
                    } else if (middle.indexOf(stream.source.operator_name) > -1) {
                        middle.splice(middle.indexOf(stream.source.operator_name), 1);
                    }
                    var source = operatorValidateList[stream.source.operator_name];
                    operatorValidateList[stream.source.operator_name].targets = [];
                    angular.forEach(stream.sinks, function(sink) {
                        if (output.indexOf(sink.operator_name) > -1) {
                            output.splice(output.indexOf(sink.operator_name), 1);
                        } else if (middle.indexOf(sink.operator_name) > -1) {
                            middle.splice(middle.indexOf(sink.operator_name), 1);
                        } else {
                            $scope.invalid.push(sink.operator_name + ' has too many inputs');
                        }
                        var target = operatorValidateList[sink.operator_name];
                        operatorValidateList[stream.source.operator_name].targets.push({'name': sink.operator_name,'classType': target.classType, 'index': target.index});
                        if ((source.classType === 'input' && target.classType !== 'parser') ||
                             (source.classType === 'parser' && target.classType !== 'mapping' && target.classType !== 'enrichment' && target.classType !== 'output') ||
                             (source.classType === 'mapping' && target.classType !== 'enrichment' && target.classType !== 'output') ||
                             (source.classType === 'enrichment' && target.classType !== 'mapping' && target.classType !== 'output')) {
                            $scope.invalid.push(stream.source.operator_name + ' ' + source.classType + ' operator can\'t connect with ' + sink.operator_name + ' ' + target.classType + ' operator');
                        }

                        /*if (source.classType === 'enrichment' && target.classType === 'mapping'){
                            angular.forEach($scope.stateObjects[source.index].properties, function(property) {
                                if (property.name == 'parentDataField' || property.name === 'dataField') {
                                    angular.forEach($scope.stateObjects[target.index].properties, function(targetProp) {
                                        if (targetProp.name === 'mappingFile' && targetProp.value) {
                                            property.choices = targetProp.value;
                                        }
                                    });
                                }
                            });
                        } else if (target.classType === 'enrichment' && source.classType === 'mapping') {
                            angular.forEach($scope.stateObjects[target.index].properties, function(property) {
                                if (property.name == 'parentDataField' || property.name === 'dataField') {
                                    angular.forEach($scope.stateObjects[source.index].properties, function(targetProp) {
                                        if (targetProp.name === 'mappingFile' && targetProp.value) {
                                            property.choices = targetProp.value;
                                        }
                                    });
                                }
                            });
                        }*/
                    });
                });

                angular.forEach(operatorValidateList, function(operator) {
                    if (operator.classType === 'enrichment') {
                        //var parentDataFieldIndex = -1;
                        var keyFieldIndex = -1;
                        angular.forEach($scope.stateObjects[operator.index].properties, function(prop, key) {
                            //if (prop.name === 'parentDataField') {
                                //parentDataFieldIndex = key;
                            /*} else */if (prop.name === 'keyField') {
                                keyFieldIndex = key;
                            }
                        });
                        if (operator.targets && operator.targets.length > 0) {
                            angular.forEach(operator.targets, function(target) {
                                if (target.classType === 'mapping') {
                                    var mapping = null;
                                    angular.forEach($scope.stateObjects[target.index].properties, function(Tprop) {
                                        if (Tprop.name === 'mappingFile') {
                                            mapping = Tprop.value;
                                        }
                                    });
                                    console.log(mapping);
                                    if (mapping === undefined) {
                                        mapping = null;
                                    } if (mapping) {
                                        mapping = Object.keys(mapping);
                                        mapping.sort();
                                    }
                                    //$scope.stateObjects[operator.index].properties[parentDataFieldIndex].choices = mapping;
                                    $scope.stateObjects[operator.index].properties[keyFieldIndex].choices = mapping;
                                }
                            });
                        } else {
                            //$scope.stateObjects[operator.index].properties[parentDataFieldIndex].choices = null;
                            $scope.stateObjects[operator.index].properties[keyFieldIndex].choices = null;
                        }
                    } else if (operator.classType === 'mapping') {
                        var mapping = null;
                        angular.forEach($scope.stateObjects[operator.index].properties, function(prop) {
                            if (prop.name === 'mappingFile') {
                                mapping = prop.value;
                            }
                        });
                        if (mapping === undefined) {
                            mapping = null;
                        }
                        if (mapping) {
                            mapping = Object.keys(mapping);
                            mapping.sort();
                        }
                        if (operator.targets && operator.targets.length > 0) {
                            angular.forEach(operator.targets, function(target) {
                                if (target.classType === 'enrichment') {
                                    angular.forEach($scope.stateObjects[target.index].properties, function(Tprop, Tkey) {
                                        if (/*Tprop.name === 'parentDataField' || */Tprop.name === 'keyField') {
                                            $scope.stateObjects[target.index].properties[Tkey].choices = mapping;
                                            delete operatorValidateList[target.name];
                                        }
                                    });
                                }
                            });
                        }
                    }
                });

                if (output.length + middle.length + input.length > 0) {
                    $scope.invalid.push("Too few connections");
                }

	        }

            $scope.buildInvalid = $scope.invalid.length > 0;
            if ($scope.buildInvalid) {
                console.log('Not a valid build');
                console.log($scope.invalid);
            } else {
                console.log('Build is valid');
            }
        };

        $scope.removeFromList = function(state, index, listIndex) {
            var name = state.properties[index].name;
	        delete state.form[name + listIndex];
        };

	    $scope.addToList = function(prop, activeState) {
		    activeState.form[prop.name + prop.value.length] = {'$invalid': true};
		    prop.value.push('');
	    };

        $scope.parseInvalidText = function() {
            $scope.invalid_text = [];
            angular.forEach($scope.invalid, function(invalid) {
                if (Array.isArray(invalid)) {
                    var name = invalid[0] || 'Unnamed system';
                    for (var i = 1; i < invalid.length; i++) {
                        $scope.invalid_text.push(name + ' has an invalid ' + invalid[i]);
                    }
                } else {
                    $scope.invalid_text.push(invalid);
                }
            });
        };
        $scope.flipError = function() {
            $scope.hideErrors = !$scope.hideErrors;
        };

        $scope.saveOperator = function(operator) {
            if (operator && $scope.operatorList) {
                var copy = jQuery.extend(true, {}, operator);
                for (var i = 0; i < $scope.operatorList[$scope.operatorList.length - 1].operators.length; i++) {
                    var op = $scope.operatorList[$scope.operatorList.length - 1].operators[i];
                    if (op.name === operator.name) {
                        $scope.operatorList[$scope.operatorList.length - 1].operators[i] = copy;
                        copy = null;
                        i = $scope.operatorList[$scope.operatorList.length - 1].operators.length;
                    }
                }
                if (copy) {
                    $scope.operatorList[$scope.operatorList.length - 1].operators.push(copy);
                }
            }
        };

        hotkeys.add({
            combo: 'ctrl+c',
            description: 'copy selected',
            callback: function() {
                    $scope.saveOperator($scope.activeState);
            }
        });

        hotkeys.add({
            combo: 'ctrl+x',
            description: 'cut selected',
            callback: function() {
                $scope.saveOperator($scope.activeState);
                $scope.removeState($scope.activeState);
            }
        });

        /*hotkeys.add({
            combo: 'ctrl+v',
            description: 'paste selected',
            callback: function() {
                if ($scope.clipboard) {
                    $scope.clipboard.x = $scope.mousePos.x;
                    $scope.clipboard.y = $scope.mousePos.y;
                    $scope.stateObjects.push($scope.clipboard);
                }
            }
        });*/

        $scope.startDetached = function () {
            tourSteps.start();
            TourService.getTourByName('tour').on('ended', function () {
                console.log("Ending tour");
                $localStorage.toured = true;
            });
        };

        $scope.tourOnLoad = function() {
            if (!$localStorage.hasOwnProperty('toured')) {
                $scope.startDetached();
            }
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

        $rootScope.setTempOperator = function() {
            console.log("here");
            $scope.activeState = $scope.operatorList[0].operators[0];
        };

        $rootScope.removeTempOperator = function() {
            $scope.activeState = null;
        };

    }
})();
