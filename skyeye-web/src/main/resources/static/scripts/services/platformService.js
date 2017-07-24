/**
 * menu service, provide the menu show in the front end
 * TODO: should call the backend to get the menu config.
 * 
 * @author JThink
 */
define(['services/services', 'services/dataService'], function(services, dataService) {
  'use strict';
  services.factory('PlatformService', ['DataService', function(DataService) {
    return {
      getMenu: function() {
        var home = {
          key: 'home',
          name: '系统首页',
          icon: 'icon-home',
          href: 'home'
        };

        var log = {
          key: 'log',
          name: '日志查询',
          icon: 'icon-log',
          sub: [{
            key: 'realtime',
            name: '实时日志',
            href: 'log-realtime'
          }, {
            key: 'history',
            name: '历史日志',
            href: 'log-history'
          }, {
            key: 'query',
            name: '日志检索',
            href: 'log-query'
          }]
        };

        var app = {
          key: 'app',
          name: 'App监控',
          icon: 'icon-app',
          sub: [{
            key: 'status',
            name: 'App状态',
            href: 'app-status'
          }, {
            key: 'deploy',
            name: 'App部署',
            href: 'app-deploy'
          }]
        };

        var statistics = {
          key: 'statistics',
          name: 'App统计',
          icon: 'icon-statistics',
          sub: [{
            key: 'api',
            name: 'Api请求统计',
            href: 'statistics-api'
          }, {
            key: 'third',
            name: '第三方请求统计',
            href: 'statistics-third'
          }]
        };

        var rpctrace = {
          key: 'rpctrace',
          name: 'Rpc追踪',
          icon: 'icon-rpctrace',
          sub: [{
            // key: 'trace',
            // name: '服务调用统计',
            // href: 'rpctrace-trace'
          // }, {
            key: 'chain',
            name: '跟踪链信息',
            href: 'rpctrace-chain'
          }]
        };

        return {
          menu: [home, log, app, statistics, rpctrace]
        }
      }
    };
  }]);
});