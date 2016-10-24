(function() {
    "use strict";

    angular.module('systemManager')
        .controller('SystemManagerController', ['$scope', '$uibModal', 'MonitorData', '$rootScope', '$websocket', '$timeout', 'uiTourService', 'tourSteps', SystemManagerController])
        .controller('ConfirmDelete', ['$scope', '$uibModalInstance', ConfirmDelete]);

    /**
     * @desc This is the function that represents the confirmation of deletion modal. When a system is chosen for
     * deletion, a modal is brought up asking if they're sure. If they choose OK, then the delete is initiated while
     * cancel cancels it
     * @param $scope The scope of the controller
     * @param $uibModalInstance The modal instance (itself really)
     */
    function ConfirmDelete($scope, $uibModalInstance) {
        $scope.ok = function() {
            $uibModalInstance.close();
        };

        $scope.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }

    /**
     * @desc This function represents the controller for the system manager
     * @param $scope The scope of the controller
     * @param $uibModal The angular modal function
     * @param MonitorData
     * @param $rootScope The scope of the module, used to pass down the data-service url which changes based on the url
     * @param $websocket The websocket function used to make sockets
     * @param $timeout Timer function
     */
    function SystemManagerController($scope, $uibModal, MonitorData, $rootScope, $websocket, $timeout, TourService, tourSteps) {

        //Contains the currently active system
        $scope.active = undefined;
        $scope.activeProperties = [];

        $scope.runningSystem = [];
        $scope.killingSystem = [];

        $scope.onlineSystems = [];

        $scope.logs = ['log1', 'log2', 'log3', 'log4'];
        $scope.currLog = null;
        $scope.errors = {};
        $scope.prevName = '';
        $scope.selectedError = '';
        $scope.selectError = function(error) {
            $scope.selectedError = error;
        };
        $scope.setLog = function(log) {
            $scope.currLog = log;
        };

        $scope.view = 'tile';

        $scope.searchTerm = 'name';

        $scope.logService = $websocket($rootScope.dataService);

        $scope.logService.send({
            "consume": "log_message"
        });
        $scope.logService.onMessage(function(message) {
            //console.log('LOGS GOING HERE');
            console.log(message.data);
            var matches = /\[ERROR]\s\S+/g.exec(message.data);
            if (matches && matches.length > 0) {
                $scope.prevName = matches[0].substr(8);
                if (!$scope.errors[$scope.prevName]) {
                    $scope.errors[$scope.prevName] = [];
                }
                $scope.errors[$scope.prevName].push([]);
            }

            $scope.errors[$scope.prevName][$scope.errors[$scope.prevName].length - 1].push(message.data);
            console.log($scope.errors);

        });

        $scope.clearLogs = function() {
            $scope.errors[$scope.active.name] = [];
        };

        $scope.toggleLogs = function() {
            if ($scope.logService.readyState === 1) {
                $scope.logService.close();
            } else if ($scope.logService.readyState === 3 || $scope.logService.readyState === 0) {
                $scope.logService = $websocket($rootScope.dataService);
                $scope.logService.send({
                    "consume": "log_message"
                });
            }
        };

        angular.element('.log-holder-outer').ready(function() {
            $(".log-holder-outer").parent().css('height', '100%');
        });


        angular.element('.system-top-row').ready(function() {
            $(".system-top-row").parent().css('display', 'flex');
        });

        angular.element(".chart-holder").ready(function() {
            $(".chart-holder").parent().parent().css('height', '100%');
        });

        angular.element(".error-table-holder").ready(function() {
            $(".error-table-holder").parent().css('height', '100%');
            $(".error-table-holder").parent().parent().css('height', '100%');
        });

        angular.element(".error-header").ready(function() {
            $(".error-header").parent().css('padding-right', '10px');
        });

        var numHolders = 0;

        angular.element('#system-holder').ready(function() {
            numHolders++;
            if (numHolders >= 2) {
                window["Split"](['#system-holder', '#tab-holder'], {
                    direction: 'vertical',
                    sizes: [45, 45]
                });
                $('#system-holder').height('45%');
                $('#tab-holder').height('45%');
            }
        });

        angular.element('#tab-holder').ready(function() {
            numHolders++;
            if (numHolders >= 2) {
                window["Split"](['#system-holder', '#tab-holder'], {
                    direction: 'vertical',
                    sizes: [45, 45]
                });
                $('#system-holder').height('45%');
                $('#tab-holder').height('calc(45% - 10px)');
            }
        });


        $scope.saveLog = function() {
            var pom = document.createElement('a');
            pom.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent($scope.currLog));
            pom.setAttribute('download', 'Log.txt');

            if (document.createEvent) {
                var event = document.createEvent('MouseEvents');
                event.initEvent('click', true, true);
                pom.dispatchEvent(event);
            } else {
                pom.click();
            }
        };
        $scope.moveScrollDown = function() {
            var textarea = document.getElementById('log-display');
            textarea.scrollTop = textarea.scrollHeight;
        };
        $scope.moveScrollUp = function() {
            var textarea = document.getElementById('log-display');
            textarea.scrollTop = 0;
        };

        $scope.down = true;
        var dataStream = $websocket($rootScope.dataService);
        var deployService = $websocket($rootScope.dataService);
        var deployed = false;
        deployService.send({
            "consume": "deployment_complete_notification"
        });
        deployService.onMessage(function(message) {
            deployed = true;
            $scope.runningSystem.splice($scope.runningSystem.indexOf(JSON.parse(message.data).id), 1);
            MonitorData.getAppList($scope.refreshSystems);
        });
        dataStream.onMessage(function(message) {
            //add launched check and timeout
            if (message.data.indexOf('System Killed ') > -1) {
                $scope.killingSystem.splice(message.data.substring(message.data.indexOf('System Killed ') + 14), 1);
                MonitorData.getAppList($scope.refreshSystems);
            }
        });

        $scope.$on('refreshManager', function() {
            MonitorData.getAppList($scope.refreshSystems);
        });

        // Real System population
        $scope.refreshSystems = function() {
            $scope.down = false;
            $scope.systems = MonitorData.systems;
            angular.forEach(MonitorData.descriptors, function(a) {
                var present = false;
                for (var i = 0; i < $scope.systems.length; i++) {
                    if ($scope.systems[i].name === a.name) {
                        $scope.systems[i].uuid = a.uuid;
                        present = true;
                        i = $scope.systems.length;
                    }
                }
                if (!present) {
                    $scope.systems.push({
                        name: a.name,
                        uuid: a.uuid,
                        state: "offline",
                        properties: {
                            applicationType: 'DigitalEdge'
                        },
                        application: a.application
                    });
                }
            });

            var names = {};
            $scope.onlineSystems = [];

	        angular.forEach($scope.systems, function(system) {
                if ($scope.runningSystem.indexOf(system.uuid) > -1) {
                    system.class = 'running';
                    $scope.onlineSystems.push(system.uuid);
                } else if ($scope.killingSystem.indexOf(system.name) > -1) {
                    system.class = 'killing';
                    $scope.onlineSystems.push(system.uuid);
                } else if (system.properties.state === 'RUNNING' ||
			        system.properties.state === 'ACCEPTED' ||
			        system.properties.state === 'SUBMITTED') {
			        system.class = 'online';
                    $scope.onlineSystems.push(system.uuid);
		        } else if (system.properties.state === 'NEW' ||
			        system.properties.state === 'NEW_SAVING') {
			        system.class = 'new';
		        } else if (system.properties.state === 'FAILED') {
					system.class = 'error';
		        } else {
			        system.class = 'offline';
		        }
                names[system.name] = system.uuid;
                $scope.$emit('Sending online systems', $scope.onlineSystems);

                system.startTime = '-';
                system.endTime = '-';
                var date = null;

                if (system.properties.startedTime && system.properties.startedTime !== 0) {
                    date = new Date(system.properties.startedTime * 1);
                    system.startTime = "" + date.getFullYear() + '-' + ('0' + (date.getMonth() + 1).toString()).slice(-2) + '-' + ('0' + date.getDate().toString()).slice(-2) + ' ' + date.toTimeString().substr(0, 17);
                }

                if (system.properties.finishedTime && system.properties.finishedTime !== 0) {
                    date = new Date(system.properties.finishedTime * 1);
                    system.endTime = "" + date.getFullYear() + '-' + ('0' + (date.getMonth() + 1).toString()).slice(-2) + '-' + ('0' + date.getDate().toString()).slice(-2) + ' ' + date.toTimeString().substr(0, 17);
                }
	        });
            $scope.$emit('Unique names', names);
        };
        // This requests a list of apps from the web socket, and passes a callback
        MonitorData.getAppList($scope.refreshSystems);

        // Tell the monitor which system to fetch data for when $scope.active is changed
        $scope.$watch("active", function(system) {
            if (system) {
                MonitorData.start(system.class !== 'online' && system.class !== 'running' ? "" : system._id);
                $scope.selectedError = "";
                if (system.properties) {
                    var prevI = 0;
                    var i = 0;
                    $scope.activeProperties = [{}];
                    angular.forEach(system.properties, function (value, key) {
                        if (Math.floor(i / 8) !== Math.floor(prevI / 8)) {
                            $scope.activeProperties.push({});
                        }
                        $scope.activeProperties[Math.floor(i / 8)][key] = value;
                        prevI = i;
                        i++;
                    });
                }
            }
        });

        /**
         * @desc Changes the given system to be active and changes the state to 1
         * @param system The system being set to active
         */
        $scope.setActive = function(system) {
            $scope.active = system;
        };

        /**
         * @desc Sends the given id to the system builder, ultimately displaying said system
         * @param id A system id given to the function to send
         */
        $scope.sendId = function(id) {
	        if (id !== undefined) {
                console.log(id);
		        $scope.$emit('Builder ID', id);
	        }
        };

        /**
         * @desc Used for deleting a system. Will actually create a modal to confirm if the user wants to delete the
         * system. If so, it returns to this function and calls the necessary request on the websocket for deletion
         * @param system The system being deleted
         */
        $scope.deleteSystem = function(system) {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'system-manager/confirm-delete.html',
                controller: 'ConfirmDelete',
                size: 'sm',
                resolve: {}
            });

            modalInstance.result.then(function() {
                $scope.systems.splice($scope.systems.indexOf(system), 1);
                if (($scope.active && $scope.active.name === system.name) || !$scope.active) {
                    $scope.active = undefined;
                    dataStream.send({
                        "request": "deleteSystem",
                        "id": system.uuid
                    });
                }
            }, function() {

            });
        };


        /**
         * @desc Toggles the given system between being online and offline using the necessary requests to the websocket
         * @param system The system that's being requested to be toggled
         */
        $scope.toggleSystem = function(system) {
            /*if (system.state === 'online' && $scope.killingSystem.indexOf(system.name) < 0) {
                $scope.killingSystem.push(system.name);
                system.class = 'killing';
                dataStream.send({
                    "request": "stopSystem",
                    "id": system.uuid
                });
            } else if ($scope.runningSystem.indexOf(system.name) < 0) {
                */$scope.runningSystem.push(system.uuid);
                system.class = 'running';
                dataStream.send({
                    "request": "deploySystem",
                    "id": system.uuid
                });

                MonitorData.start(system._id);
            //}

            $scope.onlineSystems.push(system.uuid);
            $scope.$emit('Sending online systems', $scope.onlineSystems);
        };


        /**
         * @desc Kills the system
         * @param system The system that's being requested to be killed
         */
        $scope.killSystem = function(system) {
            if ($scope.killingSystem.indexOf(system.name) < 0) {
                $scope.killingSystem.push(system.name);
                system.class = 'killing';
                dataStream.send({
                    "request": "killSystem",
                    "id": system.uuid
                });
            }

            if ($scope.active && system.name === $scope.active.name) {
                MonitorData.start('');
            }

            $scope.onlineSystems.push(system.uuid);
            $scope.$emit('Sending online systems', $scope.onlineSystems);
        };

        $scope.orderManager = function(system) {
            switch (system.class) {
                case 'online':
                    return 0;
                case 'running':
                    return 1;
                case 'killing':
                    return 2;
                case 'error':
                    return 3;
                case 'new':
                    return 4;
                default:
                    return 5;
            }
        };

        $scope.next = function() {

            tourSteps.next();
            if (TourService.getTourByName('tour').getCurrentStep().order === 120) {
                $scope.forcedFlip = true;
                console.log("flip");
            }
            if (TourService.getTourByName('tour').getCurrentStep().order === 130) {
                $scope.forcedFlip = false;
                console.log("flop");
                $scope.setActive($scope.systems[0]);
            }
            if (TourService.getTourByName('tour').getCurrentStep().order === 140) {
                console.log("flop");
                if ($scope.active.state !== 'online') {
                    $scope.toggleSystem($scope.systems[0]);
                }
            }
            console.log("manager next");
        };

        $scope.prev = function() {
            tourSteps.prev();
            if (TourService.getTourByName('tour').getCurrentStep().order === 140) {
                $scope.forcedFlip = false;
                console.log("flop");
                $scope.setActive($scope.systems[0]);
            }
            if (TourService.getTourByName('tour').getCurrentStep().order === 150) {
                console.log("flop");
                if ($scope.active.state !== 'online') {
                    $scope.toggleSystem($scope.systems[0]);
                }
            }
        };

        $scope.end = function() {
            tourSteps.end();
        };

    }

}());
