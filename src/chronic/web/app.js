
var app = angular.module("app", ['ngSanitize', 'angles', 'ui.bootstrap']);

app.factory("personaService", ["$http", "$q", function($http, $q) {
        return {
            login: function(assertion) {
                var deferred = $q.defer();
                $http.post("/chronicapp/personaLogin", {
                    assertion: assertion,
                    timezoneOffset: (-new Date().getTimezoneOffset() / 60)
                }).then(function(response) {
                    if (response.errorMessage) {
                        console.warn("personaService login", response.errorMessage);
                        deferred.reject(response.errorMessage);
                    } else {
                        console.log("personaService login", response.data.email);
                        deferred.resolve(response.data);
                    }
                });
                return deferred.promise;
            },
            logout: function(email) {
                return $http.post("/chronicapp/personaLogout", {
                    email: email
                }).then(function(response) {
                    if (response.errorMessage) {
                        console.warn("personaService logout", response.errorMessage);
                    } else {
                        console.log("personaService logout", response);
                    }
                    return response.data;
                });
            }
        };
    }]);

app.controller("personaController", ["$scope", "$location", "personaService",
    function($scope, $location, personaService) {
        $scope.login = function() {
            console.log("persona login");
            navigator.id.request();
        };
        $scope.logout = function() {
            console.log("persona logout");
            navigator.id.logout();
        };
        $scope.changeView = function(view) {
            console.log("persona changeView", view);
            $scope.view = view;
            //$location.path("/" + view);
            $scope.$broadcast("changeView", view);
        };
        $scope.getClass = function(path) {
            if ($scope.view === path) {
                return "active";
            } else {
                return "";
            }
        };
        var persona = localStorage.getItem("persona");
        var loggedInUser = null;
        if (persona === null) {
            $scope.persona = {};
        } else {
            try {
                $scope.persona = JSON.parse(persona);
                console.log("persona", $scope.persona.email, $scope.persona.assertion.substring(0, 10));
                loggedInUser = $scope.persona.email;
                personaService.login($scope.persona.assertion).then(function(persona) {
                    $scope.persona = persona;
                    if (persona.email) {
                        localStorage.setItem("persona", JSON.stringify(persona));
                        $scope.$broadcast("loggedOn", persona.email);
                    } else {
                        console.warn("login", persona);
                        localStorage.clear("persona");
                    }
                });
            } catch (e) {
                console.log(e);
            }
        }
        navigator.id.watch({
            loggedInUser: loggedInUser,
            onlogin: function(assertion) {
                $scope.loggingIn = true;
                personaService.login(assertion).then(function(response) {
                    $scope.loggingIn = false;
                    $scope.persona = response;
                    if (response.email) {
                        localStorage.setItem("persona", JSON.stringify($scope.persona));
                        $scope.$broadcast("loggedOn", response.email);
                    } else {
                        console.warn("login", response);
                    }
                });
            },
            onlogout: function(response) {
                if ($scope.persona) {
                    if ($scope.persona.email) {
                        personaService.logout($scope.persona.email);
                    } else {
                        console.warn("logout", response);
                    }
                    $scope.persona = {};
                }
                localStorage.clear("persona");
            }
        });
    }]);

app.controller("alertsController", ["$scope", "$http",
    function($scope, $http) {
        $scope.loading = false;
        $scope.alertsList = function() {
            console.log("alerts", $scope.persona.email);
            $scope.alerts = undefined;
            $scope.selected = undefined;
            $scope.loading = true;
            $http.post("/chronicapp/alertList", {
                email: $scope.persona.email
            }).then(function(response) {
                $scope.loading = false;
                console.log("alerts", response.data);
                if (response.data && response.data.alerts) {
                    $scope.alerts = response.data.alerts;
                } else {
                    console.warn("alerts", response);
                }
            });
        };
        $scope.setSelected = function() {
            $scope.selected = this.alert;
            console.log("selected", $scope.selected);
        };
        $scope.$on("loggedOn", function(email) {
            console.log("loggedOn", email);
            //$scope.changeView("info");
        });
        $scope.$on("changeView", function(event, view) {
            if (view === "alerts") {
                $scope.alertsList();
            } else {
                $scope.alerts = undefined;
                $scope.loading = false;
            }
        });
    }]);

