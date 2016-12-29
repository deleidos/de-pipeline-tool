(function() {
    "use strict";

    angular.module('systemManager')
        .controller('SystemManagerController', ['$scope', '$uibModal', 'MonitorData', '$rootScope', '$websocket', 'tourSteps', SystemManagerController])
        .controller('Confirm', ['$scope', '$uibModalInstance', 'task', 'system', Confirm]);

    /**
     * @desc This is the function that represents the confirmation of deletion modal. When a system is chosen for
     * deletion, a modal is brought up asking if they're sure. If they choose OK, then the delete is initiated while
     * cancel cancels it
     * @param $scope The scope of the controller
     * @param $uibModalInstance The modal instance (itself really)
     * @param task
     * @param system
     */
    function Confirm($scope, $uibModalInstance, task, system) {
        $scope.task = task;
        $scope.system = system;
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
     * @param tourSteps Controls where the tour currently is
     */
    function SystemManagerController($scope, $uibModal, MonitorData, $rootScope, $websocket, tourSteps) {
        $scope.active = undefined;
        //Currently selected system
        $scope.activeProperties = [];
        //Currently selected system's properties. Used for formatting
        $scope.runningSystem = [];
        //The systems being deployed
        $scope.killingSystem = [];
        //The systems being killed
        $scope.onlineSystems = [];
        //Online systems. Used by builder to see if you can save a system or not.
        $scope.logPage = 1;
        //The current index of the log page (out of 4)
        $scope.errors = {};
        //Object that contains errors
        //Key is the system name
        $scope.selectedError = '';
        //The currently selected error
        $scope.monitoring = false;
        //Whether or not monitoring is turned on
        $scope.view = 'tile';
        //Sets the view of the manager
        //Either tile or detail
        $scope.searchTerm = 'name';
        //The variable used to search on
        $scope.down = true;
        //Whether or not the data-service is down
        //Asserted to true until proved otherwise
        $scope.dataStream = $websocket($rootScope.dataService);
        //Websocket used to deploy, kill, or delete systems
        var deployService = $websocket($rootScope.dataService);
        //Consumer websocket for deployment complete
        $scope.logService = $websocket($rootScope.dataService);
        //Consumer websocket for logging

        //Refreshes on message (when something's deleted, saved, or stopped)
        $scope.dataStream.onMessage(function() {
            $scope.refresh();
        });

        //Consumes response whenever a system is deployed
        deployService.send({
            "consume": "deployment_complete_notification"
        });

        //Refreshes on response
        deployService.onMessage(function() {
            $scope.refresh();
        });

        //Consumes whenever an error comes in
        $scope.logService.send({
            "consume": "log_message"
        });

        //On error, parses out the message and pushes it to $scope.errors under the corresponding system and operator
        //in the system
        $scope.logService.onMessage(function(data) {
            var messages;
            if (data.data[0] === '[' && data.data[data.data.length - 1] === ']') {
                messages = JSON.parse(data.data);
            } else {
                messages = [data.data];
            }
            angular.forEach(messages, function(message) {
                var errorMatches = /\[[a-zA-Z]+]\s[a-zA-Z0-9\-_]+\sError\sin\s[a-zA-Z0-9\-_\s]+:/g.exec(message);
                var system = null;
                if (errorMatches) {
                    system = errorMatches[0].substring(errorMatches[0].indexOf('[ERROR] ') + 8, errorMatches[0].indexOf(' Error in '));
                    if ($scope.errors[system] === null || $scope.errors[system] === undefined) {
                        $scope.errors[system] = [];
                    }
                    $scope.errors[system].push({
                        'system': system,
                        'operator': errorMatches[0].substring(errorMatches[0].indexOf(' in ') + 4, errorMatches[0].indexOf(':')),
                        'error': errorMatches[0],
                        'fullError': message
                    });
                }
                if (system && $scope.errors[system].length >= 100) {
                    $scope.errors[system].shift();
                }
            });

            console.log($scope.errors);
        });

        //Jquery calls start

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
                $('#tab-holder').height('calc(45% - 10px)');
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

        //Jquery calls end

        //Clears the selected system's logs visually
        $scope.clearLogs = function(name) {
            $scope.errors[name] = [];
            $scope.selectedError = "";
        };

        //Starts or pauses logging
        $scope.toggleLogs = function() {
            if ($scope.logService.readyState === 1) {
                $scope.logService.close();
            } else if ($scope.logService.readyState === 3) {
                $scope.logService = $websocket($rootScope.dataService);
                $scope.logService.send({
                    "consume": "log_message"
                });
            }
        };

        //Sets the currently selected error to the selected one
        $scope.selectError = function(error) {
            $scope.selectedError = error;
        };

        //Changes the log page (max of 4)
        $scope.changeLogPage = function(style) {
            if (style === 'back' && $scope.logPage > 1) {
                $scope.logPage--;
            } else if (style === 'forward' && $scope.logPage < 4) {
                $scope.logPage++;
            }
        };

        //Forces the scrollbar of log display to move to the bottom
        //Not currently used but kept because useful for tailing
        $scope.moveScrollDown = function() {
            var textarea = document.getElementById('log-display');
            textarea.scrollTop = textarea.scrollHeight;
        };

        //Forces the scrollbar of log display to move to the top
        //Not currently used but kept because useful for tailing
        $scope.moveScrollUp = function() {
            var textarea = document.getElementById('log-display');
            textarea.scrollTop = 0;
        };

        //Refreshes the manager on a broadcast
        $scope.$on('refreshManager', function() {
            $scope.refresh();
        });

        //Checks if an active is selected and saves it if it is
        //Tells monitordata to update the system list
        $scope.refresh = function() {
            if ($scope.active && $scope.active !== undefined) {
                $scope.activeUuid = $scope.active.uuid;
            }
            MonitorData.getAppList($scope.refreshSystems);
        };

        //After systems are gotten, sets the system list to be a combination of system descriptors and active systems
        //This is because system descriptors won't be in apex if they haven't been ran
        //Combines both lists to form a system list
        $scope.refreshSystems = function() {
            $scope.down = false;
            $scope.systems = MonitorData.systems;
            console.log($scope.systems);
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

            //Goes through the new system list and sets class to be used by UI
            //Checks if a running system is online or errored or if a closing system is offline
            //Creates a list of names to be sent to the system builder to check name uniqueness

	        angular.forEach($scope.systems, function(system) {
                if (system.properties.state === 'RUNNING' ||
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

                if ($scope.runningSystem.indexOf(system.uuid) > -1) {
                    if (system.class === 'online' || system.class === 'error') {
                        $scope.runningSystem.splice($scope.runningSystem.indexOf(system.uuid), 1);
                    } else {
                        system.class = 'launching';
                    }
                }
                if ($scope.killingSystem.indexOf(system.name) > -1) {
                    if (system.class === 'offline') {
                        $scope.killingSystem.splice($scope.killingSystem.indexOf(system.uuid), 1);
                    } else {
                        system.class = 'killing';
                    }
                }

                names[system.name] = system.uuid;
                $scope.$emit('Sending online systems', $scope.onlineSystems);

                system.startTime = '-';
                system.endTime = '-';
                var date = null;

                //Sets startTime and endTime manually so that invalid times will not appear

                if (system.properties.startedTime && system.properties.startedTime !== 0) {
                    date = new Date(system.properties.startedTime * 1);
                    system.startTime = "" + date.getFullYear() + '-' + ('0' + (date.getMonth() + 1).toString()).slice(-2) + '-' + ('0' + date.getDate().toString()).slice(-2) + ' ' + date.toTimeString().substr(0, 17);
                }

                if (system.properties.finishedTime && system.properties.finishedTime !== 0) {
                    date = new Date(system.properties.finishedTime * 1);
                    system.endTime = "" + date.getFullYear() + '-' + ('0' + (date.getMonth() + 1).toString()).slice(-2) + '-' + ('0' + date.getDate().toString()).slice(-2) + ' ' + date.toTimeString().substr(0, 17);
                }
                if ($scope.activeUuid && $scope.activeUuid !== undefined && $scope.activeUuid === system.uuid) {
                    $scope.setActive(system);
                }
	        });

            //If there was an active system before, goes through and selects that one
            if (!$scope.active || $scope.active === undefined) {
                var sorted = $scope.systems.sort($scope.orderManager);
                for (var i = 0; i < sorted.length; i++) {
                    if (sorted[i].class === 'online') {
                        $scope.setActive(sorted[i]);
                        i = sorted.length;
                    }
                }
            }
            delete $scope.activeUuid;

            //Sends unique names to system builder
            $scope.$emit('Unique names', names);
        };
        // This requests a list of apps from the web socket, and passes a callback
        $scope.refresh();

        // Tell the monitor which system to fetch data for when $scope.active is changed
        $scope.$watch("active", function(system) {
            if (system) {
                MonitorData.start(!$scope.monitoring || system.class !== 'online' ? "" : system._id);
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
                templateUrl: 'system-manager/confirm.html',
                controller: 'Confirm',
                size: 'sm',
                resolve: {
                    task: function() {
                        return 'deleteSystem';
                    },
                    system: function() {
                        return system;
                    }
                }
            });

            modalInstance.result.then(function() {
                $scope.systems.splice($scope.systems.indexOf(system), 1);
                if (($scope.active && $scope.active.name === system.name) || !$scope.active) {
                    $scope.active = undefined;
                    $scope.dataStream.send({
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
            $scope.runningSystem.push(system.uuid);
            system.class = 'launching';
            $scope.dataStream.send({
                "request": "deploySystem",
                "id": system.uuid
            });

            MonitorData.start(system._id);

            $scope.onlineSystems.push(system.uuid);
            $scope.$emit('Sending online systems', $scope.onlineSystems);
        };


        /**
         * @desc Kills the system
         * @param system The system that's being requested to be killed
         */
        $scope.killSystem = function(system) {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'system-manager/confirm.html',
                controller: 'Confirm',
                size: 'sm',
                resolve: {
                    task: function() {
                        return 'killSystem';
                    },
                    system: function() {
                        return system;
                    }
                }
            });

            modalInstance.result.then(function() {
                if ($scope.killingSystem.indexOf(system.name) < 0) {
                    $scope.killingSystem.push(system.name);
                    system.class = 'killing';
                    $scope.dataStream.send({
                        "request": "killSystem",
                        "id": system.uuid
                    });
                }

                if ($scope.active && system.name === $scope.active.name) {
                    MonitorData.start('');
                }

                $scope.onlineSystems.push(system.uuid);
                $scope.$emit('Sending online systems', $scope.onlineSystems);
            }, function() {

            });
        };

        //Opens the selected error into a seperate window
        $scope.openErrorWindow = function(errorText) {
            var myWindow = window.open("", "MsgWindow", "width=200,height=100");
            console.log(errorText);
            myWindow.document.write("<p>" + errorText.split('[NEW LINE]').join("<br>") + "</p>");
        };

        //Used to sort the systems
        $scope.orderManager = function(system) {
            switch (system.class) {
                case 'online':
                    return 0;
                case 'launching':
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

        //Toggles monitoring on and off but only if the system is online
        $scope.toggleMonitoring = function() {
            if ($scope.monitoring) {
                $scope.monitoring = false;
                MonitorData.start("");
            } else if (!$scope.monitoring && $scope.active.class === 'online') {
                $scope.monitoring = true;
                MonitorData.start($scope.active._id);
            }
        };

        //Tour Functions

        $scope.next = function() {
            tourSteps.next();
        };

        $scope.prev = function() {
            tourSteps.prev();
        };

        $scope.end = function() {
            tourSteps.end();
        };

        //End Tour Functions

    }

}());
