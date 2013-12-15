
var app = angular.module("app", ['ngSanitize']);

app.factory("personaService", ["$http", "$q", function($http, $q) {
      return {
         login: function(assertion) {
            var deferred = $q.defer();
            $http.post("/chronicapp/personaLogin", {
               assertion: assertion,
               timezoneOffset: (- new Date().getTimezoneOffset()/60)
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
               navigator.id.logout();
            }
         });
      };
      $scope.setSelected = function() {
         $scope.selected = this.alert;
         console.log("selected", $scope.selected);
      };
      $scope.$on("loggedOn", function(email) {
         console.log("loggedOn", email);
         $scope.changeView("alerts");
      });
      $scope.$on("changeView", function(event, view) {
         console.log("alerts changeView", view);
         if (view === "alerts") {
            $scope.alertsList();
         } else {
            $scope.alerts = undefined;            
         }
      });
   }]);

app.controller("topicsController", ["$scope", "$http",
   function($scope, $http) {
      $scope.loading = false;
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
               navigator.id.logout();
            }
         });
      };
      $scope.action = function() {
         $scope.selected = this.topic;
         console.log("action", $scope.selected);
      };
      $scope.setSelected = function() {
         $scope.selected = this.topic;
         console.log("selected", $scope.selected);
      };
      $scope.$on("changeView", function(event, view) {
         console.log("topics changeView", view);
         if (view === "topics") {
            $scope.topicsList();
         } else {
            $scope.topics = undefined;            
         }
      });
   }]);

app.controller("subscribersController", ["$scope", "$http",
   function($scope, $http) {
      $scope.loading = false;
      $scope.subscribersList = function() {
         console.log("subscribers", $scope.persona.email);
         $scope.subscribers = undefined;
         $scope.selected = undefined;
         $scope.loading = true;
         $http.post("/chronicapp/subscriberList", {
            email: $scope.persona.email
         }).then(function(response) {
            $scope.loading = false;
            console.log("subscribers", response.data);
            if (response.data && response.data.subscribers) {
               $scope.subscriptions = response.data.subscriptions;
               $scope.subscribers = response.data.subscribers;
            } else {
               console.warn("subscribers", response);
               //navigator.id.logout();
            }
         });
      };
      $scope.actionAll = function() {
         console.log("actionAll");
      };
      $scope.actionNone = function() {
         console.log("actionNone");
      };
      $scope.action = function() {
         $scope.selected = this.subscriber;
         console.log("action", $scope.selected);
      };
      $scope.setSelected = function() {
         $scope.selected = this.subscriber;
         console.log("selected", $scope.selected);
      };
      $scope.$on("changeView", function(event, view) {
         console.log("subscribers changeView", view);
         if (view === "subscribers") {
            $scope.subscribersList();
         } else {
            $scope.subscribers = undefined;            
         }
      });
   }]);

app.controller("rolesController", ["$scope", "$http",
   function($scope, $http) {
      $scope.loading = false;
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
               //navigator.id.logout();
            }
         });
      };
      $scope.action = function() {
         $scope.selected = this.role;
         console.log("action", $scope.selected);
      };
      $scope.setSelected = function() {
         $scope.selected = this.role;
         console.log("selected", $scope.selected);
      };
      $scope.$on("changeView", function(event, view) {
         console.log("roles changeView", view);
         if (view === "roles") {
            $scope.rolesList();
         } else {
            $scope.roles = undefined;            
         }
      });
   }]);

app.controller("certsController", ["$scope", "$http",
   function($scope, $http) {
      $scope.loading = false;
      $scope.certsList = function() {
         console.log("certs", $scope.persona.email);
         $scope.certs = undefined;
         $scope.selected = undefined;
         $scope.loading = true;
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
      $scope.setSelected = function() {
         $scope.selected = this.cert;
         console.log("selected", $scope.selected);
      };
      $scope.$on("changeView", function(event, view) {
         console.log("certs changeView", view);
         if (view === "certs") {
            $scope.certsList();
         } else {
            $scope.certs = undefined;            
         }
      });
   }]);
