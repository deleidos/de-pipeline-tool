(function () {
    "use strict";

    angular.module('systemBuilder')
        .controller('SystemBuilderController', ['$scope', '$localStorage', '$timeout', '$rootScope', '$websocket', '$uibModal', SystemBuilderController])
        .controller('ToolTip', ['$scope', '$uibModalInstance', ToolTip]);



    function ToolTip($scope, $uibModalInstance) {
        $scope.ok = function() {
            $uibModalInstance.close();
        };
    }

    function SystemBuilderController($scope, $localStorage, $timeout, $rootScope, $websocket, $uibModal) {

	    var dataStream = $websocket($rootScope.dataService);
        var getStream = $websocket($rootScope.dataService);
        var saveStream = $websocket($rootScope.dataService);

        $scope.buildInvalid = true;
        $scope.updating = false;
        $scope.systemNameRegEx = new RegExp("^[A-Za-z0-9-_]+$");
        $scope.systemNamesIds = {};
        $scope.activeState = null;
        $scope.loading = true;
        $scope._id = $localStorage._id;        //add $location parameter to start with id
        $scope.stateConnections = [];
        $scope.canvas = {
            zoomlevel: 20,
            x: 100,
            y: 100
        };
        $scope.stateObjects = [];
        $scope.targetEndpointStyle = {
            endpoint: "Dot",
            cssClass: 'ep-target',
            maxConnections: -1,
            isTarget: true
        };
        $scope.sourceEndpointStyle = {
            endpoint: "Dot",
            cssClass: 'ep-src',
            isSource: true,
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


        $timeout(function() {
            $scope.resetPosition();
        }, 1);

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

            //Parses OP list based off of operator type
            angular.forEach(data, function(op) {
                for (var i = 0; i < $scope.operatorList.length; i++) {
                    if ($scope.operatorList[i].name !== 'All' && op.class_name.toUpperCase().indexOf($scope.operatorList[i].matchName.toUpperCase()) > -1) {
                        op.classType = $scope.operatorList[i].matchName;
                        //Checks for StringList and turns their value into an array if so
                        if (op.properties !== undefined && op.properties !== null) {
                            for (var j = 0; j < op.properties.length; j++) {
                                if (op.properties[j].type === 'StringList') {
                                    op.properties[j].value = [''];
                                }
                            }
                        }
	                    $scope.operatorList[i].operators.push(op);
	                    i = $scope.operatorList.length;
                    }
                }
            });
	        console.log($scope.operatorList);
            $scope.$apply();
            $scope.loading = false;
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
          $scope.canvas.zoomlevel = 50;
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
                    return {
                        left: coords.left - xOffset,
                        top: coords.top - yOffset
                    };
                } else {
                    return {
                        left: coords.left - xOffset - $("js-plumb-canvas").width() / 2,
                        top: coords.top - yOffset - $("js-plumb-canvas").height() / 2
                    };
                }
            });
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

        $scope.increaseZoom = function() {
            $scope.canvas.zoomlevel += 5;
        };

        $scope.decreaseZoom = function() {
            $scope.canvas.zoomlevel -= 5;
        };

        $scope.drop = function(e) {
          var item = JSON.parse(e.dataTransfer.getData('text/plain'));
            console.log(item);
            if (Object.keys(item).length === 0) {
                return;
            }

            var obj = null;
            var form = {
                $invalid: true,
                operatorName: {
                    $invalid: true
                }
            };
            angular.forEach(item.properties, function(property) {
                if (property.type === 'StringList') {
	                form[property.name + '0'] = {'$invalid': true};
                } else {
	                form[property.name] = {
		                $invalid: true
	                };
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
            obj.name = '';
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
            $scope.buildInvalid = true;
        };

        if (typeof $localStorage.stateObjects !== 'undefined') {
            $scope.stateObjects = $localStorage.stateObjects;
            $scope.systemName = $localStorage.systemName;
            $scope.canvas = $localStorage.canvas;
        }

        $scope.update = function() {
	        console.log('Saving...');
            $localStorage.systemName = $scope.systemName;
            $localStorage.stateObjects = $scope.stateObjects;
            $localStorage.canvas = $scope.canvas;
            $localStorage._id = $scope._id;
            $scope.updateJSON();
            $scope.updateConnections();
	        $scope.validateAll();
            console.log($scope.jsonConfig);
            if ($scope.stateObjects.length < 1) {
                $scope.buildInvalid = true;
            }
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
                                mappings[state.name] = property.value.substr(13);
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

        $scope.setActiveState = function(state) {
            $scope.activeState = state;
	        $scope.update();
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
            console.log(source.element, target.element);
            var cyclical = false;
                angular.forEach(source.connections, function(c) {
                    angular.forEach(c.endpoints, function(e) {
                    if (e.isTarget) {
                        if (e._jsPlumb.uuid === target._jsPlumb.uuid) {
                            console.log("cyclical!");
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
        };

        $scope.validateAll = function() {
            var names = [];
            var invalid = [];
            angular.forEach($scope.stateObjects, function(state) {
	            if (!state.form) {
		            invalid.push(state + ' is undefined');

	            } else if (state.form.$invalid) {
                    invalid.push(state);
                    $scope.validateState(state);
                }
                if (names.indexOf(state.name) < 0) {
                    names.push(state.name);
                } else {
                    invalid.push(state.name + ' is being used as two different operator names');
                }
            });

	        if ($scope.jsonConfig.system_descriptor === null || $scope.jsonConfig.system_descriptor === undefined) {
		        invalid.push('System Empty');
	        } else {
		        var noInput = "No input operators";
		        var noOutput = "No output operators";
                var input = [];
                var output = [];
                var middle = [];
		        if ($scope.jsonConfig.system_descriptor.application.operators.every(
				        function(x) {
					        return !x.class_name || x.class_name.indexOf("Input") === -1;
				        })) {
			        invalid.push(noInput);
		        }
		        if ($scope.jsonConfig.system_descriptor.application.operators.every(
				        function(x) {
					        return !x.class_name || x.class_name.indexOf("Output") === -1;
				        })) {
			        invalid.push(noOutput);
		        }
		        /*if ($scope.jsonConfig.system_descriptor.application.streams.length < $scope.jsonConfig.system_descriptor.application.operators.length - 1) {
			        invalid.push("Too few connections");
		        }*/
                angular.forEach($scope.jsonConfig.system_descriptor.application.operators, function(operator) {
                    if (operator.class_name.indexOf("Input") > -1) {
                        input.push(operator.name);
                    } else if (operator.class_name.indexOf("Output") > -1) {
                        output.push(operator.name);
                    } else {
                        middle.push(operator.name);
                        middle.push(operator.name);
                    }
                });


                angular.forEach($scope.jsonConfig.system_descriptor.application.streams, function(stream) {
                    if (input.indexOf(stream.source.operator_name) > -1) {
                        input.splice(input.indexOf(stream.source.operator_name), 1);
                    } else if (middle.indexOf(stream.source.operator_name) > -1) {
                        middle.splice(middle.indexOf(stream.source.operator_name), 1);
                    }
                    angular.forEach(stream.sinks, function(sink) {
                        if (output.indexOf(sink.operator_name) > -1) {
                            output.splice(output.indexOf(sink.operator_name), 1);
                        } else if (middle.indexOf(sink.operator_name) > -1) {
                            middle.splice(middle.indexOf(sink.operator_name), 1);
                        } else {
                            invalid.push(sink.operator_name + ' has too many inputs');
                        }
                    });
                });

                if (output.length + middle.length + input.length > 0) {
                    invalid.push("Too few connections");
                }

	        }
            $scope.buildInvalid = invalid.length > 0;
            if ($scope.buildInvalid) {
                console.log('Not a valid build');
                console.log(invalid);
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
    }
})();
