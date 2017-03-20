/**
 * request the backend
 * 
 * @author JThink
 */
define(['services/services', 'common/util', 'underscore'], function(services, util, _) {
  'use strict';
  services.factory('DataService', ['$http', function($http) {
    return {
      getData: function(obj, callBack) {
        var defaultObj = {
          // global params
        };
        var params = _.extend(defaultObj, obj);
        $http({
          method : 'GET',
          url : params.url,
          params : params
        }).success(function(data) {
          if (typeof callBack === 'function') {
            callBack(data);
          }
        });
      },
      post: function(url, obj, callBack) {
        $http.post(url, obj).success(function(data) {
          if (typeof callBack === 'function') {
            callBack(data);
          }
        });
      }
    }
  }]);
});