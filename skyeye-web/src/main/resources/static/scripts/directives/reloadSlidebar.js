/**
 * reload slidebar directives
 *
 * @author qianjc
 */
define(['directives/directives'], function(directives) {
  directives.directive('reloadslidebar', function($rootScope) {
    return {
      restrict : 'A',
      scope : true,
      link : function(scope, element, attrs) {
        if (scope.$parent.$last) {
          $("#nav-accordion").dcAccordion({
            eventType : 'click'
          });
        }
      }
    };
  });
});