app.controller("topicsController", ["$scope", "$http",
    function($scope, $http) {
        $scope.loading = false;
        $scope.actionAllDisabled = $scope.persona.demo;
        $scope.actionNoneDisabled = $scope.persona.demo;
        $scope.topicsList = function() {
            console.log("topics", $scope.persona.email);
            $scope.topics = undefined;
            $scope.selected = undefined;
            $scope.loading = true;
            $http.post("/chronicapp/topicList", {
                email: $scope.persona.email
            }).then(function(response) {
                $scope.loading = false;
                console.log("topics", response.data);
                if (response.data && response.data.topics) {
                    $scope.topics = response.data.topics;
                } else {
                    console.warn("topics", response);
                }
            });
        };
        $scope.actionAll = function() {
            console.log("actionAll");
            $http.post("/chronicapp/topicActionAll", {
            }).then(function(response) {
                if (response.data && response.data.topics) {
                    $scope.topics = response.data.topics;
                } else {
                    console.warn("topicActionAll", response);
                }
            });
        };
        $scope.actionNone = function() {
            console.log("actionNone");
            $http.post("/chronicapp/topicActionNone", {
            }).then(function(response) {
                if (response.data && response.data.topics) {
                    $scope.topics = response.data.topics;
                } else {
                    console.warn("topicActionNone", response);
                }
            });
        };
        $scope.action = function() {
            $scope.selected = this.topic;
            console.log("action", $scope.selected);
            $http.post("/chronicapp/topicAction", {
                "topic": $scope.selected
            }).then(function(response) {
                if (response.data && response.data.topic) {
                    console.warn("topicAction", response.data.topic.actionLabel);
                    $scope.selected.actionLabel = response.data.topic.actionLabel;
                } else {
                    console.warn("topicAction", response);
                }
            });
        };
        $scope.setSelected = function() {
            $scope.selected = this.topic;
            console.log("selected", $scope.selected);
        };
        $scope.$on("changeView", function(event, view) {
            if (view === "topics") {
                $scope.topicsList();
            } else {
                $scope.topics = undefined;
                $scope.loading = false;
            }
        });
    }]);

app.controller("subscriptionsController", ["$scope", "$http",
    function($scope, $http) {
        $scope.loading = false;
        $scope.actionAllDisabled = $scope.persona.demo;
        $scope.actionNoneDisabled = $scope.persona.demo;
        $scope.subscriptionsList = function() {
            console.log("subscriptions", $scope.persona.email);
            $scope.subscriptions = undefined;
            $scope.selected = undefined;
            $scope.loading = true;
            $http.post("/chronicapp/subscriptionList", {
                email: $scope.persona.email
            }).then(function(response) {
                $scope.loading = false;
                console.log("subscriptions", response.data);
                if (response.data && response.data.subscriptions) {
                    $scope.subscriptions = response.data.subscriptions;
                    $scope.subscriptions = response.data.subscriptions;
                } else {
                    console.warn("subscriptions", response);
                }
            });
        };
        $scope.actionAll = function() {
            console.log("actionAll");
            $http.post("/chronicapp/subscriptionActionAll", {
            }).then(function(response) {
                if (response.data && response.data.subscriptions) {
                    $scope.subscriptions = response.data.subscriptions;
                } else {
                    console.warn("subscriptionActionAll", response);
                }
            });
        };
        $scope.actionNone = function() {
            console.log("actionNone");
            $http.post("/chronicapp/subscriptionActionNone", {
            }).then(function(response) {
                if (response.data && response.data.subscriptions) {
                    $scope.subscriptions = response.data.subscriptions;
                } else {
                    console.warn("subscriptionActionNone", response);
                }
            });
        };
        $scope.action = function() {
            $scope.selected = this.subscription;
            console.log("action", $scope.selected);
            $http.post("/chronicapp/subscriptionAction", {
                "subscription": $scope.selected
            }).then(function(response) {
                if (response.data && response.data.subscription) {
                    console.warn("subscriptionAction", response.data.subscription.actionLabel);
                    $scope.selected.actionLabel = response.data.subscription.actionLabel;
                } else {
                    console.warn("subscriptionAction", response);
                }
            });
        };
        $scope.setSelected = function() {
            $scope.selected = this.subscription;
            console.log("selected", $scope.selected);
        };
        $scope.$on("changeView", function(event, view) {
            if (view === "subscriptions") {
                $scope.subscriptionsList();
            } else {
                $scope.subscriptions = undefined;
                $scope.loading = false;
            }
        });
    }]);

