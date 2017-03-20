/**
 * the statistics third controller
 *
 * @author JThink
 */
define([
      'controllers/controllers',
      'common/util',
      'common/chartUtil',
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
              constant,
              echarts,
              chalk,
              halloween,
              macarons,
              roma,
              shine,
              walden) {
      'use strict';
      controllers.controller('ApiController', ['$scope', 'DataService', function ($scope, DataService) {

        var eventType = "invoke_interface";
        var nameType = "api";
        var realtimeChartTitle = "实时第三方请求统计 - ";
        var offlineChartTitle = "历史第三方请求统计 - ";
        var theme = "macarons";


        var hint = function (domId, msg) {
          var html = "<div class='alert alert-danger alert-dismissible'>" +
              "<button type='button' class='close' data-dismiss='alert'>" +
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

        var realtimeChart = {
          chartDomId: "realtimeChart",
          chart: null,
          init: function () {
            chartResize(this.chartDomId);
            this.chart = echarts.init(document.getElementById(this.chartDomId), theme);
            this.update();
          },
          getParams: function () {
            var params = chartUtil.getInputs("#realtimeControlForm [name]");
            // 默认设置
            var timeUnit = chartUtil.TimeUnit.second;
            params.interval = timeUnit.interval;
            params.format = timeUnit.format;
            params.url = "statistics/realtime";
            params.eventType = eventType;


            // 获取 uniqueName 选择值
            var name = params.uniqueName;
            params.uniqueName = $.isEmptyObject(name) || name == "all" ? "all" : $scope.uniqueNames[name].id;
            // 设置时间范围
            var endDate = new Date(Date.now() - 10000);    // 获取10秒前数据
            var beginDate = new Date(endDate * 1 - 120000); // 120秒
            params.beginDate = beginDate;
            params.endDate = endDate;
            params.begin = util.dateFormat(beginDate, "yyyy-MM-dd hh:mm:ss");
            params.end = util.dateFormat(endDate, "yyyy-MM-dd hh:mm:ss");
            return params;
          },
          update: function () {
            hintHidden(realtimeChart.chartDomId);
            var params = realtimeChart.getParams();
            DataService.getData(params, function (res) {
              if (res.resCode != "0000" || res.statCode != "00000") {
                hint(realtimeChart.chartDomId, res.resMsg + "，" + res.statMsg);
              } else {
                if ($.isEmptyObject(res.data)) {
                  res.data = [];
                }
                var option = chartUtil.generateOption(params, res.data, realtimeChart.chart.getOption());
                option.title.text = '';
                realtimeChart.chart.setOption(option);
              }
            })
          }
        };

        var offlineChart = {
          chartDomId: "offlineChart",
          chart: null,
          init: function () {
            chartResize(this.chartDomId);
            this.chart = echarts.init(document.getElementById(this.chartDomId), theme);
            this.update();
          },
          getParams: function () {
            var params = chartUtil.getInputs("#offlineControlForm [name]");
            // 默认设置
            var timeUnit = chartUtil.TimeUnit[params.scope];
            var beginEndFormat = chartUtil.TimeUnit.day.format;
            params.interval = timeUnit.interval;
            params.format = timeUnit.format;
            params.url = "statistics/offline";
            params.eventType = eventType;
            // 获取 uniqueName 选择值
            var name = params.uniqueName;
            params.uniqueName = $.isEmptyObject(name) || name == "all" ? "all" : $scope.uniqueNames[name].id;
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
            hintHidden(realtimeChart.chartDomId);
            var params = offlineChart.getParams();
            DataService.getData(params, function (res) {
              if (res.resCode != "0000" || res.statCode != "00000") {
                hint(offlineChart.chartDomId, res.resMsg + "，" + res.statMsg);
              } else {
                if ($.isEmptyObject(res.data)) {
                  res.data = [];
                }
                var option = chartUtil.generateOption(params, res.data, offlineChart.chart.getOption());
                option.title.text = '';
                offlineChart.chart.clear();
                offlineChart.chart.setOption(option);
              }
            })
          }
        };

        var getUniqueNames = function () {
          var params = {
            url: "statistics/names",
            eventType: nameType
          };
          hintHidden("realtimeControl");
          DataService.getData(params, function (res) {
            var names = [{name: "全部", id: "all"}];
            if (res.resCode != "0000" || res.statCode != "00000") {
              hint("realtimeControl", "请求获取名称列表失败！" + res.resMsg + "，" + res.statMsg);
            } else {
              if ($.isEmptyObject(res.data)) {
                res.data = [];
              }
              for (var i = 0; i < res.data.length; i++) {
                names.push({
                  name: res.data[i].name,
                  id: res.data[i].name
                });
              }
            }
            $scope.uniqueNames = names;
            $scope.realtimeUniqueName = names[0].id;
            $scope.offlineUniqueName = names[0].id;
          })
        };

        var selectScope = function () {
          $(".scope-filter").removeClass("time-mode-selected");
          var scope = $(this).addClass("time-mode-selected").attr("scope");
          var $input = $("#offlineScope");
          if ($input.val() != scope) {
            $input.val(scope);
            offlineChart.update()
          }
        };


        var realtimeChartInterval = realtimeChartInterval | null;

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

          getUniqueNames();
        };
        var initEvent = function () {
          // bind the event

          $scope.dateRange = function ($event, name) {
            $('.btn-danger').addClass('hidden');
            $scope.dateOpened = false;
            $scope[name] = true;
          };


          $scope.realtimeChart = realtimeChart;
          $scope.offlineChart = offlineChart;
          $scope.realtimeChart.init();
          $scope.offlineChart.init();

          window.realtimeChart = realtimeChart;
          clearInterval(window.apiRealtimeChartInterval);
          window.apiHash = location.hash;
          window.apiRealtimeChartInterval = setInterval(function () {
            $scope.realtimeChart.update();
            if (location.hash != window.apiHash && window.apiHash) {
              clearInterval(window.apiRealtimeChartInterval);
            }
          }, 1000);

          $(".scope-filter").click(selectScope);
        };


        initData();
        initEvent();
      }]);
    });