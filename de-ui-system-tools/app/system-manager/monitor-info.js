(function() {

    'use strict';

    angular.module('systemManager')
        .factory('MonitorData', ['$rootScope', '$websocket', MonitorData])
        .controller('ChartController', ['$scope', 'MonitorData', ChartController]);

    function MonitorData($rootScope, $websocket) {
        var ws = $websocket($rootScope.dataService);
        var descNames = [];
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
                ret.processedData[ret.tupleLabels.length - 1] = (o.stats.tuples_processed_psma);
                ret.emittedData[ret.tupleLabels.length - 1] = (o.stats.tuples_emitted_psma);
            }
            // CpuUsage response
            if (o.operators) {
                ret.cpuData.fill(0);

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
                if (config.refresh) {
                    config.refresh = false;
                    config.refreshFunction();
                } else {
                    config.refresh = true;
                }
            }
            // AppList response
            if (o.apps) {
                ret.systems = [];
                var nameMap = {};
                angular.forEach(o.apps, function(a) {
                    if (!nameMap[a.name] || nameMap[a.name] < a.started_time) {
                        nameMap[a.name] = a.started_time;
                    }
                });
                angular.forEach(o.apps, function(a) {
	                if ((descNames.indexOf(a.name) > -1 || a.state === "RUNNING") && a.started_time === nameMap[a.name]) {
		                var x = {};
		                x.name = a.name;
		                x.online = a.state === "RUNNING";
		                x.error = a.state === "FAILED";
		                x.updateTime = (d.getUTCHours() < 10 ? "0" + d.getUTCHours() : d.getUTCHours()) + ":" + (d.getUTCMinutes() < 10 ? "0" + d.getUTCMinutes() : d.getUTCMinutes()) + ":" + (d.getUTCSeconds() < 10 ? "0" + d.getUTCSeconds() : d.getUTCSeconds());
		                x.updateDate = (d.getUTCFullYear()) + "-" + ((d.getUTCMonth() < 9 ? "0" : "") + (d.getUTCMonth() + 1)) + "-" + ((d.getUTCDate() < 10 ? "0" : "") + d.getUTCDate());
		                var ft = new Date(a.finished_time - 0); // Make sure the value isn't a string
		                x.offTime = (ft.getUTCHours() < 10 ? "0" + ft.getUTCHours() : ft.getUTCHours()) + ":" + (ft.getUTCMinutes() < 10 ? "0" + ft.getUTCMinutes() : ft.getUTCMinutes()) + ":" + (ft.getUTCSeconds() < 10 ? "0" + ft.getUTCSeconds() : ft.getUTCSeconds());
		                x.offDate = (ft.getUTCFullYear()) + "-" + ((ft.getUTCMonth() < 9 ? "0" : "") + (ft.getUTCMonth() + 1)) + "-" + ((ft.getUTCDate() < 10 ? "0" : "") + ft.getUTCDate());
		                var st = new Date(a.started_time - 0);
		                x.onTime = (st.getUTCHours() < 10 ? "0" + st.getUTCHours() : st.getUTCHours()) + ":" + (st.getUTCMinutes() < 10 ? "0" + st.getUTCMinutes() : st.getUTCMinutes()) + ":" + (st.getUTCSeconds() < 10 ? "0" + st.getUTCSeconds() : st.getUTCSeconds());
		                x.onDate = (st.getUTCFullYear()) + "-" + ((st.getUTCMonth() < 9 ? "0" : "") + (st.getUTCMonth() + 1)) + "-" + ((st.getUTCDate() < 10 ? "0" : "") + st.getUTCDate());
		                x.downTime = x.offTime; // There is no way to distinguish these values
		                x.downDate = x.offDate;
		                x._id = a.id;
		                x.state = a.state === "RUNNING" ? "online" : a.state === "PENDING" || a.state === "FINISHED" || a.state === "KILLED" ? "idle" : a.state === "FAILED" ? "error" : "warning";
		                x.properties = {
			                state: a.state,
			                startedTime: a.started_time,
			                finishedTime: a.finished_time,
			                finalStatus: a.final_status,
			                progress: a.progress,
			                applicationType: a.application_type,
			                queue: a.queue
		                };
		                ret.systems.push(x);
	                }
                });
                if (config.refresh) {
                    config.refresh = false;
                    config.refreshFunction();
                } else {
                    config.refresh = true;
                }
            }

            if (!o.apps) {
                ws.send(JSON.stringify({
                    request: "getAppList"
                }));
            }
        });
        var config = {
            tupleEntries: 20,
            intervalIds: [],
            refreshFunction: function() {},
            refresh: false
        };
        var ret = {
            processedData: new Array(config.tupleEntries),
            emittedData: new Array(config.tupleEntries),
            tupleLabels: new Array(config.tupleEntries),
            cpuData: [],
            cpuLabels: [],
            systems: [], // At the moment, this is where we transition to calling running applications "Systems"
            descriptors: [],
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
                ret.tupleLabels.fill("");
                while (ret.cpuLabels.length > 0 && ret.cpuLabels[0] === undefined) {
                    ret.cpuLabels.shift();
                    ret.cpuData.shift();
                }
                // Begin requesting data from web socket
                if (appId !== "") {
                    ret.getDetails(appId);
                    ret.getCpuUsage(appId);
                    config.intervalIds.push(setInterval(ret.getDetails, 3000, appId));
                    config.intervalIds.push(setInterval(ret.getCpuUsage, 10000, appId));
                }
            }
        };
        ret.tupleLabels.fill("");
        ws.onOpen(function() {});
        ws.onClose(function() {
            //dataStream = $websocket('ws://localhost:8080/analytics');
        });
        ws.onError(function() {
            //dataStream = $websocket('ws://localhost:8080/analytics');
        });

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
                animation: false,
                legend: {
                    display: true
                },
                scales: {
                  yAxes: [{
                    ticks: {
                      stepSize: 1
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
                legend: {
                    display: true
                }
            }
        }];
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
