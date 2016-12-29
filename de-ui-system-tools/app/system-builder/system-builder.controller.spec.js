describe('SystemBuilderController', function () {
	var testScope, testSocket;
	var fakeInput = {
        "_id": "744d6597-109b-4394-88c6-4d4792b999fb",
        "name": "S3InputOperat",
        "className": "com.deleidos.framework.operators.s3.S3InputOperator",
        "displayName": "S3 Input",
        "jarName": "de-operator-s3-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
        "type": "input",
        "properties": [{
            "name": "bucketName",
            "displayName": null,
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:786",
            "value": "asdad"
        }, {
            "name": "path",
            "displayName": null,
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:787",
            "value": "asda"
        }, {
            "name": "accessKey",
            "displayName": null,
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:788",
            "value": "sdsadas"
        }, {
            "name": "secretKey",
            "displayName": null,
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:789",
            "value": "dasd"
        }, {
            "name": "endpoint",
            "displayName": null,
            "choices": null,
            "required": false,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:790",
            "value": "asdas"
        }, {
            "name": "splitter",
            "displayName": null,
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:791",
            "value": "asdasd"
        }, {
            "name": "headerRows",
            "displayName": null,
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:792",
            "value": "asda"
        }],
        "color": "#9b2743",
        "sources": [{
            "uuid": 2187,
            "$$hashKey": "object:778",
            "connections": [{
                "uuid": "2188",
                "$$hashKey": "object:968"
            }]
        }],
        "x": 26917,
        "y": 26948,
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
        "_id": "ad607609-be3e-45c2-8cc5-b18056ff555b",
        "name": "MongoDbOutput",
        "className": "com.deleidos.framework.operators.mongodb.MongoDbOutputOperator",
        "displayName": "MongoDB Output",
        "jarName": "de-operator-mongodb-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
        "type": "output",
        "properties": [{
            "name": "hostName",
            "displayName": "Host Name",
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:889",
            "value": "asda"
        }, {
            "name": "hostPort",
            "displayName": "Port",
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:890",
            "value": "dasda"
        }, {
            "name": "database",
            "displayName": "Database",
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:891",
            "value": "sdasda"
        }, {
            "name": "collection",
            "displayName": "Collection",
            "choices": null,
            "required": true,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:892",
            "value": "sdasda"
        }, {
            "name": "userName",
            "displayName": "Username",
            "choices": null,
            "required": false,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:893",
            "value": "dasda"
        }, {
            "name": "password",
            "displayName": "Password",
            "choices": null,
            "required": false,
            "type": "String",
            "validation": {
                "name": "String",
                "type": "textInput",
                "list": false,
                "regex": ".+",
                "options": null,
                "file": null,
                "$$hashKey": "object:373"
            },
            "$$hashKey": "object:894",
            "value": "asda"
        }],
        "color": "#00778b",
        "targets": [{
            "uuid": 2188,
            "$$hashKey": "object:881"
        }],
        "x": 27640,
        "y": 27072,
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
            "collection": {
                "$invalid": false
            },
            "userName": {
                "$invalid": true
            },
            "password": {
                "$invalid": true
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

