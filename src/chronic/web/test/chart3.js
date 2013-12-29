var app = angular.module("anglesExample", ["angles"]);

app.controller("angCtrl", function($scope, $http) {

   $scope.metricList = function() {
      console.log("metricList");
      $scope.metrics = undefined;
      $scope.selected = undefined;
      $scope.loading = true;
      $http.post("/chronicapp/metricList", {
      }).then(function(response) {
         $scope.loading = false;
         if (response.data && response.data.metrics) {
            console.log("metrics", response.data.metrics[0].data);
            $scope.metrics = response.data.metrics;
            $scope.drawChart();
         } else {
            console.warn("metrics", response);
         }
      });
   };

   $scope.metricList();

   $scope.drawChart = function() {
      $scope.chart = [{
            labels: $scope.metrics[0].labels,
            datasets: [
               {
                  fillColor: "rgba(151,187,205,0)",
                  strokeColor: "#e67e22",
                  pointColor: "rgba(151,187,205,0)",
                  pointStrokeColor: "#e67e22",
                  data: $scope.metrics[0].data
               }
            ]
         }]
   };

   $scope.options = {
      segmentShowStroke: false
   }
})
