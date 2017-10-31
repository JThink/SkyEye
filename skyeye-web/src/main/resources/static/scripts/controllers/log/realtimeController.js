/**
 * the log real time controller
 *
 * @author JThink
 */
define(['controllers/controllers', 'common/util','common/constant', 'underscore', 'services/dataService'], function(controllers, util, constant, _) {
  'use strict';
  controllers.controller('RealtimeController', ['$scope', 'DataService', function($scope, DataService) {

    var gHostApps = {};
    var gInterval = 1000;

    var gKeyword = '';

    var gCurrentNanoTime = [];

    var renderLog = function() {
      var host = $scope.selectedHost;
      var app = $scope.selectedApp;
      if (typeof(host) === 'undefined' || typeof(app) === 'undefined') {
        return;
      }
      var params = {
        url: 'log/realtime',
        host: $scope.selectedHost,
        app: $scope.selectedApp,
        interval: gInterval
      };
      if (gKeyword.trim() !== '') {
        params = _.extend({keyword: gKeyword.trim()}, params);
      }
      DataService.getData(params, function(data) {
        if (data.resCode === constant.resCodeFailed) {

        } else {
          if (data.statCode === constant.statCodeSuccess) {
            var logs = data.data.logs;
            if (logs.length === 0) {
              return;
            }
            var ul = $('.log-realtime-row-content .log-content ul');
            var panel = $('.log-realtime-row-content .log-content-panel');
            var total = 1024;
            var lastLength = gCurrentNanoTime.length;
            if (gCurrentNanoTime.length + logs.length > total) {
              // when log > 1024 lines
              for (var i = 0; i < lastLength + logs.length - total; ++i) {
                $('.log-realtime-row-content .log-content-panel .item')[0].remove();
                gCurrentNanoTime.splice(i, 1);
              }
            }
            _.each(logs, function(log) {
              var flag = log.flag;
              var has = function(list, value) {
                for (var i = list.length - 1; i >=0; --i) {
                  if (value === list[i]) {
                    return true;
                  } else {
                    continue;
                  }
                }
                return false;
              };
              if (!has(gCurrentNanoTime, flag)) {
                var html = '<li>';
                _.each(log.log.split('\n'), function(subItem) {
                  html += '<p class="item" flag=' + log.flag + '>' + subItem.replace('\t', '&nbsp;&nbsp;&nbsp;&nbsp;') + '</p>';
                });
                html += '</li>';
                $(ul).append(html);
                $(panel).scrollTop($(panel)[0].scrollHeight);
                gCurrentNanoTime.push(flag);
              }
            });
          }
        }
      });
    };

    var clear = function() {
      gCurrentNanoTime = [];
      $('.log-realtime-row-content .log-content .log').empty();
    };

    var renderHost = function() {
      var params = {
        url: 'app/hostApp',
        type: constant.zkNodeTypeEphemeral,
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

    var initData = function() {
      // render the app info to table
      renderHost();

      setHeight();
    };

    var hostChange = function() {
      var host = $scope.selectedHost;
      renderApp(host);
      clear();
    };

    var appChange = function() {
      var app = $scope.selectedApp;
      clear();
    };

    var gTimer = setInterval(function() {
      $scope.$apply(renderLog);
    }, gInterval);

    var setHeight = function() {
      var resize = function() {
        $('.log-realtime-row .log-content-panel').height($(window).height() - 242);
      }
      resize();
      
      window.onresize = function() {
         resize();
      }
    };

    var apply = function() {
      gKeyword = $scope.keyword;
    };

    var initEvent = function() {
      // bind the event
      $scope.hostChange = hostChange;
      $scope.appChange = appChange;
      $scope.apply = apply;
      $('.time-filter').on('click', function() {
        // time changed
        $('.time-filters .time-filter').removeClass('time-mode-selected current-time-filter').addClass('time-mode-default');
        $(this).removeClass('time-mode-default').addClass('time-mode-selected current-time-filter');
        gInterval = $('.current-time-filter').attr('data-filter');
        clear();
        // clearInterval
        clearInterval(gTimer);
        gTimer = setInterval(function() {
          $scope.$apply(renderLog);
        }, gInterval);
      });
    };

    initData();

    initEvent();
  }]);
});