app.controller("rolesController", ["$scope", "$http",
    function($scope, $http) {
        $scope.loading = false;
        $scope.actionAllDisabled = $scope.persona.demo;
        $scope.actionNoneDisabled = $scope.persona.demo;
        $scope.rolesList = function() {
            console.log("roles", $scope.persona.email);
            $scope.roles = undefined;
            $scope.selected = undefined;
            $scope.loading = true;
            $http.post("/chronicapp/roleList", {
                email: $scope.persona.email
            }).then(function(response) {
                $scope.loading = false;
                console.log("roles", response.data);
                if (response.data && response.data.roles) {
                    $scope.roles = response.data.roles;
                } else {
                    console.warn("roles", response);
                }
            });
        };
        $scope.actionAll = function() {
            console.log("actionAll");
            $http.post("/chronicapp/roleActionAll", {
            }).then(function(response) {
                if (response.data && response.data.roles) {
                    $scope.roles = response.data.roles;
                } else {
                    console.warn("roleActionAll", response);
                }
            });
        };
        $scope.action = function() {
            $scope.selected = this.role;
            console.log("action", $scope.selected);
            $http.post("/chronicapp/roleAction", {
                "role": $scope.selected
            }).then(function(response) {
                if (response.data && response.data.role) {
                    console.warn("roleAction", response.data.role.actionLabel);
                    $scope.selected.actionLabel = response.data.role.actionLabel;
                } else {
                    console.warn("roleAction", response);
                }
            });
        };
        $scope.setSelected = function() {
            $scope.selected = this.role;
            console.log("selected", $scope.selected);
        };
        $scope.$on("changeView", function(event, view) {
            if (view === "roles") {
                $scope.rolesList();
            } else {
                $scope.roles = undefined;
            }
        });
    }]);

