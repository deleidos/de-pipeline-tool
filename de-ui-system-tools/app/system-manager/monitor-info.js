(function() {

    'use strict';

    angular.module('systemManager')
        .factory('MonitorData', ['$rootScope', '$websocket', MonitorData])
        .controller('ChartController', ['$scope', 'MonitorData', ChartController]);

    function MonitorData($rootScope, $websocket) {
        var ws = $websocket($rootScope.dataService);
        var descNames = [];
        var getAppListSocket = $websocket($rootScope.dataService);
        getAppListSocket.onMessage(function(message) {
            var o = JSON.parse(message.data);
            ret.systems = [];
            var nameMap = {};
            angular.forEach(o, function(a) {
                if (!nameMap[a.name] || nameMap[a.name] < a.startedTime) {
                    nameMap[a.name] = a.startedTime;
                }
            });
            angular.forEach(o, function(a) {
                if ((descNames.indexOf(a.name) > -1 || a.state === "RUNNING") && a.startedTime === nameMap[a.name]) {
                    var x = {};
                    x.name = a.name;
                    x.online = a.state === "RUNNING";
                    x.error = a.state === "FAILED";
                    x._id = a.id;
                    x.state = a.state === "RUNNING" ? "online" : a.state === "PENDING" || a.state === "FINISHED" || a.state === "KILLED" ? "idle" : a.state === "FAILED" ? "error" : "warning";
                    x.properties = {
                        name: a.name,
                        state: a.state,
                        startedTime: a.startedTime,
                        finishedTime: a.finishedTime
                    };
                    ret.systems.push(x);
                }
            });

            config.refreshFunction();
        });


        ws.onMessage(function(message) {
          var o = JSON.parse(message.data);
	        var d = new Date();
            // Details response
            if (o.stats) {
                if (ret.tupleLabels.length >= config.tupleEntries) {
                    for (var i = 0; i < ret.tupleLabels.length - 1; i++) {
                        ret.processedData[i] = ret.processedData[i + 1];
                        ret.emittedData[i] = ret.emittedData[i + 1];
                        ret.tupleLabels[i] = ret.tupleLabels[i + 1];
                    }
                }
                // Current time in MM:SS format
                ret.tupleLabels[ret.tupleLabels.length - 1] = ((d.getUTCMinutes() < 10 ? "0" + d.getUTCMinutes() : d.getUTCMinutes()) + ":" + (d.getUTCSeconds() < 10 ? "0" + d.getUTCSeconds() : d.getUTCSeconds()));
                ret.processedData[ret.tupleLabels.length - 1] = (o.stats.tuplesProcessedPSMA);
                ret.emittedData[ret.tupleLabels.length - 1] = (o.stats.tuplesEmittedPSMA);
                console.log(ret);
            }
            // CpuUsage response
            if (o.operators) {
                fill(ret.cpuData, 0);

                angular.forEach(o.operators, function(x) {
                    var i = ret.cpuLabels.indexOf(x.name);
                    if (i >= 0) {
	                    ret.cpuData[i] = x.cpuPercentageMA;
                    } else {
	                    ret.cpuLabels.push(x.name);
	                    ret.cpuData.push(x.cpuPercentageMA);
                    }
                });
            }

            // SystemDescriptors response
            if (Array.isArray(o) && o.length > 0 && o[0]._id) {
                ret.descriptors = [];
                angular.forEach(o, function(a) {
                    descNames.push(a.name);
                    ret.descriptors.push({
                        name: a.name,
                        uuid: a._id
                    });
                });
                getAppListSocket.send(JSON.stringify({
                    request: "getAppList"
                }));
                config.refreshFunction();
            }

            // Log List response
            else if (Array.isArray(o) && !o[0].clusterId) {
                ret.logNames = o;
            }

        });

        var config = {
            tupleEntries: 20,
            intervalIds: [],
            refreshFunction: function() {}
        };
        function fill(array, value) {
            for (var i = 0; i < array.length; i++) {
                array[i] = value;
            }
        }
        var ret = {
            appId: "",
            processedData: new Array(config.tupleEntries),
            emittedData: new Array(config.tupleEntries),
            tupleLabels: new Array(config.tupleEntries),
            cpuData: [],
            cpuLabels: [],
            systems: [], // At the moment, this is where we transition to calling running applications "Systems"
            descriptors: [],
            logNames: [],
            stramEvents: {},
            getDetails: function(appId) {
                ws.send(JSON.stringify({
                    request: "getAppDetails",
                    id: appId
                }));
            },
            getCpuUsage: function(appId) {
                ws.send(JSON.stringify({
                    request: "getAppCpuUsage",
                    id: appId
                }));
            },
            getAppList: function(fn) {
                config.refreshFunction = fn || function() {};
                ws.send(JSON.stringify({
                    request: "getSystemDescriptors"
                }));
            },
            start: function(appId) {
                // Stop requesting data from the web socket
                while (config.intervalIds.length > 0) {
                    clearInterval(config.intervalIds.pop());
                }
                // Clear all data
                angular.forEach(ret, function(val) {
                    if (Array.isArray(val) && val.length > 0) {
                        if (val[0] && val[0].name) {} // Don't clear out systems or descriptors
                        else {
                            var len = val.length;
                            while (val.length > 0) {
                                val.pop();
                            }
                            while (len >= 0) {
                                val.push(undefined);
                                len--;
                            }
                        }
                    }
                });
                fill(ret.tupleLabels.fill, "");
                while (ret.cpuLabels.length > 0 && ret.cpuLabels[0] === undefined) {
                    ret.cpuLabels.shift();
                    ret.cpuData.shift();
                }
                // Begin requesting data from web socket
                if (appId !== "") {
                    ret.getDetails(appId);
                    ret.getCpuUsage(appId);

                    config.intervalIds.push(setInterval(ret.getDetails, 10000, appId));
                    config.intervalIds.push(setInterval(ret.getCpuUsage, 30000, appId));
                }
                ret.appId = appId;
            }
        };
        fill(ret.tupleLabels, "");
        return ret;
    }

    function ChartController($scope, MonitorData) {
        $scope.charts = [{
            id: "tupleChart",
            data: [MonitorData.processedData, MonitorData.emittedData],
            labels: MonitorData.tupleLabels,
            series: ["Processed Tuples PSMA", "Emitted Tuples PSMA"],
            colors: ["#9ebeca", "#666666"],
            options: {
                maintainAspectRatio: false,
                animation: false,
                legend: {
                    display: true
                },
                scales: {
                  yAxes: [{
                    ticks: {
                    }
                  }]
                },
                pointRadius: 0
            }
        }, {
            id: "cpuChart",
            data: MonitorData.cpuData,
            labels: MonitorData.cpuLabels,
            options: {
                maintainAspectRatio: false,
                legend: {
                    display: true
                }
            }
        }];

        //TODO Parse through cpuData and limit sig figs
        angular.forEach($scope.charts, function(x) {
            $scope[String(x.id + "Data")] = x.data;
            $scope[String(x.id + "Labels")] = x.labels;
            $scope[String(x.id + "Series")] = x.series;
            $scope[String(x.id + "Legend")] = x.legend;
            $scope[String(x.id + "Colors")] = x.colors;
            $scope[String(x.id + "Options")] = x.options;
        });
    }
}());
