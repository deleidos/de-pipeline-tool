(function() {
    angular.module('systemBuilder').directive('appFilereader', ['$q', appFilereader]);
    angular.module('operatorLibrary').directive('appFilereader', ['$q', appFilereader]);

    /**
     *
     * @param $q
     * @returns {{restrict: string, require: string, link: link}}
     * @description Used for reading and obtaining files from an input field. Sets the value of what you pull in to the
     * file contents as a base64. Sets ngnameVal to the filename
     */
	function appFilereader($q) {
		var slice = Array.prototype.slice;

		return {
			restrict: 'A',
			require: '?ngModel',
			link: function (scope, element, attrs, ngModel) {
				if (!ngModel) {
					return;
				}

				ngModel.$render = function () {
				};

				element.bind('change', function (e) {
					var element = e.target;

                    if (attrs.ngNameVal && attrs.ngNameVal !== undefined) {
					    var ngName1 = attrs.ngNameVal.substr(0, attrs.ngNameVal.indexOf('.'));
					    var ngName2 = attrs.ngNameVal.substr(attrs.ngNameVal.indexOf('.') + 1, attrs.ngNameVal.length);
                        scope.$parent[ngName1][ngName2] = e.target.value.split(/(\\|\/)/g).pop();
                        scope.$apply();
                    }

					$q.all(slice.call(element.files, 0).map(readFile))
						.then(function (values) {
							if (element.multiple) {
								ngModel.$setViewValue(values);
							} else {
                                ngModel.$setViewValue(values.length ? values[0] : null);
							}
						});

					function readFile(file) {
						var deferred = $q.defer();

						var reader = new FileReader();
						reader.onload = function (e) {
							deferred.resolve(e.target.result);
						};
						reader.onerror = function (e) {
							deferred.reject(e);
						};
						reader.readAsDataURL(file);

						return deferred.promise;
					}

				}); //change

			} //link
		}; //return
	}
})();
