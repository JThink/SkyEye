/**
 * chartUtil js
 *
 * @author Aiur
 */
define(['jquery', 'echarts', 'common/util', 'underscore'], function ($, echarts, util, _) {


  // 取色数组。 macarons
  var colorPalette = [
    '#2ec7c9', '#b6a2de', '#5ab1ef', '#ffb980', '#d87a80',
    '#8d98b3', '#e5cf0d', '#97b552', '#95706d', '#dc69aa',
    '#07a2a4', '#9a7fd1', '#588dd5', '#f5994e', '#c05050',
    '#59678c', '#c9ab00', '#7eb00a', '#6f5553', '#c14089'
  ];


  /**
   * TimeUnit 应用于 getTimePointsArrInRange。
   * interval 的值是为避免越过对应的时间单位而故意设置的数值。
   */
  var TimeUnit = {
    second: {format: "yyyy-MM-dd hh:mm:ss", interval: 999},     // 间隔999毫秒
    minute: {format: "yyyy-MM-dd hh:mm", interval: 59999},      // 间隔59999毫秒
    hour: {format: "yyyy-MM-dd hh", interval: 3599999},         // 间隔3599999毫秒
    day: {format: "yyyy-MM-dd", interval: 84600000},            // 间隔23.5小时
    week: {format: "yyyy-MM:w", interval: 84600000},            // 间隔23.5小时
    month: {format: "yyyy-MM", interval: 2160000000}            // 间隔25天
  };


  var chartDefaultOpt = function () {
    return {
      title: {
        text: "",
        subtext: ""
      },
      tooltip: {
        trigger: "axis",
        showDelay: 0,
        hideDelay: 0
      },
      grid: {
        left: "2%",
        right: "3%",
        bottom: "5%",
        containLabel: true
      },
      legend: {},
      toolbox: {
        show: true,
        feature: {
          magicType: {type: ["bar", "line", "stack", "tiled"]},
          saveAsImage: {backgroundColor: '#FFF'}
        }
      },
      xAxis: {
        type: "category",
        boundaryGap: false,
        minInterval: 1,
        splitLine: {
          show: false
        }

      },
      yAxis: {
        type: "value",
        minInterval: 1,
        min: 0, //eCharts bug. 和 minInterval 同用时在数值小于纵轴分割数时仍会出现小数。
        axisLabel: {
          formatter: function (y) {
            return y && (y.toString().indexOf(".") > -1) ? null : y;
          }
        }


      },
      series: []
    }
  };

  var getTimePointsArrInRange = function (startDate, endDate, intervalMS, format) {
    var date = util.dateFormat(startDate, format);
    var rtn = [date];

    var prev = date;
    var ms = startDate * 1 + intervalMS;
    var max = endDate * 1;
    while (ms < max) {
      date = util.dateFormat(new Date(ms), format);
      if (date != prev) {
        rtn.push(date);
      }
      prev = date;
      ms = ms + intervalMS;
    }
    date = util.dateFormat(endDate, format);
    if (date != prev) {
      rtn.push(date);
    }

    return rtn;
  };

  var generateSuccFailData = function (params, resData, name) {
    var data = {};
    data[name] = {};
    var line, time, succ, fail;
    for (var x = 0; x < resData.length; x++) {
      line = resData[x];
      time = line["time"];
      succ = line["succ"];
      fail = line["fail"];
      data[name][time] = {
        succ: succ,
        fail: fail
      }
    }
    var timeArr = getTimePointsArrInRange(params.beginDate, params.endDate, params.interval, params.format);
    var sf;
    var succArr = [], failArr = [];
    var xAxisLen = params.scope == "week" ? 4 : 5;
    for (var i = 0; i < timeArr.length; i++) {
      time = timeArr[i];
      sf = data[name][time];
      sf = sf == null ? {} : sf;
      time = time.substring(time.length - xAxisLen, time.length);

      succArr.push({name: time, value: sf.succ == null ? 0 : sf.succ});
      failArr.push({name: time, value: sf.fail == null ? 0 : sf.fail});
      // var a = parseInt(Math.random() * 10000 + 90000);
      // var b = parseInt(Math.random() * 5000);
      // succArr.push({name: time, value: sf.succ == null ? a : sf.succ});
      // failArr.push({name: time, value: sf.fail == null ? b : sf.fail});
    }

    var series = [
      {
        name: "成功", data: succArr,
        // stack: '总量',
        type: "line", smooth: true,
        symbol: "none", barMinHeight: 1,
        areaStyle: {
          normal: {
            // color: colorPalette[0],
            opacity: 0
          }
        }
      },
      {
        name: "失败", data: failArr,
        // stack: '总量',
        type: "line", smooth: true,
        symbol: "none", barMinHeight: 1,
        areaStyle: {
          normal: {
            // color: colorPalette[1],
            opacity: 0.3
          }
        }
      }
    ];
    return series;
  };


  var generateStackSeries = function (series) {
    for (var i = 0; i < series.length; i++) {
      var sd = series[i].data;
      var ci = i % colorPalette.length;
      var data = [];
      for (var x = 0; x < sd.length; x++) {
        data.push(sd[x].value)
      }
      series[i].data = data;
      series[i].stack = '总量';
      series[i].areaStyle = {
        normal: {color: colorPalette[ci], opacity: 0.05}
      };
      series[i].symbol = "none";
      series[i].smooth = true;
    }
    return series;
  };

  var generateLegend = function (series) {
    var arr = [];
    for (var i = 0; i < series.length; i++) {
      arr.push(series[i].name)
    }
    return arr;
  };

  var generateXAxisData = function (series) {
    var tmp = {};
    for (var i = 0; i < series.length; i++) {
      for (var j = 0; j < series[i].data.length; j++) {
        tmp[series[i].data[j].name] = null;
      }
    }
    var rtn = [];
    for (var key in tmp) {
      rtn.push(key);
    }
    return rtn;
  };

  var generateOption = function (params, data, oldOption) {
    var option = new chartDefaultOpt();
    var series = generateSuccFailData(params, data, params.uniqueName);

    // 维持通过 toolbox 进行的显示变换
    if (!$.isEmptyObject(oldOption)) {
      var toolboxStatus = oldOption.toolbox[0].feature.magicType.iconStatus;
      if (!$.isEmptyObject(toolboxStatus)) {
        option.toolbox.feature.magicType.iconStatus = toolboxStatus;
        var isBar = toolboxStatus.bar == "emphasis";
        var isStack = toolboxStatus.stack == "emphasis";
        for (var i = 0; i < series.length; i++) {
          if (isBar) {
            series[i].type = "bar";
            option.xAxis.boundaryGap = true;
          }
          if (isStack) {
            series[i].stack = '总量';
          }
        }
      }
    }

    option.series = series;
    option.legend.data = generateLegend(series);
    option.xAxis.data = generateXAxisData(series);


    return option;
  };


  var getInputs = function (selectorStr) {
    var rtn = {};
    var $inputs = $(selectorStr);
    var $input;
    for (var i = 0; i < $inputs.length; i++) {
      $input = $($inputs[i]);
      rtn[$($inputs[i]).attr("name")] = $input.val();
    }
    return rtn;
  };


  return {
    getInputs: getInputs,
    generateOption: generateOption,
    TimeUnit: TimeUnit
  };

});