app.controller("certsController", ["$scope", "$http",
    function($scope, $http) {
        $scope.actionAllDisabled = $scope.persona.demo;
        $scope.actionNoneDisabled = $scope.persona.demo;
        $scope.loading = false;
        $scope.certsList = function() {
            console.log("certs", $scope.persona.email);
            $scope.certs = undefined;
            $scope.loading = true;
            $scope.selected = undefined;
            $http.post("/chronicapp/certList", {
                email: $scope.persona.email
            }).then(function(response) {
                $scope.loading = false;
                console.log("certs", response.data);
                if (response.data && response.data.certs) {
                    $scope.certs = response.data.certs;
                } else {
                    console.warn("certs", response);
                    //navigator.id.logout();
                }
            });
        };
        $scope.actionAll = function() {
            console.log("actionAll");
            $scope.loading = true;
            $scope.certs = undefined;
            $http.post("/chronicapp/certActionAll", {
            }).then(function(response) {
                if (response.data && response.data.certs) {
                    console.warn("certActionAll", response.data);
                    $scope.certs = response.data.certs;
                } else {
                    console.warn("certActionAll", response);
                }
            });
        };
        $scope.action = function() {
            $scope.selected = this.cert;
            console.log("action", $scope.selected);
            $http.post("/chronicapp/certAction", {
                "cert": $scope.selected
            }).then(function(response) {
                if (response.data && response.data.cert) {
                    console.warn("certAction", response.data.cert.actionLabel);
                    $scope.selected.actionLabel = response.data.cert.actionLabel;
                } else {
                    console.warn("certAction", response);
                }
            });
        };
        $scope.setSelected = function() {
            $scope.selected = this.cert;
            console.log("selected", $scope.selected);
            $http.post("/chronicapp/certAction", {
                "cert": $scope.selected
            }).then(function(response) {
                if (response.data && response.data.cert) {
                    console.warn("certAction", response.data.cert.actionLabel);
                    $scope.selected.actionLabel = response.data.cert.actionLabel;
                } else {
                    console.warn("certAction", response);
                }
            });
        };
        $scope.$on("changeView", function(event, view) {
            if (view === "certs") {
                $scope.certsList();
            } else {
                $scope.certs = undefined;
                $scope.loading = false;
            }
        });
    }]);

app.controller("chartController", function($scope, $http) {

    $scope.interval = "MINUTELY";
    
    $http.post("/chronicapp/intervalList", {
    }).then(function(response) {
        if (response.data && response.data.intervals) {
            console.log("intervalList", response.data.intervals);
            $scope.intervals = response.data.intervals;
        } else {
            console.warn("intervalList", response);
        }
    });

    $scope.intervalChanged = function(value) {
        console.log("intervalChanged", value);
        $scope.interval = value;
        $scope.chartList();
    };

    $scope.chartList = function() {
        console.log("chartList");
        $scope.charts = undefined;
        $scope.metrics = undefined;
        $scope.selected = undefined;
        $scope.loading = true;
        $http.post("/chronicapp/chartList", {
            data: $scope.interval
        }).then(function(response) {
            $scope.loading = false;
            $scope.charts = undefined;
            if (response.data && response.data.metrics) {
                $scope.metrics = response.data.metrics;
                console.log("metrics", response.data.metrics.length, response.data.metrics.length);
                if ($scope.metrics.length > 0) {
                    console.log("metrics first length", response.data.metrics[0].data.length, response.data.metrics[0].labels.length);
                }
                $scope.renderCharts();
            } else {
                console.warn("metrics", response);
            }
        });
    };

    $scope.renderCharts = function() {
        var charts = [];
        console.log("renderCharts", $scope.metrics.length);
        for (var i = 0; i < $scope.metrics.length; i++) {
            var metrics = $scope.metrics[i];
            var chart = {
                commonName: $scope.metrics[i].commonName,
                topicLabel: $scope.metrics[i].topicLabel,
                metricLabel: $scope.metrics[i].metricLabel,
                labels: $scope.metrics[i].labels,
                datasets: [
                    {
                        fillColor: "rgba(151,187,205,0)",
                        strokeColor: "#e67e22",
                        pointColor: "rgba(151,187,205,0)",
                        pointStrokeColor: "#e67e22",
                        data: $scope.metrics[i].data
                    }
                ]
            };
            charts.push(chart);
        }
        $scope.charts = charts;
    };

    $scope.options = {
        pointDotRadius: 1,
        pointDotStrokeWidth: 1,
        scaleShowLabels: true,
        scaleOverlay: false,
        scaleOverride: false,
        scaleSteps: 3,
        scaleStepWidth: 2,
        scaleStartValue: 0,
        segmentShowStroke: false
    };

    $scope.$on("changeView", function(event, view) {
        if (view === "charts") {
            $scope.chartList();
        } else {
            $scope.loading = false;
            $scope.charts = undefined;
        }
    });

})
