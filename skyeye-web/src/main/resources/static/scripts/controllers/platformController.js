/**
 * menu controller, provide the menu data for frontend
 *
 * @author JThink
 */
define(['controllers/controllers', 'common/util', 'uiBootstrapTpls', 'services/platformService', 'directives/reloadSlidebar', 'services/dataService'], function (controllers, util, uiBootstrapTpls) {
  'use strict';
  controllers.controller('PlatformController', ['$scope', '$state', 'paginationConfig', 'datepickerPopupConfig', 'PlatformService', 'DataService', function ($scope, $state, paginationConfig, datepickerPopupConfig, PlatformService, DataService) {

    var renderPagination = function () {
      paginationConfig.firstText = '首页';
      paginationConfig.lastText = '末页';
      paginationConfig.previousText = '上一页';
      paginationConfig.nextText = '下一页';
    };

    var initData = function () {
      // init menu
      $scope.menus = PlatformService.getMenu().menu;
      // render the jqpagination
      renderPagination();
    };

    var setMenuClickTitle = function () {
      $('.sidebar-content').on('click', 'li>a', function () {
        if ($(this).closest('li').hasClass('platform-sub2') || $(this).closest('li').find('.platform-sub2').length === 0) {
          // change the color
          $('.sidebar-content li>a').css('color', '#a7b1c2');
          $(this).css('color', '#ffffff');
          $('.page-title').html($(this).find('.lang-translate').html());
        }
      });
    };

    var triggerMenu = function () {
      var hashUrl = window.location.hash.split('/');
      var menu = hashUrl[1], sm = hashUrl[2];
      if (sm) {
        $('.' + menu).trigger('click');
        $('.' + menu + '-' + sm).trigger('click');
      } else {
        $('.' + menu).trigger('click');
      }
    };

    var initEvent = function () {
      setMenuClickTitle();
      // click the brand
      $('.navbar-brand').on('click', function () {
        // window.location.href = window.location.href.split('#')[0];
        $('.sub-menu-link :first').trigger('click');
      });

      // render the menu according to the url
      setTimeout(function () {
        triggerMenu();
      }, 100);

    };

    // init data
    initData();
    // add the click event
    initEvent();
  }]);
});