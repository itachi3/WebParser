var app = angular.module('fetch', ['ui.bootstrap', 'ui.router']);
app.config(function ($stateProvider, $urlRouterProvider) {
    var home = {
        name: 'home',
        url: '/home?url',
        templateUrl: 'home.html',
        controller: 'MainCtrl'
    }

    var links = {
        name: 'links',
        url: '/links?url&type',
        templateUrl: 'links.html',
        controller: 'LinksCtrl'
    }

    $stateProvider.state(home);
    $stateProvider.state(links);
    $urlRouterProvider.otherwise('/home');
});
app.filter('unique', function () {
    return function (arr, field) {
        return _.uniq(arr, function (a) {
            return a[field];
        });
    };
});
app.controller('MainCtrl', function ($scope, $http, $stateParams, $state) {
    $scope.url = null;
    $scope.fetching = false;
    $scope.response = null;
    $scope.performSearch = function (val) {
        if (val && val.length > 0) {
            $scope.response = null;
            $scope.fetching = true;
            $scope.alert = null;
            $http.get('http://localhost:8090/v1.0/fetch', {
                params: {
                    url: val
                }
            }).success(function (res) {
                $scope.fetching = false;
                console.log(res);
                if (res.success)
                    $scope.response = res.success;
                if (res.error && res.error.length > 0)
                    $scope.alert = {
                        type: 'danger',
                        msg: res.error
                    }
            }).error(function (err) {
                $scope.fetching = false;
                console.error(err);
                $scope.alert = {
                    type: 'danger',
                    msg: 'Oops something went wrong'
                }
            })
        }
    }
    if ($stateParams.url) {
        $scope.url = $stateParams.url;
        $scope.performSearch($scope.url);
        $scope.fetching = true;
    }
    $scope.clear = function () {
        $scope.url = null;
        $scope.alert = null;
        $scope.response = null;
        $scope.fetching = false;
        $state.go('home');
    }
    $scope.getLocation = function (val) {
        return $http.get('http://localhost:8090/v1.0/suggestion').then(function (response) {
            return response.data;
        });
    };
});
app.controller('LinksCtrl', function ($scope, $http, $stateParams) {
    console.log($stateParams);
    $scope.type = $stateParams.type;
    $scope.fetching = false;
    if ($stateParams.url) {

        $scope.url = $stateParams.url;
        $http.get('http://localhost:8090/v1.0/fetch', {
            params: {
                url: $stateParams.url
            }
        }).success(function (res) {
            $scope.fetching = true;
            console.log(res);
            $scope.links = ($scope.type == 'Internal' ? res.success.hyperLinks.internal.links : res.success.hyperLinks.external.links);
            if ($scope.links.length > 0) {
                var links = [];
                links = $scope.links.slice(0, 15);
                $http.post('http://localhost:8090/v1.0/parse', {
                    links: links
                }).success(function (res) {
                    $scope.fetching = false;
                    if (res.success);
                    $scope.redirects = res.success;
                }).error(function (err) {
                    $scope.fetching = false;
                    console.error(err)
                })
            }
        }).error(function (err) {
            $scope.fetching = false;
            console.error(err);
        })
    }
    $scope.getRedirect = function (url, type) {
        if (!$scope.redirects)
            return null;
        else {
            var linkInfo = _.findWhere($scope.redirects, {
                originalUrl: url
            })
            if (linkInfo) {
                if (type == 'redirect')
                    return linkInfo.redirects;
                else if (type == 'secure')
                    return linkInfo.secure;
                else
                    return linkInfo.reason;
            } else
                return null;
        }
    }
})
