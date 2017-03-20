/**
 * the log query controller
 *
 * @author JThink
 */
define(['controllers/controllers', 'common/util', 'common/constant', 'services/dataService'], function (controllers, util, constant) {
  'use strict';
  controllers.controller('QueryController', ['$scope', 'DataService', function ($scope, DataService) {

    var renderLog = function() {
      $('.log-query-row .log-content-panel').addClass('loading');
      var params = {
        url: 'log/query',
        sql: $scope.sql
      };
      DataService.getData(params, function(data) {
        $('.log-query-row .log-content-panel').removeClass('loading');
        if (data.resCode === constant.resCodeFailed) {
          $('.log-query-row .sql .result').addClass('hidden');
          $('.log-query-row .sql .error').removeClass('hidden');
          $scope.logs = [];
          $scope.queryError = data.statMsg;
        } else {
          if (data.statCode === constant.statCodeSuccess) {
            $('.log-query-row .sql .result').removeClass('hidden');
            $('.log-query-row .sql .error').addClass('hidden');
            $scope.logs = [];
            $scope.logs = data.data.logs;
            $scope.total = data.data.total;
            $scope.current = data.data.current;
          }
        }
      });
    };

    var setHeight = function() {
      var resize = function() {
        $('.log-query-row .log-content-panel').height($(window).height() - 500);
      }
      resize();
      
      window.onresize = function() {
         resize();
      }
    };

    var renderColumn = function() {
      var columns = [
        {
          'column': 'day',
          'type': 'String',
          'mean': '日期',
          'example': '2016-10-11'
        }, {
          'column': 'time',
          'type': 'String',
          'mean': '时间',
          'example': '19:55:38.338'
        }, {
          'column': 'nanoTime',
          'type': 'String',
          'mean': '日志生成纳秒',
          'example': '37326316993466'
        }, {
          'column': 'app',
          'type': 'String',
          'mean': '应用名',
          'example': 'footprint-sync'
        }, {
          'column': 'host',
          'type': 'String',
          'mean': '主机名',
          'example': 'JThink-pc'
        }, {
          'column': 'thread',
          'type': 'String',
          'mean': '线程名',
          'example': 'main'
        }, {
          'column': 'level',
          'type': 'String',
          'mean': '日志级别',
          'example': 'INFO'
        }, {
          'column': 'eventType',
          'type': 'String',
          'mean': '日志类型',
          'example': 'normal'
        }, {
          'column': 'pack',
          'type': 'String',
          'mean': '包名',
          'example': 'org.I0Itec.zkclient'
        }, {
          'column': 'clazz',
          'type': 'String',
          'mean': '类名',
          'example': 'ZkClient'
        }, {
          'column': 'line',
          'type': 'String',
          'mean': '行号',
          'example': '339'
        }, {
          'column': 'messageSmart',
          'type': 'String',
          'mean': '粗粒度索引',
          'example': 'this is 测试40'
        },  {
          'column': 'messageMax',
          'type': 'String',
          'mean': '细粒度索引',
          'example': 'this is 测试40'
        }
      ];
      $scope.columns = columns;
    };

    var initData = function() {
      // init data
      setHeight();
      renderColumn();
      showColumn();
    };

    var gShow = true;

    var showColumn = function() {
      if (gShow === true) {
        $scope.btnShowText = '查看字段';
        $('.log-query-row-content .panel-table').addClass('hidden');
        gShow = false;
      } else {
        $scope.btnShowText = '隐藏字段';
        $('.log-query-row-content .panel-table').removeClass('hidden');
        gShow = true;
      }
    };

    var query = function() {
      if ($.trim($scope.sql) === '') {
          $('.log-query-row .sql .result').addClass('hidden');
          $('.log-query-row .sql .error').removeClass('hidden');
          $scope.logs = [];
          $scope.queryError = 'sql错误';
          return;
      }
      renderLog();
    };

    var initEvent = function() {
      // bind the event
      $scope.showColumn = showColumn;
      $scope.query = query;
    };

    initData();

    initEvent();
  }]);
});