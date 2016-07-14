var myApp = angular.module('plumbApp.directives', []);

myApp.directive('jsPlumbCanvas', [function() {
   var jsPlumbZoomCanvas = function(instance, zoom, el, transformOrigin) {
       transformOrigin = transformOrigin || [0, 0];
       var p = ["webkit", "moz", "ms", "o"],
           s = "scale(" + zoom + ")",
           oString = (transformOrigin[0] * 100) + "% " + (transformOrigin[1] * 100) + "%";
       for (var i = 0; i < p.length; i++) {
           el.style[p[i] + "Transform"] = s;
           el.style[p[i] + "TransformOrigin"] = oString;
       }
       el.style["transform"] = s;
       el.style["transformOrigin"] = oString;
       instance.setZoom(zoom);
   };

    return {
       restrict: 'E',
       scope: {
           onConnection: '=onConnection',
           zoom: '=',
           x: '=',
           y: '='
       },
       controller: ['$scope', function ($scope) {
           this.scope = $scope;
       }],
       transclude: true,
       template: '<div ng-transclude></div>',
       link: function(scope, element) {

           var instance = jsPlumb.getInstance();
           scope.jsPlumbInstance = instance;

           instance.bind("connection", function(info, origEvent) {
                if (typeof origEvent !== 'undefined' && origEvent.type === 'mouseup') {
                   console.log("[connection] event in jsPlumbCanvas Directive [DRAG & DROP]", info, origEvent);
                   var targetUUID = $(info.target).attr('uuid');
                   var sourceUUID = $(info.source).attr('uuid');
                   scope.onConnection(instance, info.connection, targetUUID, sourceUUID);
                   instance.detach(info.connection);
                }
            });

           $(element).css({
               minWidth: '1000px',
               minHeight: '1000px',
               display: 'block',
               border: '5px solid grey'
           }).draggable({
               drag: function() {
                   var position = $(this).position();
                   scope.x = position.left;
                   scope.y = position.top;
                   scope.$parent.$apply();
               }
           });
           instance.setContainer($(element));
           instance.bind("beforeStartDetach", function(params) {
             if (params.endpoint.isTarget) {
              return false;
            }
           });

           var zoom = (typeof scope.zoom === 'undefined') ? 1 : scope.zoom / 100;
           jsPlumbZoomCanvas(instance, zoom, $(element)[0]);

           scope.zoomBusy = false;
           scope.zoomTime = 200; // milliseconds

           scope.$watch('zoom', function(newVal) {
              var oldVal = instance.getZoom();
              newVal = newVal / 100;
               var ds = function(x) {
	               return x * newVal / oldVal;
               };
               var cx = $(".panel-primary")[0].offsetWidth / 2;
               var cy = $(".panel-primary")[0].offsetHeight / 2;
               var xOffset = $(element).position().left - ds($(element).position().left) + ds(cx) - cx;
               var yOffset = $(element).position().top - ds($(element).position().top) + ds(cy) - cy;
               jsPlumbZoomCanvas(instance, newVal, $(element)[0]);
               $(element).offset(function(i, coords) {
					return {
						left: coords.left - xOffset,
						top: coords.top - yOffset
					};
               });
           });

           function updatePosition() {
             $(element).offset(function() {
				return {
					left: scope.x,
					top: scope.y
				};
             });
           }
           scope.$watch('x', updatePosition);
           scope.$watch('y', updatePosition);

           scope.zoomIn = function() {
             if (!scope.zoomBusy) {
               scope.zoomBusy = true;
               scope.zoom += 5;
               scope.$apply();
               setTimeout(function() {
	               scope.zoomBusy = false;
               }, scope.zoomTime);
             }
           };
           scope.zoomOut = function() {
             if (!scope.zoomBusy) {
               scope.zoomBusy = true;
               scope.zoom -= 5;
               scope.$apply();
               setTimeout(function() {
	               scope.zoomBusy = false;
               }, scope.zoomTime);
             }
           };

           $(element).bind('wheel', function(e) {
               if (e.originalEvent.deltaY < 0) {
                   if (scope.zoom < 90) {
                       scope.zoomIn();
                   }
               } else {
                   if (scope.zoom > 20) {
                       scope.zoomOut();
                   }
               }
               e.preventDefault();
           });
       }
   };
}]);

