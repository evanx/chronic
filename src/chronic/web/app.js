
var app = angular.module('app', []);

app.factory("personaService", ["$http", "$q", function($http, $q) {
      return {
         login: function(assertion) {
            var deferred = $q.defer();
            $http.post("/app/LoginPersona", {
               assertion: assertion
            }).then(function(response) {
               if (response.errorMessage) {
                  console.warn("personaService login", response.errorMessage);
                  deferred.reject(response.errorMessage);
               } else {
                  console.info("personaService login", response.data.email);
                  deferred.resolve(response.data);
               }
            });
            return deferred.promise;
         },
         logout: function(email) {
            return $http.post("/app/LogoutPersona", {
               email: email
            }).then(function(response) {
               if (response.errorMessage) {
                  console.warn("personaService logout", response.errorMessage);
               } else {
                  console.info("personaService logout", response.data.email);
               }
               return response.data;
            });
         }
      };
   }]);

app.controller('personaController', ['$scope', 'personaService',
   function($scope, personaService) {
      $scope.login = function() {
         console.info("personaController login");
         navigator.id.request();
      };
      $scope.logout = function() {
         console.info("personaController logout");
         navigator.id.logout();
      };
      var persona = localStorage.getItem("persona");
      var loggedInUser = null;
      if (persona === null) {
         $scope.persona = {};
      } else {
         try {
            $scope.persona = JSON.parse(persona);
            console.log("persona", $scope.persona.email, $scope.persona.accessToken.substring(0, 10));
            loggedInUser = $scope.persona.email;
            personaService.login($scope.persona.accessToken).then(function(persona) {
               $scope.persona = persona;
               if (persona.email) {
                  localStorage.setItem("persona", JSON.stringify(persona));
               } else {
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
            personaService.login(assertion).then(function(persona) {
               $scope.persona = persona;
               if (persona.email) {
                  localStorage.setItem("persona", JSON.stringify($scope.persona));               
               }
            });
         },
         onlogout: function() {
            if ($scope.persona) {
               if ($scope.persona.email) {
                  personaService.logout($scope.persona.email);
               }
               $scope.persona = {};
            }
            localStorage.clear("persona");
         }
      });
   }]);

