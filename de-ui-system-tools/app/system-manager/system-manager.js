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

        //0 = no info display, 1 = large info display and minimized charts besides the selected ones
        $scope.state = 0;

        //Contains the currently active system
        $scope.active = undefined;

        $scope.runningSystem = [];
        $scope.killingSystem = [];

        $scope.down = true;
        var dataStream = $websocket($rootScope.dataService);
        var deployService = $websocket($rootScope.dataService);
        deployService.send({
            "consume": "deployment_complete_notification"
        });
        deployService.onMessage(function(message) {
            $scope.runningSystem.splice($scope.runningSystem.indexOf(JSON.parse(message.data).id), 1);
            MonitorData.getAppList($scope.refreshSystems);
        });
        dataStream.onMessage(function(message) {
            //add launched check and timeout
            if (message.data.indexOf('System Killed ') > -1) {
                $scope.killingSystem.splice(message.data.substring(message.data.indexOf('System Killed ') + 14), 1);
            }
            MonitorData.getAppList($scope.refreshSystems);
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

	        angular.forEach($scope.systems, function(system) {
                if ($scope.runningSystem.indexOf(system.uuid) > -1) {
                    system.class = 'running';
                } else if ($scope.killingSystem.indexOf(system.name) > -1) {
                    system.class = 'killing';
                } else if (system.properties.state === 'RUNNING' ||
			        system.properties.state === 'ACCEPTED' ||
			        system.properties.state === 'SUBMITTED') {
			        system.class = 'online';
		        } else if (system.properties.state === 'NEW' ||
			        system.properties.state === 'NEW_SAVING') {
			        system.class = 'new';
		        } else if (system.properties.state === 'FAILED') {
					system.class = 'error';
		        } else {
			        system.class = 'offline';
		        }
                names[system.name] = system.uuid;
	        });
            $scope.$emit('Unique names', names);
        };
        // This requests a list of apps from the web socket, and passes a callback
        MonitorData.getAppList($scope.refreshSystems);

        // Tell the monitor which system to fetch data for when $scope.active is changed
        $scope.$watch("active", function(system) {
            MonitorData.start(system === undefined ? "" : system._id);
        });

        /**
         * @desc Changes the given system to be active and changes the state to 1
         * @param system The system being set to active
         */
        $scope.setActive = function(system) {
            $scope.state = 1;
            $scope.active = system;
            $(".flip-container").children(".system-inner").css('display', '');
            $(".flipper").css('display', 'none');
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
         * @desc Toggles the current system-manager state. If it's 1, it goes to 0, otherwise, it goes to 1
         */
        $scope.toggleState = function() {
	        if ($scope.state === 1) {
		        $scope.state = 0;
                $(".flip-container").children(".system-inner").css('display', 'none');
                $(".flipper").css('display', '');
	        } else {
		        $scope.state = 1;
                $(".flip-container").children(".system-inner").css('display', '');
                $(".flipper").css('display', 'none');
	        }
            $scope.active = undefined;
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
                    $scope.state = 0;
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
            console.log(system);
            if (system.state === 'online' && $scope.killingSystem.indexOf(system.name) < 0) {
                $scope.killingSystem.push(system.name);
                system.class = 'killing';
                dataStream.send({
                    "request": "stopSystem",
                    "id": system.uuid
                });
            } else if ($scope.runningSystem.indexOf(system.name) < 0) {
                $scope.runningSystem.push(system.uuid);
                system.class = 'running';
                dataStream.send({
                    "request": "deploySystem",
                    "id": system.uuid
                });
            }
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
        };

        $scope.orderManager = function(system) {
            if (system.state === 'online') {
                return 0;
            } else if (system.state === 'error') {
                return 1;
            } else if (system.state === 'new') {
                return 2;
            } else {
                return 3;
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
