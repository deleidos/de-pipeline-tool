(function () {
    "use strict";

    angular.module('systemBuilder')
        .controller('SystemBuilderController', ['$scope', '$localStorage', '$timeout', '$rootScope', '$websocket', 'hotkeys', 'uiTourService', 'tourSteps', SystemBuilderController]);

    function SystemBuilderController($scope, $localStorage, $timeout, $rootScope, $websocket, hotkeys, TourService, tourSteps) {

	    var dataStream = $websocket($rootScope.dataService);
        //Responsible for getting operator data
        var getStream = $websocket($rootScope.dataService);
        //Gets a system descriptor to load a system
        var saveStream = $websocket($rootScope.dataService);
        //Saves a system
        $scope.buildInvalid = true;
        //Whether or not the current system is valid
        $scope.updating = false;
        //Whether or not the canvas is being refreshed/updated
        $scope.systemName = '';
        //The system name
        $scope.systemNameRegEx = new RegExp("^[A-Za-z0-9-_]+$");
        //Regular expression to check the system name's validity
        $scope.systemNamesIds = {};
        //The IDs already in use for systems
        $scope.activeState = null;
        //The selected state
        $scope.loading = true;
        //Checks if operators are being loaded. Used to check timeouts
        $scope._id = $localStorage._id;
        //The current system ID. Gotten from cache to load a system
        $scope.onlineSystems = [];
        //Array of all online systems. Used to disallow saving while system is online
        if (!$scope.canvas) {
            $scope.canvas = {
                zoomlevel: 30,
                x: 100,
                y: 100
            };
        }
        //Sets canvas if it hasn't been sets
        $scope.stateObjects = [];
        //All current state objects. Used to build the system
        $scope.targetEndpointStyle = {
          endpoint: "Dot",
          paintStyle:{ radius: 15},
          cssClass: 'ep-target',
          isSource: true,
          isTarget: true,
          maxConnections: -1,
          connector: ["Bezier", {
              curviness: 150
          }],
          connectorStyle: {
              lineWidth: 8,
              strokeStyle: "#61B7CF",
              joinstyle: "round",
              outlineColor: "white",
              outlineWidth: 2
          }
        };
        //Style used for a target end point

        $scope.sourceEndpointStyle = {
            endpoint: "Dot",
            paintStyle:{ radius: 15},
            cssClass: 'ep-src',
            isSource: true,
            isTarget: true,
            maxConnections: -1,
            connector: ["Bezier", {
                curviness: 150
            }],
            connectorStyle: {
                lineWidth: 8,
                strokeStyle: "#61B7CF",
                joinstyle: "round",
                outlineColor: "white",
                outlineWidth: 2
            }
        };
        //Style used for a source endpoint

        $scope.jsonConfig = {
            "request": "saveSystemDescriptor",
            "systemDescriptor": {
                "name": $scope.systemName,
                "application": {
                    "operators": [],
                    "streams": []
                },
                "mappings": {}
            }
        };
        //Stores and is sent to save a system

        $scope.refreshing = false;
        //Whether or not states are being refreshed

        $scope.invalid = [];
        //Array of invalid operators

        $scope.invalid_text = [];
        //Array of error text

        $scope.hideErrors = true;
        //Whether or not errors are visible

        $scope.searchOp = {displayName: ''};
        //Search term used for operators

        $scope.canvasRefresh = false;
        //Whether or not canvas is being refreshed

        $scope.oldZoom = 30;
        //Holds old zoom value to be reset if something changes it

        //Broadcast receiver
        //Collects all system names used for name uniqueness
        $scope.$on('SystemNames', function(data, names) {
            $scope.systemNamesIds = names;
            $scope.$apply();
        });

        //Sends request to update operator strip
        $scope.$on('Updating metadata', function() {
            dataStream.send({
                "request": "getOperatorMetadata"
            });
        });

        //On a save, tells system manager to refresh to show new system
        saveStream.onMessage(function(message) {
            $scope._id = JSON.parse(message.data)._id;
            $localStorage._id = $scope._id;
            $scope.$emit('refresh');
        });

        //Requests operators
        dataStream.send({
            "request": "getOperatorMetadata"
        });

        //On request, creates operator list and parses operators into them
        dataStream.onMessage(function(message) {
            if (message.data) {
                var data = JSON.parse(message.data);
                if ($scope.loading) {
                    $scope.$emit('About to send operators', JSON.parse(message.data));
                }
                $scope.operatorList = [
                    {
                        name: 'All',
                        operators: data
                    }, {
                        name: 'Input',
                        matchName: 'input',
                        operators: []
                    }, {
                        name: 'Binary Input',
                        matchName: 'binaryInput',
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
                    }, {
                        name: 'Binary Output',
                        matchName: 'binaryOutput',
                        operators: []
                    }];
                if ($localStorage.savedOperators) {
                    $scope.operatorList.push({
                        name: 'Stored Operators',
                        operators: $localStorage.savedOperators
                    });
                } else {
                    $scope.operatorList.push({
                        name: 'Stored Operators',
                        operators: []
                    });
                }

                //Changes value to an array if the property is a list
                //Stores properties into correct list based on type versus matchname
                angular.forEach(data, function (op) {
                    for (var j = 0; j < op.properties.length; j++) {
                        if (op.properties[j].list) {
                            op.properties[j].value = [''];
                        }
                    }
                    for (var i = 0; i < $scope.operatorList.length; i++) {
                        if ($scope.operatorList.name !== 'All' && $scope.operatorList[i].name !== 'Stored Operators' && op.type === $scope.operatorList[i].matchName) {
                            $scope.operatorList[i].operators.push(op);
                            i = $scope.operatorList.length;
                        }
                    }
                });
                if (!$scope.loading) {
                    $rootScope.refreshOperators($scope.operatorList);
                }
                $scope.$apply();
                $scope.loading = false;
            }
        });


        //Resets canvas position to be central around operators in canvas
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

        //Broadcasted from manager, sent id to load, gets system
        $scope.$on('ID for Builder', function(data, id) {
            $scope._id = id;
            getStream.send({
                "request": "getSystemDescriptor",
                "id": $scope._id
            });
        });

        //Sent all online systems
        $scope.$on('Receiving online systems', function(data, systems) {
           $scope.onlineSystems = systems;
        });

        //Sets system to the one recieved
        getStream.onMessage(function(message) {
            $scope.jsonConfig = JSON.parse(message.data);
	        if ($scope.jsonConfig.state) {
		        $scope.stateObjects = JSON.parse($scope.jsonConfig.state);
                $scope.systemName = $scope.jsonConfig.name;
                $scope.refreshStates();
                $scope.resetPosition();
                setUUID();
	        } else {
		        console.log("No State Present");
	        }
        });

        //Fixes jsplumb bug where moving around would cause weird visual errors
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
            $scope.jsonConfig.systemDescriptor._id = $scope._id;
        }

        //Changes cached canvas on zoom
        $scope.$watch('canvas.zoomlevel', function() {
            $localStorage.canvas = $scope.canvas;
        });

        //Used to drop an operator into the canvas
        //Sets validation handling on drop
        //Creates target and/or source for operator
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
            angular.forEach(item.properties, function(property, index) {
                property.validation = $scope.findValidation(property.type);
                item.properties[index].validation = property.validation;
                if (property.validation.list) {
                    if (property.value) {
                        angular.forEach(property.value, function(value, index) {
                            form[property.name + '' + index] = {
                                '$invalid': (value === null || value === undefined) && property.required
                            };
                        });
                    } else {
                        form[property.name + '' + 0] = {
                            '$invalid': property.required
                        };

                    }
                } else {
                    form[property.name] = {
                        $invalid: (property.value === null || property.value === undefined) && property.required
                    };
                    if (property.validation.type === 'fileInput') {
                        form[property.name].filenameField = (property.filenameField === null || property.filenameField === undefined) && property.required;
                    }
                }
            });
            if (item.type === 'input' || item.type === 'binaryInput') {
                obj  =  angular.extend(item, {
                    'sources': [{
                        uuid: getNextUUID()
                    }],
                    x: e.offsetX,
                    y: e.offsetY,
                    color: item.color,
                    form: form
                });
            } else if (item.type === 'output' || item.type === 'binaryOutput') {
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
            $scope.stateObjects.push(obj);
            $scope.updateJSON();
            $scope.validateAll();
            $scope.$apply();
        };

        //Resets entire builder
        //Clears ID
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
            $scope._id = '';
            $scope.resetPosition();
        };

        //Deleted current operator selected
        $scope.clearSelected = function() {
            delete $scope.activeState;
        };

        $scope.removeIndex = function(index, object) {
            object.splice(index, 1);
            if ($scope.activeState === $scope.stateObjects[index]) {
                $scope.activeState = null;
            }
        };

        //Used to turn on and off operators to refresh them visually
        $scope.refreshStates = function() {
          $scope.refreshing = true;
          $timeout(function() {
            $scope.refreshing = false;
          }, 10);
        };

        //Removes selected operator
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

        //Removes all connections from a removed state
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

        //removes specific connection
        $scope.removeStateConnection = function(state, epIndex, cntIndex) {
            console.log("Removing state connection from ", state, epIndex, cntIndex);
            state.sources[epIndex].connections.splice(cntIndex, 1);
            $scope.refreshStates();
        };

        //Updates everything
        $scope.$on('Sending connection notification', function() {
            $scope.updateJSON();
            $scope.updateConnections();
            $scope.refreshStates();
        });

        //Changes zoom to old zoom
        $scope.equalizeZoom = function() {
            if ($scope.oldZoom !== $scope.canvas.zoomlevel) {
                $scope.canvas.x += ($scope.canvas.zoomlevel - $scope.oldZoom) / 5 * -1368.1;
                $scope.canvas.y += ($scope.canvas.zoomlevel - $scope.oldZoom) / 5 * -1364.65;
                $scope.oldZoom = $scope.canvas.zoomlevel;
            }
        };

        //Loads in info from cache
        $scope.load = function() {
            $scope.stateObjects = $localStorage.stateObjects;
            $scope.systemName = $localStorage.systemName;
            $scope.canvas = $localStorage.canvas;
            $scope.oldZoom = $localStorage.oldZoom;
            $scope.onlineSystems = $localStorage.onlineSystems;
            console.log($scope.onlineSystems);
            $scope.equalizeZoom();
        };

        if ($localStorage.stateObjects && $localStorage.stateObjects !== 'undefined') {
            $scope.load();
        } else {
            $timeout(function() {
                $scope.resetPosition();
            }, 1);
        }

        //Updates cache and calls validaition
        $scope.update = function() {
	        console.log('Saving...');
            $localStorage.systemName = $scope.systemName;
            $localStorage.stateObjects = $scope.stateObjects;
            $localStorage.canvas = $scope.canvas;
            $localStorage._id = $scope._id;
            $localStorage.oldZoom = $scope.oldZoom;
            $localStorage.onlineSystems = $scope.onlineSystems;
            if ($scope.operatorList) {
                $localStorage.savedOperators = $scope.operatorList[$scope.operatorList.length - 1].operators;
            }
            $scope.updateJSON();
            $scope.updateConnections();
	        $scope.validateAll();
            $scope.parseInvalidText();
            console.log($scope.jsonConfig);
            console.log($scope.stateObjects);
        };

        //On a change, updates view
        $scope.$watch('stateObjects', function() {
            if (!$scope.updating) {
                $scope.updating = !$scope.updating;
                $scope.update();
                $scope.updating = !$scope.updating;
            }
        }, true);

        //Updates the system descriptor that's sent for saving
	    $scope.updateJSON = function() {
		    $scope.jsonConfig = {
			    "systemDescriptor":{
				    "state": JSON.stringify($scope.stateObjects),
				    "name": $scope.systemName,
				    "application": {
					    "description": "Example Application",
					    "operators": [],
					    "streams": []
				    },
                    "operatorFiles": {}
			    },
			    "request": "saveSystemDescriptor"
		    };
		    if ($scope._id !== undefined && $scope._id !== null) {
			    $scope.jsonConfig.systemDescriptor._id = $scope._id;
		    }
		    var operatorFiles = {};
			angular.forEach($scope.stateObjects, function(state) {
				var operator = {
					"name": state.name,
					"class": state.className,
					"properties": {}
				};

				//Properly sets properties of all states
				angular.forEach(state.properties, function(property) {
                    if (property.value && property.value !== undefined) {
                        if (property.validation.type === 'fileInput') {
                            if (!operatorFiles[state.name] || operatorFiles[state.name] === undefined) {
                                operatorFiles[state.name] = {};
                            }
                            operator.properties[property.name] = property.fileName;
                            operatorFiles[state.name][property.name] = {
                                filename: property.fileName,
                                filenameField: property.filenameField,
                                bytes: property.value
                            };

                        } else if (property.validation.list) {
                            operator.properties[property.name] = property.value !== undefined ? property.value : undefined;

                        } else {
                            operator.properties[property.name] = property.value !== undefined ? property.value.toString() : undefined;
                        }
                    }
				});
				$scope.jsonConfig.systemDescriptor.application.operators.push(operator);
			});
		    $scope.jsonConfig.systemDescriptor.operatorFiles = operatorFiles;
	    };

        //Redraws all connections
	    $scope.updateConnections = function() {
		    $scope.jsonConfig.systemDescriptor.application.streams = [];
		    var streamNum = 1;
		    angular.forEach($scope.stateObjects, function(state) {
			    angular.forEach(state.sources, function(source) {
				    var sinks = [];
                    var operatorNames = [];
			        angular.forEach(source.connections, function(connection) {
                        var sink = {
                            operatorName: '',
                            portName: "input"
                        };
                        var targetUUID = parseInt(connection.uuid);
                        //stuff
                        angular.forEach($scope.stateObjects, function (state2) {
                            angular.forEach(state2.targets, function (target) {
                                if (targetUUID === target.uuid) {
                                    sink.operatorName = state2.name;
                                }
                            });
                        });
                        if (operatorNames.indexOf(sink.operatorName) < 0) {
                            operatorNames.push(sink.operatorName);
                            sinks.push(sink);
                        }
				    });
				    if (source.connections !== undefined && source.connections.length > 0) {
					    var stream = {
						    name: 'Stream ' + streamNum,
						    source: {
							    operatorName: state.name,
							    portName: "outputPort"
						    },
						    sinks: sinks
					    };
					    streamNum++;
					    $scope.jsonConfig.systemDescriptor.application.streams.push(stream);
				    }
			    });
		    });

	    };

	    //Saves canvas on change
        $scope.$watch('canvas', function (canvas) {
            $localStorage.canvas = canvas;
        });

        //Checks if the last uuid is undefined and if so resets is
        if (typeof $localStorage.lastUUID === 'undefined') {
            $localStorage.lastUUID = 2000;
        }

        //Gets next uuid
        function getNextUUID() {
            $localStorage.lastUUID++;
            return $localStorage.lastUUID;
        }

        //Sets UUID of a state's target and sources
        function setUUID() {
            angular.forEach($scope.stateObjects, function(state) {
                if (state.sources && state.sources !== undefined && state.sources[0].uuid >= $localStorage.lastUUID) {
                    $localStorage.lastUUID = state.sources[0].uuid + 1;
                }
                if (state.targets && state.targets !== undefined && state.targets[0].uuid >= $localStorage.lastUUID) {
                    $localStorage.lastUUID = state.targets[0].uuid + 1;
                }
            });
        }

        //Sends operator to be saved
        $scope.$on('Sending saved operator', function(data, operator) {
            $scope.activeState = operator;
            $scope.update();
            console.log(operator);
        });

        //Sets active state
        $scope.setActiveState = function(state) {
            $scope.activeState = state;
            $scope.update();
        };

        //Sets active state triggeredd by an event
        $scope.setActiveStateFromEvent = function(e) {
            if (e.target.tagName !== "JS-PLUMB-CANVAS") {
                var scope = angular.element(e.target).scope();
                if (scope && scope.stateObject) {
                    $scope.setActiveState(scope.stateObject);
                } else if (scope) {
                    $scope.setActiveState(scope.state);
                } else {
                    $scope.setActiveState(undefined);
                }
            } else {
                $scope.setActiveState(undefined);
            }
        };

        //Checks is state has a target
        $scope.hasEventualTarget = function(instance, source, target) {
            if (!source) {
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

        //checks if a system is cyclical (goes in a circle)
        $scope.isCyclical = function(instance, sourceUUID) {
            var e = instance.getEndpoint(sourceUUID);
            if (e) {
                return $scope.hasEventualTarget(instance, e, instance.getEndpoint($(e.element).parent().parent().children(".endpoint-target").children().attr("uuid")));
            }
            return false;
        };

        //On connection checks some validation and sets connections
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

        //Sends JSOn to be saved as a system
        $scope.saveToManager = function() {
            if ($scope.systemNameRegEx.exec($scope.systemName) !== null &&
                !$scope.buildInvalid &&
                ($scope.systemNamesIds.hasOwnProperty($scope.systemName) && $scope.systemNamesIds[$scope.systemName] === $scope._id || !$scope.systemNamesIds.hasOwnProperty($scope.systemName))) {
                    $scope.updateJSON();
                    $scope.updateConnections();
                    saveStream.send($scope.jsonConfig);
                    console.log("Saved to manager");
                    console.log($scope.jsonConfig);
            }
        };

        //Finds the validation to use for a property
        $scope.findValidation = function(type) {
            var validations = $localStorage.validations.concat($localStorage.customValidations);
            for (var i = 0; i < validations.length; i++) {
                if (type === validations[i].name) {
                    return jQuery.extend(true, {}, validations[i]);
                }
            }
        };

        //Validates the given state's property
        //Then validates the entire state if validation has changed
        $scope.validateForm = function(state, index, type, listIndex) {
            var value;
            var name;
            var preValid;
            var emptyRegExp = new RegExp(/^$/g);
            if (type === 'filenameField') {
                value = state.properties[index].filenameField;
                name = state.properties[index].name;
                preValid = state.form[name].filenameField;
                var string = new RegExp(/.+/g);
                state.form[name].filenameField = string.exec(value) === null;

                if (state.form[name].filenameField && !state.properties[index].required) {
                    state.form[name].filenameField = emptyRegExp.exec(value) === null;
                }
            } else {
                value = state.properties[index].value;
                name = state.properties[index].name;
                if (type === 'list') {
                    value = value[listIndex];
                    name = name + '' + listIndex;
                }
                preValid = state.form[name].$invalid;
                if (type === 'file') {
                    state.properties[index].value = state.properties[index].value.substr(state.properties[index].value.indexOf('base64,') + 7);
                    var fileExtension = state.properties[index].validation.file;
                    if (fileExtension && fileExtension !== undefined && fileExtension !== "") {
                        fileExtension = new RegExp(/fileExtension$/g);
                    } else {
                        fileExtension = new RegExp(/\..+$/g);
                    }
                    var fileNameRegex = fileExtension.exec(state.properties[index].fileName);
                    if (fileNameRegex === null && !state.properties[index].required) {
                        fileNameRegex = emptyRegExp.exec(state.properties[index].fileName);
                    }
                    state.form[name].$invalid = !state.properties[index].value &&
                    state.properties[index].value === undefined &&
                    state.properties[index].value === "" &&
                    fileNameRegex !== null;
                } else {
                    var validation = state.properties[index].validation;
                    var regex = new RegExp(validation.regex, "i");
                    state.form[name].$invalid = !regex.exec(value) || regex.exec(value)[0] !== value.toString();
                    if (state.form[name].$invalid && !state.properties[index].required) {
                        state.form[name].$invalid = emptyRegExp.exec(value) === null;
                    }
                }
            }
            if (preValid !== state.form[name].$invalid || (state.form[name].filenameField !== null && state.form[name].filenameField !== undefined && state.form[name].filenameField !== preValid)) {
                $scope.validateState(state);
            }
        };

        //Checks if all properties are still valid or not
        $scope.validateState = function(state) {
            var invalid = [];
            state.form.$invalid = false;
            if (state.form.operatorName.$invalid) {
                state.form.$invalid = true;
                invalid.push('operatorName');
            }
	        if (state.properties && state.properties !== undefined) {
		        for (var i = 0; i < state.properties.length; i++) {
			        var name = state.properties[i].name;
			        if (state.properties[i].validation.list) {
                        if (state.properties[i].value && state.properties[i].value !== undefined) {
                            for (var j = 0; j < state.properties[i].value.length; j++) {
                                if (state.form[name + j].$invalid) {
                                    state.form.$invalid = true;
                                    invalid.push(name + j);
                                }
                            }
                        } else {
                            state.form.$invalid = true;
                            invalid.push(name + '0');
                        }
			        } else if (state.form[name].$invalid) {
				        state.form.$invalid = true;
				        invalid.push(name);
			        }
		        }
	        }

            return invalid;
        };

        //Checks if every state has returned valid
        //Checks if system name is valid
        //checks if system structure is valid
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
                var binaryInput = false;
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
                        operatorValidateList[state.name] = {'type': state.type, 'index': i};
                    } else {
                        $scope.invalid.push('\"' + state.name + '\" is being used as two different operator names');
                    }

                    if (state.type === 'input' || state.type === 'binaryInput') {
                        input.push(state.name);
                        noInput = null;
                    } else if (state.type === 'output' || state.type === 'binaryOutput') {
                        output.push(state.name);
                        noOutput = null;
                    } else {
                        middle.push(state.name);
                        middle.push(state.name);
                    }
                    if (state.type === 'parser') {
                        noParser = null;
                    }
                    if (state.type === 'binaryInput') {
                        binaryInput = true;
                    }
                    i++;
                });

                if (noInput) {
                    $scope.invalid.push(noInput);
                }
                if (noParser && !binaryInput) {
                    $scope.invalid.push(noParser);
                }
                if (noOutput) {
                    $scope.invalid.push(noOutput);
                }

                angular.forEach($scope.jsonConfig.systemDescriptor.application.streams, function(stream) {
                    if (input.indexOf(stream.source.operatorName) > -1) {
                        input.splice(input.indexOf(stream.source.operatorName), 1);
                    } else if (middle.indexOf(stream.source.operatorName) > -1) {
                        middle.splice(middle.indexOf(stream.source.operatorName), 1);
                    }
                    var source = operatorValidateList[stream.source.operatorName];
                    operatorValidateList[stream.source.operatorName].targets = [];
                    angular.forEach(stream.sinks, function(sink) {
                        if (output.indexOf(sink.operatorName) > -1) {
                            output.splice(output.indexOf(sink.operatorName), 1);
                        } else if (middle.indexOf(sink.operatorName) > -1) {
                            middle.splice(middle.indexOf(sink.operatorName), 1);
                        } else {
                            $scope.invalid.push(sink.operatorName + ' has too many inputs');
                        }
                        var target = operatorValidateList[sink.operatorName];
                        operatorValidateList[stream.source.operatorName].targets.push({'name': sink.operatorName,'type': target.type, 'index': target.index});
                        if (!((source.type === 'input' && target.type === 'parser') ||
                             (source.type === 'parser' && (target.type === 'mapping' || target.type === 'enrichment' || target.type === 'output')) ||
                             (source.type === 'mapping' && (target.type === 'enrichment' || target.type === 'output')) ||
                             (source.type === 'enrichment' && (target.type === 'mapping' || target.type === 'output')) ||
                             (source.type === 'binaryInput' && target.type === 'binaryOutput'))) {
                            $scope.invalid.push(stream.source.operatorName + ' operator can\'t connect with ' + sink.operatorName + ' operator');
                        }
                    });
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

        //Removes property from multi-property
        $scope.removeFromList = function(state, index, listIndex) {
            var name = state.properties[index].name;
	        delete state.form[name + listIndex];
        };

        //Adds property from multi-property
	    $scope.addToList = function(prop, activeState) {
		    activeState.form[prop.name + prop.value.length] = {'$invalid': true};
		    prop.value.push('');
	    };

	    //Parses invalid text to be readable and pushed to the error box
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

        //Toggles error list visible and invisible
        $scope.flipError = function() {
            $scope.hideErrors = !$scope.hideErrors;
        };

        //Saves an operator
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

        //Hot key for saving a system to cache
        hotkeys.add({
            combo: 'ctrl+c',
            description: 'copy selected',
            callback: function() {
                $scope.saveOperator($scope.activeState);
            }
        });

        //Hot key for saving a system to cache and deleting it from the canvas
        hotkeys.add({
            combo: 'ctrl+x',
            description: 'cut selected',
            callback: function() {
                $scope.saveOperator($scope.activeState);
                $scope.removeState($scope.activeState);
            }
        });

        //tour functions

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
            console.log($scope.operatorList[0].operators[0]);
            $scope.activeState = jQuery.extend(true, {},$scope.operatorList[0].operators[0]);

            angular.forEach($scope.activeState.properties, function(property) {
                property.validation = $scope.findValidation(property.type);
            });
        };

        $rootScope.removeTempOperator = function() {
            $scope.activeState = null;
        };

        //End tour functions

    }
})();
