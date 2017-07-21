/**
 * app js, provide the router
 *
 * @author JThink
 */
define(['angular', 'controllers/controllers', 'services/services', 'filters/filters', 'directives/directives'], function(angular) {
  'use strict';
  return angular.module('drip', ['controllers', 'services', 'filters', 'directives', 'ngCookies', 'ngResource', 'ngSanitize', 'ngRoute', 'ui.bootstrap', 'ui.bootstrap.tpls', 'ui.router']).config(['$routeProvider','$httpProvider','$provide', '$stateProvider', function($routeProvider, $httpProvider, $provide, $stateProvider) {
    // route
    $stateProvider.state('home', {
      url: '/home',
      templateUrl : 'views/home.html',
      controller : 'HomeController'
    }).state('log-history', {
      url: '/log/history',
      templateUrl : 'views/log/history.html',
      controller : 'HistoryController'
    }).state('log-query', {
      url: '/log/query',
      templateUrl : 'views/log/query.html',
      controller : 'QueryController'
    }).state('log-realtime', {
      url:'/log/realtime',
      templateUrl : 'views/log/realtime.html',
      controller : 'RealtimeController'
    }).state( 'app-deploy', {
      url:'/app/deploy',
      templateUrl : 'views/app/deploy.html',
      controller : 'DeployController'
    }).state('app-status', {
      url: '/app/status',
      templateUrl : 'views/app/status.html',
      controller : 'StatusController'
    }).state('statistics-api', {
      url: '/statistics/api',
      templateUrl : 'views/statistics/api.html',
      controller : 'ApiController'
    }).state('statistics-third', {
      url: '/statistics/third',
      templateUrl : 'views/statistics/third.html',
      controller : 'ThirdController'
    }).state('rpctrace-trace', {
        url: '/rpctrace/trace',
        templateUrl: 'views/rpctrace/trace.html',
        controller: 'TraceController'
    }).state('rpctrace-chain', {
        url: '/rpctrace/chain',
        templateUrl: 'views/rpctrace/chain.html',
        controller: 'ChainController'
    });
  }]).run(['$rootScope', '$location', '$state', function($rootScope, $location, $state) {
    // change for debug
    // $rootScope.$on('$locationChangeStart', function() {
      // if(!auth) {
      //   $location.path('/403'+ window.localStorage.getItem('lang'));
      //   $('.project-nav').hide();
      //   $('.profile-menu').hide();
      // }
    // });
    $rootScope.$state = $state;
    $rootScope.$on('$routeChangeSuccess', function() {
      // console.log('change');
      // var hashUrl = window.location.hash.split('/');
      // var menu = hashUrl[1], sm = hashUrl[2];
      // $('.' + menu).trigger('click');
      // $('.sidebar-content li>a').css('color', '#aeb2b7');
      // if (sm) {
      //   $('.' + menu + '-' + sm).css('color', '#dc5c5c');
      // } else {
      //   $('.' + menu).css('color', '#dc5c5c');
      // }
      // $('.page-title').html($('.' + menu + '-' + sm).find('.lang-translate').html());
    });
  }]);
});
