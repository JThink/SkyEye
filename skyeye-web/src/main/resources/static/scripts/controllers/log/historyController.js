/**
 * the log history controller
 *
 * @author JThink
 */
define(['controllers/controllers', 'common/util','common/constant', 'services/dataService'], function(controllers, util, constant) {
  'use strict';
  controllers.controller('HistoryController', ['$scope', 'DataService', function($scope, DataService) {

    var gHostApps = {};

    var renderLog = function() {
      $('.log-history-row .log-content-panel').addClass('loading');
      var params = {
        url: 'log/history',
        host: $scope.selectedHost,
        app: $scope.selectedApp
      };
      DataService.getData(params, function(data) {
        $('.log-history-row .log-content-panel').removeClass('loading');
        if (data.resCode === constant.resCodeFailed) {
          $('.log-history-row .filter .result').addClass('hidden');
          $('.log-history-row .filter .error').removeClass('hidden');
          $scope.logs = [];
          $scope.queryError = data.statMsg;
        } else {
          if (data.statCode === constant.statCodeSuccess) {
            $('.log-history-row .filter .result').removeClass('hidden');
            $('.log-history-row .filter .error').addClass('hidden');
            $scope.logs = [];
            $scope.logs = data.data.logs;
            $scope.total = data.data.total;
            $scope.current = data.data.current;
          }
        }
      });
    };

    var renderHost = function() {
      var params = {
        url: 'app/hostApp',
        type: constant.zkNodeTypePersistent,
        isDeploy: false
      };
      DataService.getData(params, function(data) {
        var hostApps = data.data;
        gHostApps = hostApps;
        var hosts = Object.keys(hostApps);
        $scope.hosts = hosts;
        if (hosts.length > 0) {
          var host = hosts[0];
          $scope.selectedHost = host;
          renderApp(host);
        }
      });
    };

    var renderApp = function(host) {
      var apps = gHostApps[host];
      $scope.apps = apps;
      if (apps.length > 0) {
        $scope.selectedApp = apps[0];
      }
    };

    var setHeight = function() {
      var resize = function() {
        $('.log-history-row .log-content-panel').height($(window).height() - 325);
      }
      resize();
      
      window.onresize = function() {
         resize();
      }
    };

    var initDateRange = function() {
      var today = util.getToday();
      $scope.date = today;
    };

    var initLogLevel = function() {
      var params = {
        url: 'log/level'
      };
      DataService.getData(params, function(data) {
        var levels = data.data;
        $scope.levels = levels;
        if (levels.length > 0) {
          $scope.selectedLevel = levels[0];
        }
      });
    };

    var initEventType = function() {
      var params = {
        url: 'log/eventType'
      };
      DataService.getData(params, function(data) {
        var types = data.data;
        $scope.types = types;
        if (types.length > 0) {
          $scope.selectedType = types[0];
        }
      });
    };

    var initOpt = function() {
      var params = {
        url: 'log/opt'
      };
      DataService.getData(params, function(data) {
        var opts = data.data;
        $scope.opts = opts;
        if (opts.length > 0) {
          $scope.selectedOpt = opts[0];
        }
      });
    };

    var initData = function() {
      // render data
      $scope.time = util.dateFormat(util.getToday(), 'hh:mm:ss') + '.000';
      // render the app info to table
      renderHost();
      setHeight();

      initDateRange();

      initLogLevel();
      initEventType();
      initOpt();
    };

    var hostChange = function() {
      var host = $scope.selectedHost;
      renderApp(host);
    };

    var query = function() {
      // check the input
      var date = $scope.date;
      var time = $scope.time;
      if (typeof(date) === 'undefined' || typeof(time) === 'undefined' || $.trim(date) === '' || $.trim(time) === '') {
        $('.log-history-row .filter .result').addClass('hidden');
        $('.log-history-row .filter .error').removeClass('hidden');
        $scope.logs = [];
        $scope.queryError = '过滤条件有误';
        return;
      }
      var filter = {
        host: $scope.selectedHost,
        app: $scope.selectedApp,
        date: util.dateFormat(date),
        opt: $scope.selectedOpt,
        time: time,
        level: $scope.selectedLevel,
        eventType: $scope.selectedType
      };
      DataService.post('log/history', filter, function(data) {
        $('.log-history-row .log-content-panel').removeClass('loading');
        if (data.resCode === constant.resCodeFailed) {
          $('.log-history-row .filter .result').addClass('hidden');
          $('.log-history-row .filter .error').removeClass('hidden');
          $scope.logs = [];
          $scope.queryError = data.statMsg;
        } else {
          if (data.statCode === constant.statCodeSuccess) {
            $('.log-history-row .filter .result').removeClass('hidden');
            $('.log-history-row .filter .error').addClass('hidden');
            $scope.logs = [];
            $scope.logs = data.data.logs;
            $scope.total = data.data.total;
            $scope.current = data.data.current;
          }
        }
      });     
    };

    var initEvent = function() {
      // bind the event
      $scope.hostChange = hostChange;
      $scope.query = query;

      // date change
      $scope.dateRange = function($event, name) {
        $('.btn-danger').addClass('hidden');
        $event.preventDefault();
        $event.stopPropagation();
        $scope.dateOpened = false;
        $scope[name] = true;
      };
    };

    initData();

    initEvent();
  }]);
});