myApp.directive('jsPlumbObject', [function() {
    return {
        restrict: 'E',
        require: '^jsPlumbCanvas',
        scope: {
            stateObject: '=stateObject'
        },
        transclude: true,
        template: '<div ng-transclude></div>',
        link: function(scope, element, attrs, jsPlumbCanvas) {
            var instance = jsPlumbCanvas.scope.jsPlumbInstance;

            instance.draggable(element, {
                drag: function (event) {
                    scope.stateObject.x = event.pos[0];
                    scope.stateObject.y = event.pos[1];

                    scope.$apply();
                }
            });

            scope.$parent.$parent.$parent.dragWorkaround();

            scope.$on('$destroy', function() {

            });
        }
    };
}]);

myApp.directive('jsPlumbEndpoint', [function() {

    return {
        restrict: 'E',
        require: '^jsPlumbCanvas',
        scope: {
            settings: '=settings'
        },
        controller: ['$scope', function ($scope) {
            this.scope = $scope;
            this.connectionObjects = {};
        }],
        transclude: true,
        template: '<div ng-transclude></div>',
        link: function(scope, element, attrs, jsPlumbCanvas) {
            var instance = jsPlumbCanvas.scope.jsPlumbInstance;
            scope.jsPlumbInstance = jsPlumbCanvas.scope.jsPlumbInstance;
            scope.uuid = attrs.uuid;
            var options = {
                anchor: attrs.anchor,
                uuid: attrs.uuid
            };

            // console.log('rigging up endpoint');
            // $(element).addClass('_jsPlumb_endpoint');
            // $(element).addClass('endpoint-' + attrs.anchor);

            var ep = instance.addEndpoint(element, scope.settings, options);

            // Hide target endpoint on input operators. They must have a target endpoint,
            // otherwise connections from them do not visibly disappear when deleted o_O
            if (element.scope().$parent.$parent.stateObject.classType === "input") {
              if (ep.isTarget) {
                  ep.canvas.style.display = "none";
              }
            }

            scope.$on('$destroy', function() {
                instance.deleteEndpoint(ep);
            });
        }
    };
}]);



myApp.directive('jsPlumbConnection', ['$timeout', function($timeout) {

    return {
        restrict: 'E',
        require: '^jsPlumbEndpoint',
        scope: {
            ngClick: '&ngClick',
            ngModel: '=ngModel'
        },
        link: function(scope, element, attrs, jsPlumbEndpoint) {
            var instance = jsPlumbEndpoint.scope.jsPlumbInstance;
            var sourceUUID = jsPlumbEndpoint.scope.uuid;
            var targetUUID = scope.ngModel.uuid;

            //we delay the connections by just a small bit for loading
            console.log('[directive][jsPlumbConnection] ', scope, attrs);

            $timeout(function() {

                if (typeof jsPlumbEndpoint.connectionObjects[targetUUID] === 'undefined') {
                    jsPlumbEndpoint.connectionObjects[targetUUID] = instance.connect({
                        uuids:[
                            targetUUID,
                            sourceUUID
                        ],
                        overlays:[
                            ["Label", {label:"", id:"label"}]
                        ], editable:true});

                    console.log('[created---------][directive][jsPlumbConnection] ');

                }

                var connection = jsPlumbEndpoint.connectionObjects[targetUUID];

                connection.bind("click", function() {
                    scope.ngClick();
                    scope.$apply();
                });

                connection.bind("mouseenter", function() {
                    scope.ngModel.mouseover = true;
                    scope.$apply();
                });
                connection.bind("mouseleave", function() {
                    scope.ngModel.mouseover = false;
                    scope.$apply();
                });

                // Put a clone of the delete button on the label (centered on the line),
                // then hide the original. We can't just move the original, it angers Angular
                var overlay = connection.getOverlay("label");
                if (overlay) {
                    console.log('[getOverlay][label]', overlay, element);
                    $(element).clone(true, true).appendTo(overlay.canvas);
                    $(element).css("display", "none");
                }


            }, 300);


            scope.$on('$destroy', function() {
                console.log('jsPlumbConnection for $destroy');
                if (jsPlumbEndpoint.connectionObjects[targetUUID].source && jsPlumbEndpoint.connectionObjects[targetUUID].target) {
                    instance.detach(jsPlumbEndpoint.connectionObjects[targetUUID]);
                }
                // if the connection is destroyed, I am assuming the parent endPoint is also destroyed, and we need to remove
                // the reference that a link exists, so it will be rendered again
                jsPlumbEndpoint.connectionObjects[targetUUID] = undefined;
            });

        }
    };
}]);
