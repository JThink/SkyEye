/**
 * the services module
 * 
 * @author JThink
 */
define(['angular'], function(angular) {
  'use strict';
  return angular.module('services', []).config(['$provide', function($provide) {
      if (typeof(DEBUG_FLAG) == 'undefined') {
        $provide.constant('DEBUG', true);
      } else {
        $provide.constant('DEBUG', DEBUG_FLAG);
      }
    }]);
});