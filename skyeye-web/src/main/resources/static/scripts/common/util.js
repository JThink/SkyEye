/**
 * util js
 *
 * @author JThink
 */
define(['jquery', 'underscore'], function ($, _) {

  var dateFormat = function (date, format) {
    format = format || "yyyy-MM-dd";
    var d = new Date(date);
    var o = {
      "M+": d.getMonth() + 1,
      "d+": d.getDate(),
      "h+": d.getHours(),
      "m+": d.getMinutes(),
      "s+": d.getSeconds(),
      "q+": Math.floor((d.getMonth() + 3) / 3),
      "w+": Math.floor(d.getDate() / 7 + 1),
      "S": d.getMilliseconds()
    }

    if (/(y+)/.test(format)) {
      format = format.replace(RegExp.$1, (d.getFullYear() + "")
          .substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
      if (new RegExp("(" + k + ")").test(format)) {
        format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k]
            : ("00" + o[k]).substr(("" + o[k]).length));
      }
    }
    return format;
  };

  var getToday = function () {
    return new Date();
  };

  var pagination = function (data, page, pageSize) {
    return _.first(_.rest(data, (page - 1) * pageSize), pageSize);
  };

  var trimStr = function (str) {
    return str.replace(/(^\s*)|(\s*$)/g, '');
  };

  var isEmpty = function (data) {
    return (data == '' || data == null) ? 0 : data;
  };

  var strDateSplit = function (strDate) {
    var date = strDate.split(".");
    return date[0];
  };

  return {
    dateFormat: dateFormat,
    getToday: getToday,
    pagination: pagination,
    trimStr: trimStr,
    isEmpty: isEmpty,
    strDateSplit: strDateSplit
  };

});
