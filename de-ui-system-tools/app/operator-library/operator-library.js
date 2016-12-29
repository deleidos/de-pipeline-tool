(function() {
    "use strict";

    angular.module('operatorLibrary')
        .controller('OperatorLibraryController', ['$scope', '$uibModal', '$rootScope', '$websocket', '$timeout', '$localStorage', 'uiTourService', 'tourSteps', OperatorLibraryController])
        .controller('OperatorDescriptionController', ['$scope', '$uibModalInstance', 'description', OperatorDescriptionController]);

    /**
     *
     * @param $scope The scope tied to the controller
     * @param $uibModalInstance The modal instance
     * @param description The current description of the property selected
     * @description Controls the pop up window displayed when descriptions are edited
     */
    function OperatorDescriptionController($scope, $uibModalInstance, description) {
        $scope.description = description;
        $scope.ok = function() {
            $uibModalInstance.close($scope.description);
        };

        $scope.cancel = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }

    /**
     * @desc This function represents the controller for the operator library
     * @param $scope The scope of the controller
     * @param $uibModal The modal controls imported from angular bootstrap
     * @param $rootScope The scope of the module, used to pass down the data-service url which changes based on the url
     * @param $websocket The websocket function used to make sockets
     * @param $timeout Angulars timeout function
     * @param $localStorage Cache storage angular uses
     * @param TourService Contains information about the tour
     * @param tourSteps Used to have the tour progress or egress     *
     * @description Controller for operator-library.html. Controls the manipulation of operators
     */
    function OperatorLibraryController($scope, $uibModal, $rootScope, $websocket, $timeout, $localStorage, TourService, tourSteps) {
        $scope.opSocket = $websocket($rootScope.dataService);
        //Websocket used for grabbing and saving operators
        $scope.validationSocket = $websocket($rootScope.dataService);
        //Websocket used for grabbing and saving validations
        $scope.disableSave = false;
        //Controls if saving is possible. Disabled when a save is in progress
        $scope.searchTerm = 'name';
        //Used for searching on the operators
        $scope.useJar = false;
        //Whether or not to use a new uploaded jar or an existing one
        $scope.mappingFile = {};
        //Mapping file imported to be used in a select box
        $scope.currTab = {selected: 0};
        //The current tab opened. Used for tour to navigate without mouse presses
        $scope.operators = [];
        //An array holding all the operators
        $scope.active = {};
        //The currently selected operator
        $scope.lastActive = null;
        //The last active operator. Used on saving to add an id to a new operator
        $scope.types = [
            'input',
            'output',
            'parser',
            'enrichment',
            'mapping',
            'binaryInput',
            'binaryOutput'
        ];
        //The types of operators currently
        $scope.validations = [
            {
                'name': 'String',
                'type': 'textInput',
                'list': false,
                'regex': ".+",
                'options': null,
                'file': null
            },
            {
                'name': 'Integer',
                'type': 'textInput',
                'list': false,
                'regex': "^\\-?\\d+",
                'options': null,
                'file': null
            },
            {
                'name': 'FloatingPoint',
                'type': 'textInput',
                'list': false,
                'regex': "^\\-?\\d+(\\.\\d+)?",
                'options': null,
                'file': null
            },
            {
                'name': 'StringList',
                'type': 'textInput',
                'list': true,
                'regex': ".+",
                'options': null,
                'file': null
            },
            {
                'name': 'File',
                'type': 'fileInput',
                'list': false,
                'regex': null,
                'options': null,
                'file': ""
            }
        ];
        //The base validations available
        $localStorage.validations = $scope.validations;
        $scope.validationTypes = [
            'textInput',
            'fileInput',
            'selectBox'
        ];
        //The different types of validations

        $localStorage.customValidations = [];
        //User created validations

        //Start websocket calls

        //On successful deletion, the operator is deleted
        //Otherwise, an operator was saved so it updates that operator's id to the new id
        $scope.opSocket.onMessage(function(message) {
            if (message.data.indexOf("Deleted") > -1) {
                for (var i = 0; i < $scope.operators.length; i++) {
                    if (message.data.indexOf($scope.operators[i]._id) > -1) {
                        $scope.operators.splice(i, 1);
                    }
                }
            } else {
                $scope.lastActive._id = message.data;
            }
            $scope.$emit('Update metadata');

        });

        //Sends a request to get the current validation rules
        $scope.validationSocket.send({
            "request": "getValidationRules"
        });
        //On completion, adds them to user created validation and auto selects the first one
        $scope.validationSocket.onMessage(function(message) {
            $scope.customValidations = JSON.parse(message.data);
            if ($scope.customValidations.length > 0) {
                $scope.selectRule(0);
            }
            $localStorage.customValidations = $scope.customValidations;
            $scope.validationSocket.close();
        });

        //End websocket calls

        //JQuery calls needed to set up page properly
        //Used to set up the split page properly

        var numHolders = 0;

        angular.element('#operator-holder').ready(function() {
            numHolders++;
            if (numHolders >= 2) {
                window["Split"](['#operator-holder', '#tab-holder-operator'], {
                    direction: 'vertical',
                    sizes: [30, 60]
                });
                $('#operator-holder').height('30%');
                $('#tab-holder-operator').height('60%');
            }
        });

        angular.element('#tab-holder-operator').ready(function() {
            numHolders++;
            if (numHolders >= 2) {
                window["Split"](['#operator-holder', '#tab-holder-operator'], {
                    direction: 'vertical',
                    sizes: [30, 60]
                });
                $('#operator-holder').height('30%');
                $('#tab-holder-operator').height('60%');
            }
        });

        angular.element('#jarFileInput').ready(function() {
            $('#jarFileInput').change(function() {
                $scope.active.file = document.getElementById('jarFileInput').files[0];
                $scope.active.jarName = document.getElementById('jarFileInput').value.replace(/.*[\/\\]/, '');
                $scope.$apply();
            });
        });

        angular.element('.validation-holder-outer').ready(function() {
            $(".validation-holder-outer").parent().css('height', '100%');
        });

        //End Jquery

        //On broadcast (when builder loads the operators, sends them here to be loaded
        $scope.$on('Sending operators',function(data, operators) {
            $scope.operators = operators;
        });

        //Sets the selected operator to be active
        //Needed because angular doesn't allow variable setting in html
        $scope.setActive = function(op) {
            $scope.active = op;
        };

        //Adds a new property to an operator
        $scope.addProperty = function() {
            $scope.active.properties.push({
                'name': '',
                "displayName": "",
                "choices": null,
                "required": false,
                "type": "String",
                "description": ""
            });
        };

        //Removes a property from an operator
        $scope.removeProperty = function(index) {
            $scope.active.properties.splice(index, 1);
        };

        //Creates a new operator, does not save it however
        $scope.addOperator = function() {
            $scope.operators.push({
                'name': '',
                'displayName': '',
                'className': '',
                'type': 'input',
                'jar': null,
                'jarName': '',
                'file': null,
                'properties': [],
                '_id': null
            });
            $scope.active = $scope.operators[$scope.operators.length - 1];
            $scope.useJar = "true";
            $timeout(function() {
                var textarea = document.querySelector("#operator-holder > .system-holder > table > tbody + tbody");
                textarea.scrollTop = textarea.scrollHeight;
            }, 10);
        };

        //Deletes an operator from local memory and the database if it's there
        $scope.deleteOperator = function(index, id) {
            if (id && id !== undefined && id !== "") {
                $scope.opSocket.send({
                    "request": "deleteOperatorMetadata",
                    "id": id
                });
            } else {
                $scope.operators.splice(index, 1);
            }
            if ($scope.active._id === id) {
                if (index !== 0) {
                    $scope.setActive($scope.operators[index - 1]);
                } else {
                    $scope.active = {};
                }
            }
        };

        //Saves an operator to the database by sending over the metadata
        //TODO save individual ones rather than all of them
        $scope.saveOperator = function() {
            $scope.disableSave = true;
            var reader = new FileReader();
            reader.onload = function(e) {
                $scope.sendMetadata(e);
            };
            if ($scope.active.file && $scope.active.file !== undefined) {
                reader.readAsDataURL($scope.active.file);
            } else {
                $scope.sendMetadata(null);
            }

        };

        //Sends all operators to the database to be saved. Cannot save individual ones
        $scope.sendMetadata = function(e) {
            var props = $scope.active.properties;
            for (var i = 0; i < props.length; i++) {
                delete props[i].$$hashKey;
            }
            var jarName = document.getElementById('jarFileInput').value.replace(/.*[\/\\]/, '');
            if ($scope.active._id && (!jarName || jarName === undefined || jarName === "")) {
                jarName = $scope.active.jarName;
            }
            var metadata = {
                "name": $scope.active.name,
                "className": $scope.active.className,
                "displayName": $scope.active.displayName,
                "properties": props,
                "jarName": jarName,
                "type": $scope.active.type
            };
            if ($scope.active._id && $scope.active._id !== undefined) {
                metadata._id = $scope.active._id;
            }
            console.log(
                JSON.stringify({
                    "metadata": {
                        "metadata": metadata,
                        "bytes": e === null ? null : e.target.result.substr(e.target.result.indexOf(',') + 1)
                    },
                    "request": "saveOperatorMetadata"
                })
            );
            $scope.opSocket.send({
                "metadata": {
                    "metadata": metadata,
                    "bytes": e === null ? null : e.target.result.substr(e.target.result.indexOf(',') + 1)
                },
                "request": "saveOperatorMetadata"
            });
            $scope.lastActive = $scope.active;

            $scope.disableSave = false;
        };

        //Creates a new validation rule and auto selects it
        $scope.newRule = function() {
            $scope.customValidations.push({
                'name': 'New Rule',
                'type': 'textInput',
                'list': false,
                'regex': ".+",
                'options': null
            });
            $scope.selectRule($scope.customValidations.length - 1);
        };

        //Called when switching what kind of rule the active one is
        //Prevents non-textInputs from being lists
        $scope.configureRule = function() {
            if ($scope.activeRule.type !== 'textInput') {
                $scope.activeRule.list = false;
            }
        };

        //Sets the activeRule to the selected one
        $scope.selectRule = function(index) {
            $scope.activeRule = $scope.customValidations[index];
        };

        //Saves a rule to the database by sending over via websocket
        $scope.saveValidation = function() {
            $scope.validationSocket = $websocket($rootScope.dataService);
            var tempRule = $scope.activeRule;
            delete tempRule.$$hashKey;
            $scope.validationSocket.send({
                "request": "saveValidationRule",
                "validationRule": tempRule
            });
            $scope.validationSocket.onMessage(function (message) {
                $localStorage.customValidations = $scope.customValidations;
                $scope.activeRule._id = JSON.parse(message.data)._id;
                $scope.validationSocket.close();
            });
        };

        //Deletes a rule from the database. then deletes it from view
        $scope.deleteValidation = function(rule, index) {
            if (rule._id && rule._id !== undefined) {
                $scope.validationSocket = $websocket($rootScope.dataService);
                $scope.validationSocket.send({
                    "request": "deleteValidationRule",
                    "id": rule._id
                });
                $scope.validationSocket.onMessage(function () {
                    deleteRule(rule, index);
                    $localStorage.customValidations = $scope.customValidations;
                    $scope.validationSocket.close();
                });
            } else {
                deleteRule(rule, index);
            }
        };

        //Deletes a rule from view but only when it's deleted from the database
        function deleteRule(rule, index) {
            $scope.customValidations.splice(index, 1);
            if (rule === $scope.activeRule) {
                if ($scope.customValidations.length < 1) {
                    $scope.activeRule = {};
                } else if (index >= $scope.customValidations.length) {
                    $scope.activeRule = $scope.customValidations[index - 1];
                } else {
                    $scope.activeRule = $scope.customValidations[index];
                }
            }
        }

        //Adds an option for a select box rule, but only when options are available (when it's a select box)
        $scope.addOption = function() {
            if (!$scope.activeRule.options || $scope.activeRule.options === undefined) {
                $scope.activeRule.options = [];
            }
            $scope.activeRule.options.push("");
        };

        //Deletes the selected option from a select box
        $scope.deleteOption = function($index) {
            $scope.activeRule.options.splice($index, 1);
        };

        //Adds schema mapping keys as options for a select box
        //Uses fileReader to get the value, parses it, then grabs the keys returned
        $scope.addMapping = function() {
            if ($scope.mappingFile.value && $scope.mappingFile.value !== undefined) {
                $scope.activeRule.options = Object.keys(JSON.parse(window.atob($scope.mappingFile.value.substr($scope.mappingFile.value.indexOf(',') + 1))));
            }
        };

        //Pulls up a dialog box that allows you to edit a property's description
        //On Okay, updates it
        //On Cancel, doesn't upadte
        $scope.editDescription = function(active, index) {
            var modalInstance = $uibModal.open({
                animation: true,
                templateUrl: 'operator-library/operator-description.html',
                controller: 'OperatorDescriptionController',
                size: 'sm',
                resolve: {
                    description: function() {
                        return active.properties[index].description;
                    }
                }
            });

            modalInstance.result.then(function(description) {
                active.properties[index].description = description;
            }, function() {

            });
        };

        //Tour Functions

        $scope.next = function() {
            tourSteps.next();
            if (TourService.getTourByName('tour').getCurrentStep().order === 190) {
                $scope.currTab.selected = 0;
            } else if (TourService.getTourByName('tour').getCurrentStep().order === 225) {
                $scope.currTab.selected = 1;
            }
        };

        $scope.prev = function() {
            tourSteps.prev();
            if (TourService.getTourByName('tour').getCurrentStep().order === 230) {
                $scope.currTab.selected = 0;
            } else if (TourService.getTourByName('tour').getCurrentStep().order === 250) {
                $scope.currTab.selected = 1;
            }
        };

        $scope.end = function() {
            tourSteps.end();
        };

        //End Tour Functions
    }

}());
