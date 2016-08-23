describe('SystemBuilderController', function () {
	var testScope, testSocket;
	var fakeInput = {
		"name": "Input",
		"class_name": "com.deleidos.framework.operators.s3.S3InputOperator",
		"display_name": "S3 Input",
		"properties": [{
			"name": "bucketName",
			"display_name": "Bucket Name",
			"choices": null,
			"required": true,
			"type": "String",
			"value": "rtws.flight.data"
		}, {
			"name": "splitter",
			"display_name": "Splitter",
			"choices": ["Line", "JSON"],
			"required": true,
			"type": "String",
			"value": "JSON"
		}, {
			"name": "headerRows",
			"display_name": "Header Row Count",
			"choices": null,
			"required": true,
			"type": "Integer",
			"value": 0
		}],
		"classType": "input",
		"color": "#b77033",
		"sources": [{
			"uuid": 2056,
			"connections": [{
				"uuid": "2061"
			}]
		}],
		"x": 1508,
		"y": 1577,
		"form": {
			"$invalid": false,
			"operatorName": {
				"$invalid": false
			},
			"bucketName": {
				"$invalid": false
			},
			"path": {
				"$invalid": false
			},
			"accessKey": {
				"$invalid": false
			},
			"secretKey": {
				"$invalid": false
			},
			"endpoint": {
				"$invalid": true
			},
			"splitter": {
				"$invalid": false
			},
			"headerRows": {
				"$invalid": false
			}
		}
	};

	var fakeOutput = {
		"name": "Output",
		"class_name": "com.deleidos.framework.operators.mongodb.MongoDbOutputOperator",
		"display_name": "MongoDB Output",
		"properties": [{
			"name": "hostName",
			"display_name": "Hostname",
			"choices": null,
			"required": true,
			"type": "String",
			"value": "54.145.31.90"
		}, {
			"name": "hostPort",
			"display_name": "Port",
			"choices": null,
			"required": true,
			"type": "String",
			"value": "27017"
		}, {
			"name": "database",
			"display_name": "Database Name",
			"choices": null,
			"required": true,
			"type": "String",
			"value": "local"
		}, {
			"name": "userName",
			"display_name": "Username",
			"choices": null,
			"required": false,
			"type": "String"
		}, {
			"name": "password",
			"display_name": "Password",
			"choices": null,
			"required": false,
			"type": "String"
		}],
		"classType": "output",
		"color": "#855093",
		"targets": [{
			"uuid": 2061
		}],
		"x": 2395,
		"y": 1661,
		"form": {
			"$invalid": false,
			"operatorName": {
				"$invalid": false
			},
			"hostName": {
				"$invalid": false
			},
			"hostPort": {
				"$invalid": false
			},
			"database": {
				"$invalid": false
			},
			"userName": {
				"$invalid": false
			},
			"password": {
				"$invalid": false
			}
		}
	};

	var fakeSystemName = 'test';

	beforeEach(function () {
		module('main', function ($provide) {
			$provide.value('$location', {
				host: function () {
					return 'test';
				}
			});
		});
		module('systemBuilder');
		angular.mock.module('ngWebSocket', 'ngWebSocketMock');
		inject(function ($controller, $rootScope, $websocketBackend) {
			testScope = $rootScope;
			testSocket = $websocketBackend;
			$controller('SystemBuilderController', {
				$scope: testScope
			});
		});
	});

	it('should successfully initiate data', function () {
		expect(testScope.activeState).toBeNull();
	});

	it('should connect to the appropriate server', function() {
		testSocket.mock();
		testSocket.expectConnect('test');
		testSocket.expectSend({"request": "getOperatorMetadata"});
	});


	beforeEach(function() {
		testScope.stateObjects = [];
		testScope.stateObjects.push(fakeInput);
		testScope.stateObjects.push(fakeOutput);
		testScope.systemName = fakeSystemName;
		testScope.updateJSON();
		testScope.updateConnections();
		testScope.validateAll();
	});

	it('should add operators to the map and update jsonConfig', function() {
		expect(testScope.jsonConfig).not.toBe(null);
	});

	/*it('should validate to true', function() {
		expect(testScope.buildInvalid).toBe(false);
	});*/

	it('should validate to false if there\'s no name', function() {
		testScope.stateObjects[0].properties[0].value = "";
		testScope.updateJSON();
		testScope.validateForm(testScope.stateObjects[0], 0, 'normal', null);
		testScope.validateAll();
		expect(testScope.buildInvalid).toBe(true);
	});

	it('should validate to false if there\'s a string instead of a number', function() {
		testScope.stateObjects[0].properties[2].value = "test";
		testScope.updateJSON();
		testScope.validateForm(testScope.stateObjects[0], 0, 'normal', null);
		testScope.validateAll();
		expect(testScope.buildInvalid).toBe(true);
	});

	it('should clear all', function() {
		testScope.removeAll();
		expect(testScope.stateObjects.length).toBe(0);
		expect(testScope.activeState).toBe(null);
		expect(testScope.jsonConfig).toEqual({
			"description": "DefaultJSON",
			"operators": [],
			"streams": []
		});
		expect(testScope.buildInvalid).toBe(true);
	});

});

