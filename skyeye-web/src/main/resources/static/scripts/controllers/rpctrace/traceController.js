/**
 * the statistics third controller
 *
 * @author JThink
 */
define([
      'controllers/controllers',
      'common/util',
      'common/chartUtil',
      'common/rpcchart',
      'common/constant',
      'echarts',
      'echarts-theme-chalk',
      'echarts-theme-halloween',
      'echarts-theme-macarons',
      'echarts-theme-roma',
      'echarts-theme-shine',
      'echarts-theme-walden',
      'services/dataService'],
    function (controllers,
              util,
              chartUtil,
              rpcchart,
              constant,
              echarts,
              chalk,
              halloween,
              macarons,
              roma,
              shine,
              walden) {
      'use strict';
      controllers.controller('TraceController', ['$scope', 'DataService', function ($scope, DataService) {

        var theme = "macarons";

        var hint = function (domId, msg) {
          var html = "<div class='alert alert-danger alert-dismissible'>" +
              "<button type='button' class='cloinitEventse' data-dismiss='alert'>" +
              "<span aria-hidden='true'>&times;</span>" +
              " <span class='sr-only'>关闭</span>" +
              "</button>" +
              msg +
              "</div>";
          $("#" + domId).before(html)

        };
        var hintHidden = function (domId) {
          $("#" + domId).prev().remove();
        };

        var chartResize = function (chartId) {
          var $c = $("#" + chartId);
          var $cp = $($c.parent()[0]);
          $cp.resize(function () {
            var wth = $(this).width();
            $c.width(wth);
            $c.height(wth / 16 * 4);
          });
          $cp.resize()
        };


        var offlineChart = {
          chartDomId: "offlineChart",
          chart: null,
          init: function () {
            chartResize(this.chartDomId);
            this.chart = echarts.init(document.getElementById(this.chartDomId), theme);
          },
          getParams: function () {
            var params = chartUtil.getInputs("#offlineControlForm [name]");
            // 默认设置
            var timeUnit = rpcchart.TimeUnit[params.scope];
            var beginEndFormat = rpcchart.TimeUnit.day.format;
            params.interval = timeUnit.interval;
            params.format = timeUnit.format;
            params.url = "rpctrace/trace";
            // 获取 uniqueName 选择值
            var uniqueIface = params.uniqueIface;
            params.uniqueIface = $scope.offlineUniqueIface;
            var uniqueMethod = params.uniqueMethod;
            params.uniqueMethod = $scope.offlineUniqueMethod;
            // 默认时间范围
            if ($.isEmptyObject(params.begin) && $.isEmptyObject(params.end)) {
              var now = Date.now();
              params.begin = util.dateFormat(new Date(now - 86400000 * 90), beginEndFormat);
              params.end = util.dateFormat(now, beginEndFormat);
            }
            params.beginDate = new Date(Date.parse(params.begin));
            params.endDate = new Date(Date.parse(params.end));

            return params;
          },
          update: function () {
            var params = offlineChart.getParams();
            DataService.getData(params, function (res) {
              if (res.resCode != "0000" || res.statCode != "00000") {
                hint(offlineChart.chartDomId, res.resMsg + "，" + res.statMsg);
              } else {
                if ($.isEmptyObject(res.data)) {
                  res.data = [];
                }
                var option = rpcchart.generateOption(params, res.data, offlineChart.chart.getOption());
                option.title.text = '';
                offlineChart.chart.clear();
                offlineChart.chart.setOption(option);
              }
            })
          },
          getMethod: function() {
            var name = $scope.offlineUniqueIface;
            var params = {
              url: "rpctrace/methods",
              iface: name
            };
             DataService.getData(params, function(res){
                var names = [];
                if (res.resCode != "0000" || res.statCode != "00000") {
                } else {
                  if ($.isEmptyObject(res.data)) {
                    res.data = [];
                  }
                  for (var i = 0; i < res.data.length; i++) {
                    names.push({
                      name: res.data[i],
                      id: res.data[i]
                    });
                  }
                }
                $scope.offlineUniqueMethod = res.data[0];
                $scope.uniqueMethods = names;
              })
          }

        };

        var watchMethod = $scope.$watch(function(){return $scope.offlineUniqueMethod},function(newValue,oldValue,scope){
            offlineChart.update();
        });

        var watchIface = $scope.$watch(function(){return $scope.offlineUniqueIface},function(newValue,oldValue,scope){
            offlineChart.getMethod();
        });

        var getUniqueIfaces = function () {
          var params = {
            url: "rpctrace/names"
          };

          DataService.getData(params, function (res) {
            var names = [];
            if (res.resCode != "0000" || res.statCode != "00000") {
            } else {
              if ($.isEmptyObject(res.data)) {
                res.data = [];
              }
              for (var i = 0; i < res.data.length; i++) {
                names.push({
                  name: res.data[i],
                  id: res.data[i]
                });
              }
            }
            $scope.offlineUniqueIface = res.data[0];
            $scope.uniqueIfaces = names;
          })
        };


        var selectScope = function () {
          $(".scope-filter").removeClass("time-mode-selected");
          var scope = $(this).addClass("time-mode-selected").attr("scope");
          var $input = $("#offlineScope");
          if($input.val() != scope){
            $input.val(scope);
            offlineChart.update()
          }
        };

        var initData = function () {
          // render data

          var now = Date.now();
          var today = util.dateFormat(now, "yyyy-MM-dd");
          var yesterday = util.dateFormat(new Date(now - 86400000), "yyyy-MM-dd");
          var minDate = util.dateFormat(new Date(now - 90 * 86400000), "yyyy-MM-dd");
          $scope.today = today;
          $scope.yesterday = yesterday;
          $scope.minDate = minDate;
          $scope.endDate = today;
          $scope.beginDate = minDate;

          getUniqueIfaces();
        };
        var initEvent = function () {
          // bind the event

          $scope.dateRange = function ($event, name) {
            $('.btn-danger').addClass('hidden');
            $scope.dateOpened = false;
            $scope[name] = true;
          };


          $scope.offlineChart = offlineChart;
          $scope.offlineChart.init();

          window.offlineChart = offlineChart;
//          clearInterval(window.apiOfflineChartInterval);
//          window.apiOfflineChartInterval = setInterval(function(){
//            $scope.offlineChart.update();
//            clearInterval(window.apiOfflineChartInterval);
//          })
//          window.thirdHash = location.hash;

          $(".scope-filter").click(selectScope);
        };


        initData();
        initEvent();
      }]);
    });