/**
 * left bar accordion
 *
 * @author JThink
 */
$(function () {
  $("#nav-accordion").delegate('.sub-menu-link', 'click', function(e) {
    $(this).parent('li').siblings().find('ul').slideUp('fast');
    $(this).siblings('ul').slideToggle('fast');
    if ($(this).siblings('ul').length !== 0) {
      e.preventDefault();
    }
  });
});

var Script = function () {
  //sidebar dropdown menu auto scrolling
  jQuery('#sidebar .sub-menu > a').click(function () {
    var o = ($(this).offset());
    diff = 250 - o.top;
    if (diff > 0)
      $("#sidebar").scrollTo("-=" + Math.abs(diff), 500);
    else
      $("#sidebar").scrollTo("+=" + Math.abs(diff), 500);
  });

  //sidebar toggle
  $(function () {
    function responsiveView() {
      var wSize = $(window).width();
      if (wSize <= 768) {
        $('#container').addClass('sidebar-close');
        $('#sidebar > ul').hide();
      }

      if (wSize > 768) {
        $('#container').removeClass('sidebar-close');
        $('#sidebar > ul').show();
      }
    }
    $(window).on('load', responsiveView);
    $(window).on('resize', responsiveView);
  });

  // custom scrollbar
  $("#sidebar").niceScroll({
    styler: "fb",
    cursorcolor: "#e8403f",
    cursorwidth: '3',
    cursorborderradius: '10px',
    background: '#404040',
    spacebarenabled: false,
    cursorborder: ''
  });

  // custom scrollbar in main content
  $("html").niceScroll({
    cursorcolor: "#3071A9",
    cursorwidth: '6',
    cursorborderradius: '10px',
    background: '#404040',
    spacebarenabled: false,
    cursorborder: '',
    zindex: '1000'
  });
}();