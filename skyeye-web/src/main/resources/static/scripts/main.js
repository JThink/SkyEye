/**
 * require js lib
 *
 * @author JThink
 */
var basePath = './libs/', min = '.min';
require.config({
  baseUrl: './scripts',
  paths: {
    jquery: basePath + 'jquery/jquery' + min,
    jqueryui: basePath + 'jquery-plugin/jquery-ui' + min,
    commonTable: basePath + 'jquery-plugin/common-table',
    underscore: basePath + 'underscore/underscore' + min,
    bootstrap: basePath + 'bootstrap/bootstrap' + min,
    dcjqaccordion: basePath + 'jquery-plugin/jquery.dcjqaccordion.2.7',
    scrollTo: basePath + 'jquery-plugin/jquery.scrollTo' + min,
    nicescroll: basePath + 'jquery-plugin/jquery.nicescroll',
    select: basePath + 'bootstrap-plugin/bootstrap-select',
    ui: basePath + 'custom/ui',
    angular: basePath + 'angular/angular' + min,
    uiBootstrapTpls: basePath + 'angular-plugin/ui-bootstrap-tpls-0.13.0' + min,
    'angular-route': basePath + 'angular/angular-route' + min,
    'angular-cookies': basePath + 'angular/angular-cookies' + min,
    'angular-sanitize': basePath + 'angular/angular-sanitize' + min,
    'angular-resource': basePath + 'angular/angular-resource' + min,
    'angular-mocks': basePath + 'angular/angular-mocks' + min,
    'ui-router': basePath + 'angular-plugin/angular-ui-router' + min,
//    'angular-upload': basePath + 'upload/ng-file-upload' + min,
//    'directive': basePath + 'upload/ng-file-upload-shim' + min,
    echarts: basePath + 'echarts/echarts',
    zrender: basePath + 'echarts/zrender',
    'echarts-theme-chalk': basePath + 'echarts/theme/chalk',
    'echarts-theme-halloween': basePath + 'echarts/theme/halloween',
    'echarts-theme-macarons': basePath + 'echarts/theme/macarons',
    'echarts-theme-roma': basePath + 'echarts/theme/roma',
    'echarts-theme-shine': basePath + 'echarts/theme/shine',
    'echarts-theme-walden': basePath + 'echarts/theme/walden',
    'jedate': basePath + 'jedate/jquery.jedate' + min
  },
  shim: {
    underscore: {
      exports: '_'
    },
    bootstrap: {
      deps: [
        'jquery'
      ],
      exports: 'bootstrap'
    },
    jqueryui: {
      deps: [
        'jquery'
      ],
      exports: 'jqueryui'
    },
    dcjqaccordion: {
      deps: [
        'jquery'
      ],
      exports: 'dcjqaccordion'
    },
    commonTable: {
      deps: [
        'jquery'
      ],
      exports: 'commonTable'
    },
    scrollTo: {
      deps: [
        'jquery'
      ],
      exports: 'scrollTo'
    },
    nicescroll: {
      deps: [
        'jquery'
      ],
      exports: 'nicescroll'
    },
    select: {
      deps: [
        'jquery'
      ],
      exports: 'select'
    },
    ui: {
      deps: [
        'dcjqaccordion',
        'nicescroll',
        'scrollTo',
        'select',
        'angular'
      ],
      exports: 'ui'
    },
    angular: {
      deps: [
        'jquery'
      ],
      exports: 'angular'
    },
    uiBootstrapTpls: [
      'angular'
    ],
    'ui-router': [
      'angular'
    ],
    'angular-route': [
      'angular'
    ],
    'angular-cookies': [
      'angular'
    ],
    'angular-sanitize': [
      'angular'
    ],
    'angular-resource': [
      'angular'
    ],
    'angular-mocks': {
      deps: [
        'angular'
      ],
      exports: 'angular.mock'
    },
    echarts: {
      exports: 'echarts'
    },
    'echarts-theme-chalk': {
      exports: 'echarts-theme-chalk'
    },
    'echarts-theme-halloween': {
      exports: 'echarts-theme-halloween'
    },
    'echarts-theme-macarons': {
      exports: 'echarts-theme-macarons'
    },
    'echarts-theme-roma': {
      exports: 'echarts-theme-roma'
    },
    'echarts-theme-shine': {
      exports: 'echarts-theme-shine'
    },
    'echarts-theme-walden': {
      exports: 'echarts-theme-walden'
    },
    jedate: {
      deps: [
        'jquery'
      ],
      exports: 'jedate'
    }
  },
  priority: [
    'angular'
  ]
});

require([
  'angular',
  'app',
  'underscore',
  'jquery',
  // 'jqueryui',
  'bootstrap',
  'dcjqaccordion',
  'commonTable',
  'scrollTo',
  'nicescroll',
  'select',
  'ui',
  'uiBootstrapTpls',
  'ui-router',
  'angular-route',
  'angular-cookies',
  'angular-sanitize',
  'angular-resource',
  'echarts',
  'echarts-theme-chalk',
  'echarts-theme-halloween',
  'echarts-theme-macarons',
  'echarts-theme-roma',
  'echarts-theme-shine',
  'echarts-theme-walden',
  'jedate',
  'controllers/platformController',
  'controllers/homeController',
  'controllers/log/historyController',
  'controllers/log/queryController',
  'controllers/log/realtimeController',
  'controllers/app/deployController',
  'controllers/app/statusController',
  'controllers/statistics/apiController',
  'controllers/statistics/thirdController',
  'controllers/rpctrace/traceController',
  'controllers/rpctrace/chainController'
], function (angular, app) {
  'use strict';
  angular.element().ready(function () {
    angular.bootstrap(document, [app.name]);
  });
});
