/**
 * the rpctrace chain controller
 *
 * @author Aiur
 */
define(['controllers/controllers', 'common/util', 'common/constant', 'jedate', 'services/dataService'],
    function (controllers, util, constant, jedate) {
      'use strict';
      controllers.controller('ChainController', ['$scope', 'DataService', function ($scope, DataService) {

        $scope.chainPageSize = 15;
        var itemStats = {
          close: "glyphicon-chevron-right",
          open: "glyphicon-chevron-down",
          end: "glyphicon-minus",
          loading: "glyphicon-repeat"
        };
        var traceItemBuilder = {
          titleRetract: 18,
          buildItem: function (id, traceId, itemName, msg, detail, start, length, rule, level, stats) {
            var title = this.buildTitle(itemName, stats);
            var scale = this.buildScale(msg, detail, start, length, rule);
            var itemStart = "<div class='trace-chain-item' level='" + level + "' traceId='" + traceId + "' id='" + id + "'>";
            var itemEnd = "</div>";

            return itemStart + title + this.scalePlate + scale + itemEnd;
          },
          buildTitle: function (itemName, stats) {
            return "<div class='title'><div><span><i class='glyphicon " + stats + "'></i></span> " + itemName + "</div></div>";
          },
          buildScale: function (msg, detail, start, length, rule) {
            var line = this.buildScaleLine(msg, start, length, rule);
            var floatDetail = this.buildScaleFloatDetail(detail);
            return "<div class='scale'>" + line + floatDetail + "</div>";
          },
          buildScaleLine: function (msg, start, length, rule) {
            var left = start / rule * 100;
            var width = length / rule * 100;
            width = width > 100 ? 100 : width;
            return "<div class='line' style='left: " + left + "%;width: " + width + "%'>" + msg + "</div>";
          },
          buildScaleFloatDetail: function (detail) {
            if (detail === null) {
              return "";
            }
            return "<div class='float-detail'>" + detail + "</div>";
          },
          scalePlate: "<div class='scale-plate'><div><div class='node-0'></div></div><div><div class='node-1'></div></div><div><div class='node-2'></div></div><div><div class='node-3'></div></div><div><div class='node-4'></div><div class='node-5'></div></div></div>"
        };

        $scope.buildTraceInfo = function (data, prevLastRow, newLastRow) {
          var root = "root";
          $scope.itemStats = itemStats;
          var builder = traceItemBuilder;
          doBuild(data, prevLastRow, newLastRow);

          function doBuild(data, prevLastRow, newLastRow) {
            var spansArr = [];
            var spans;
            var rule = 5;
            // 遍历所有 trace, 获取耗时最高值和最小开始时间。
            for (var i = 0; i < data.length; i++) {
              var dto = data[i];
              spans = format(dto);
              var rootSpans = spans[root];

              for (var key in rootSpans) {
                var consume = getTimeConsume(rootSpans[key]);
                rule = rule < consume ? consume : rule;
              }
              spansArr.push(spans);
            }
            // 构建标尺。
            rule = buildScalePlate(rule);
            // 构建分页。
            if ($scope.reBuildPagination) {
              $scope.reBuildPagination(prevLastRow, newLastRow)
            }
            // 清空图形。
            $(".trace-chain-item[level]").remove();
            // 构建每一个 span
            for (var x = 0; x < spansArr.length; x++) {
              spans = spansArr[x];
              buildSpan(spans, root, 0, rule, 1, null);
            }
          }

          function format(data) {
            var spanCSArr = data.spans;
            var spans = {};
            for (var i = 0; i < spanCSArr.length; i++) {
              var spanCS = spanCSArr[i];
              for (var k in spanCS) {
                var key = k.toString();
                var span = spanCS[key];
                var id = span.id;
                var parentId = span.parentId || root;
                var tag = key.substring(key.length - 1);
                var sons = spans[parentId];
                sons = $.isEmptyObject(sons) ? {} : sons;
                sons[id] = $.isEmptyObject(sons[id]) ? {} : sons[id];
                sons[id][tag] = span;
                spans[parentId] = sons;
              }
            }
            return spans;
          }

          /**
           * 同级 Span 按照开始时间进行排序。
           * @param slSpans
           * @returns {Array}
           */
          function sameLevelSort(slSpans) {
            var arr = [];
            for (var id in slSpans) {
              arr.push(slSpans[id]);
            }
            if (arr.length === 1) {
              return arr;
            }
            arr.sort(function (a, b) {
              a = a.c || a.s;
              b = b.c || b.s;
              return b.annotations[0].timestamp - a.annotations[0].timestamp;
            });
            return arr;
          }

          /**
           * 获取时间消耗值。
           * @param span
           * @returns {number}
           */
          function getTimeConsume(span) {
            var c = span.c;
            if (c) {
              var an = c.annotations;
              if (an) {
                return an[1].timestamp - an[0].timestamp;
              }
            }
            return 0;
          }

          /**
           * 获取 span 的开始时间和时间消耗。
           * @param span
           * @returns {{consume: number, minStart: Number}}
           */
          function getConsumeAndMinStart(span) {
            var c = span.c;
            if (c) {
              var an = c.annotations;
              if (an) {
                return {
                  consume: an[1].timestamp - an[0].timestamp,
                  minStart: an[0].timestamp
                }
              }
            }
            return {consume: 5, minStart: Number.MAX_VALUE};
          }

          /**
           * 构建标尺及尺度。
           * @param rule 最大耗时
           */
          function buildScalePlate(rule) {
            var dim = rule / 5;
            var radix = parseInt(dim.toString().split(".")[0]);
            var len = radix.length;
            var mu = ((len - 1) * 10);
            if (len > 1) {
              dim = parseInt((parseInt((radix / mu).toString().split(".")[0]) + 1)) * mu;
            } else {
              dim = radix + 1;
            }
            $(".scale-plate.plate .node").each(function (i, n) {
              $(n).text((i * dim).toString() + "ms")
            });
            return dim * 5;
          }


          /**
           * 构建 rootSpan, sonSpan
           * rootSpan 与 sonSpan HTML 一致, 但放置方式不一致。
           * @param spans
           * @param parentId
           * @param rule
           * @param level
           * @param traceStart
           */
          function buildSpan(spans, parentId, parentTimeShift, rule, level, traceStart) {
            var slSpans = spans[parentId]; // 同层级 span(sameLevelSpan)
            if (!$.isEmptyObject(slSpans)) {
              slSpans = sameLevelSort(slSpans);
              var nextLevel = level + 1;
              // 遍历同层级 span
              for (var i = 0; i < slSpans.length; i++) {
                var span = slSpans[i];
                var c = span.c || {};
                var s = span.s || {};
                if (!$.isEmptyObject(c) || $.isEmptyObject(s)) {
                  var id = c.id || s.id;
                  var hasSon = !$.isEmptyObject(spans[id]);

                  traceStart = root === parentId ? c.annotations[0].timestamp : traceStart;
                  var html = buildSpanHtml(c, s, parentId, parentTimeShift, rule, level, traceStart, hasSon);
                  if (root === parentId) {
                    $(".trace-chain-panel-detail").append(html);
                  } else {
                    $("#" + parentId).after(html);
                    $("#" + id).hide();
                  }
                  // timeShift = parentTimeShift + cs - sr;
                  // TODO: 计算偏移量，目前认为cs和sr时间趋于相等（忽略了网络传输时间, 防止多个节点部署的时候机器时钟没有完全同步）
                  var timeShift = parentTimeShift + c.annotations[0].timestamp - s.annotations[0].timestamp;
                  buildSpan(spans, id, timeShift, rule, nextLevel, traceStart);
                }
              }
            }
          }

          function buildSpanHtml(c, s, parentId, shift, rule, level, minStartTime, hasSon) {
            var can = c.annotations;
            var timeConsume = can ? can[1].timestamp - can[0].timestamp : "unknown";
            var ss = (c.serviceId || s.serviceId).split(".");

            var id, traceId, itemName, msg, detail, start, length, stats;

            id = c.id || s.id;
            traceId = c.traceId || s.traceId;
            itemName = ss[ss.length - 1];
            msg = " " + timeConsume + "ms";
            detail = buildSpanDetail(c, s);
            start = can ? can[0].timestamp - minStartTime : 0;
            start += shift;
            length = timeConsume;
            stats = level === 1 ? $scope.itemStats.close : hasSon ? $scope.itemStats.open : $scope.itemStats.end;
            return builder.buildItem(id, traceId, itemName, msg, detail, start, length, rule, level, stats);
          }

          function buildSpanDetail(c, s) {
            var spanId = c.id || s.id;
            var parentId = c.parentId || "none";
            var traceId = c.traceId || s.traceId;
            var serviceId = c.serviceId || s.serviceId;
            var name = c.name || s.name;

            var can = c.annotations;
            var san = s.annotations;
            var cban = c.binaryAnnotations;
            var sban = s.binaryAnnotations;
            var timeConsume = can ? (can[1].timestamp - can[0].timestamp) + "ms" : "unknown";

            var csr = "<tr><td >Client:</td></tr>", ssr = "<tr><td >Server:</td></tr>";
            if (!$.isEmptyObject(cban)) {
              for (var i = 0; i < cban.length; i++) {
                csr = csr + "<tr><td>" + cban[i].key + "</td>" +
                    "<td>" + cban[i].type + "</td>" +
                    "<td>" + (cban[i].value || "") + "</td></tr>";
              }
            }
            if (!$.isEmptyObject(can)) {
              csr = csr +
                  "<tr><td>CS:</td><td>" + util.dateFormat(new Date(can[0].timestamp), "yyyy-MM-dd hh:mm:ss.S") + "</td>" +
                  "<td>IP:</td><td>" + can[0].endPoint.ip + "</td></tr>" +
                  "<tr><td>CR:</td><td>" + util.dateFormat(new Date(can[1].timestamp), "yyyy-MM-dd hh:mm:ss.S") + "</td>" +
                  "<td>IP:</td><td>" + can[1].endPoint.ip + "</td></tr>";
            }

            if (!$.isEmptyObject(sban)) {
              for (var x = 0; x < sban.length; x++) {
                ssr = ssr + "<tr><td>" + sban[x].key + "</td>" +
                    "<td>" + sban[x].type + "</td>" +
                    "<td>" + (sban[x].value || "") + "</td></tr>";
              }
            }

            if (!$.isEmptyObject(san)) {
              ssr = ssr +
                  "<tr><td>SR:</td><td>" + util.dateFormat(new Date(san[0].timestamp), "yyyy-MM-dd hh:mm:ss.S") + "</td>" +
                  "<td>IP:</td><td>" + san[0].endPoint.ip + "</td></tr>" +
                  "<tr><td>SS:</td><td>" + util.dateFormat(new Date(san[1].timestamp), "yyyy-MM-dd hh:mm:ss.S") + "</td>" +
                  "<td>IP:</td><td>" + san[1].endPoint.ip + "</td></tr>";
            }

            return "<table>" +
                "<tr><td>Name:</td><td>" + name + "</td><td>SpanId:</td><td>" + spanId + "</td></tr>" +
                "<tr><td>耗时:</td><td colspan='3'>" + timeConsume + "</td></tr>" +
                "<tr><td>ParentId:</td><td>" + parentId + "</td><td>TraceId:</td><td>" + traceId + "</td></tr>" +
                "<tr><td>ServiceId:</td><td colspan='3'>" + serviceId + "</td></tr>" +
                csr + ssr +
                "</table>";
          }
        };


        var initSearchTraceEvent = function () {
          $scope.search = function () {
            $scope.searchHasLastRow(null);
          };
          $scope.searchHasLastRow = function (lastRow) {
            $scope.loading();
            var params = {
              url: "rpctrace/traceInfoList",
              sid: $scope.sid,
              type: $scope.typeSelect,
              startTime: $scope.getTime("startTime"),
              endTime: $scope.getTime("endTime"),
              lastRow: lastRow === "null" ? null : lastRow,
              pageSize: $scope.chainPageSize
            };
            DataService.getData(params, function (res) {
              if (res !== null && res.statCode === "00000") {
                $scope.buildTraceInfo(res.data.traceInfos, lastRow, res.data.newLastRow);
                $scope.loaded();
              }
            });
          }
        };

        /**
         * 分页
         */
        var initPageEvent = function () {

          $("#chainPagination").delegate("li", "click", changePage);
          $scope.reBuildPagination = reBuildPagination;


          function changePage() {
            if ($(this).attr("class") === "disabled") {
              return;
            }
            var lastRow = $(this).attr("lastRow") || null;
            $scope.searchHasLastRow(lastRow);
          }

          function reBuildPagination(prevLastRow, newLastRow) {
            var pages = $("#chainPagination");
            if (prevLastRow === null) {
              pages.empty();
              pages.append("<li class='disabled'><a aria-label='Previous'><span>«</span></a></li>");
              pages.append("<li lastRow='null' class='active'><a>1</a></li>");
              if (newLastRow) {
                pages.append("<li lastRow='" + newLastRow + "'><a>2</a></li>");
                pages.append("<li lastRow='" + newLastRow + "'><a aria-label='Next'><span>&raquo;</span></a></li>");
              } else {
                pages.append("<li class='disabled'><a aria-label='Next'><span>&raquo;</span></a></li>");
              }
            } else {
              var lis = pages.find("li[lastRow]");
              pages.empty();
              var lastRows = [];
              var pushed = {"null": true};
              for (var i = 0; i < lis.length; i++) {
                var liLastRow = $(lis[i]).attr("lastRow");
                if (!pushed[liLastRow]) {
                  lastRows.push($(lis[i]).attr("lastRow"));
                }
                pushed[liLastRow] = true;
              }
              if (prevLastRow === lastRows[lastRows.length - 1]) {
                lastRows = lastRows.concat([newLastRow]);
              }
              lastRows.sort();
              lastRows = ["null"].concat(lastRows);
              if (lastRows.length >= 2) {
                pages.append("<li id='prev' lastRow='" + lastRows[lastRows.length - 2] + "'>" +
                    "<a aria-label='Previous'><span>«</span></a></li>");
              } else {
                pages.append("<li class='disabled'><a aria-label='Previous'><span>«</span></a></li>");
              }

              for (var x = 0; x < lastRows.length; x++) {
                if (lastRows[x] === null) {
                  continue;
                }
                var active = lastRows[x] === prevLastRow ? "class='active'" : "";
                pages.append("<li " + active + " lastRow='" + lastRows[x] + "'><a>" + (x + 1) + "</a></li>");
                if (active !== "") {
                  if (x - 1 > -1) {
                    $("#prev").attr("lastRow", lastRows[x - 1]);
                  } else {
                    $("#prev").addClass("disabled");
                  }
                }
              }
              if (newLastRow !== null) {
                pages.append("<li lastRow='" + newLastRow + "'><a aria-label='Next'><span>&raquo;</span></a></li>");
              }
            }
          }
        };

        /**
         * 数据加载中
         */
        var loadingEvent = function () {
          $scope.loading = loading;
          $scope.loaded = loaded;

          function loading() {
            $(".trace-chain-panel-detail").addClass("loading");
          }
          function loaded() {
            $(".trace-chain-panel-detail").removeClass("loading");
          }
        };
        /**
         * 数据标记图标的转换改变事件。
         */
        var initItemTagChangeEvent = function () {
          var closeCls = "." + itemStats.close;
          var openCls = "." + itemStats.open;
          var endCls = "." + itemStats.end;
          var loadingCls = "." + itemStats.loading;
          var open = function () {
            $(this).addClass(itemStats.open).removeClass(itemStats.close);
            var $item = $($(this).parents(".trace-chain-item")[0]);
            var traceId = $item.attr("traceid");
            var sons = $item.nextAll("[traceid='" + traceId + "']");
            sons.each(function (i, n) {
              var line = $(n).find(".line");
              line.hide();
              $(n).show();
              line.show(500);
            });
          };
          var close = function () {
            $(this).removeClass(itemStats.open).addClass(itemStats.close);
            var $item = $($(this).parents(".trace-chain-item")[0]);
            var traceId = $item.attr("traceid");
            var sons = $item.nextAll("[traceid='" + traceId + "']");
            sons.each(function (i, n) {
              $(n).hide();
            });
          };
          var loaded = function () {
            $(this).removeClass(itemStats.loading)
                .addClass(itemStats.open)
                .css("transform", "rotate(0deg)");
          };
          $(".trace-chain-panel-detail")
              .delegate(closeCls, "click", open)
              .delegate(openCls, "click", close);
        };

        /**
         * 加载中图表的旋转。
         */
        var initLoadingTagRotateEvent = function () {
          var cls = "." + itemStats.loading;
          var rotate = function () {
            var angle = (new Date() / 5 % 360) + "deg";
            $(cls).css("transform", "rotate(" + angle + ")");
          };
          setInterval(rotate, 50);
        };

        /**
         * span 详细信息的浮动与锚定。
         */
        var initSpanDetailFloatEvent = function () {
          function anchor() {
            var that = $(this);
            if (that.hasClass("anchor")) {
              that.removeClass("anchor");
            } else {
              that.addClass("anchor");
            }
          }

          function detailAnchor() {
            $(this).addClass("anchor");
          }

          function detailUnAnchor() {
            $(this).removeClass("anchor");
          }

          function coord() {
            var that = $(this);
            that.next().css("left", that.css("left"));
          }

          $(".trace-chain-panel-detail")
              .delegate(".scale", "click", anchor)
              .delegate(".line", "mouseover", coord)
              .delegate(".anchor .float-detail", "mouseenter", detailAnchor)
              .delegate(".anchor .float-detail", "mouseleave", detailUnAnchor)
        };

        /**
         * 时间范围选择插件相关事件。
         */
        var initDateRangeEvent = function () {
          var now = new Date();
          $scope.today = util.getToday();
          $scope.startTime = util.getToday();
          $scope.startTimeHMS = util.dateFormat(now, "hh:mm:ss");
          $scope.endTime = util.getToday();
          $scope.endTimeHMS = util.dateFormat(new Date(now * 1 + 1000), "hh:mm:ss");
          $scope.dateRange = function ($event, name) {
            $('.btn-danger').addClass('hidden');
            $event.preventDefault();
            $event.stopPropagation();
            $scope[name] = true;
          };
          $scope.getTime = getTime;


          function getTime(model) {
            var time = util.dateFormat($scope[model], "yyyy-MM-dd") + " " + $scope[model + "HMS"];
            return util.parseDate(time, "yyyy-MM-dd HH:mm:ss") * 1;
          }
        };

        var serviceInfoEvent = function () {
          initIfaceSelected();
          $scope.ifaceChange = ifaceChange;
          $scope.methodChange = methodChange;
          function initIfaceSelected() {
            var params = {
              url: 'rpctrace/names'
            };
            DataService.getData(params, function (res) {
              if (res !== null && res.statCode === "00000") {
                $scope.ifaces = res.data;
              }
            });
          }

          function ifaceChange() {
            var params = {
              url: 'rpctrace/methods',
              iface: $scope.ifaceSelect
            };
            DataService.getData(params, function (res) {
              if (res !== null && res.statCode === "00000") {
                $scope.methodSelect = null;
                $scope.methods = [];
                $scope.methods = res.data;
              }
            });
          }

          function methodChange() {
            $scope.sid = $scope.ifaceSelect + "_" + $scope.methodSelect;
            $scope.search();
          }
        };

        var initJedate = function () {

          $scope.minDate = {
            format: 'YYYY-MM-DD hh:mm:ss',
            maxDate: $.nowDate(0),
            isinitval: true,
            ishmsVal: true,
            choosefun: function (ele, datas) {
              $(ele).change();
            }
          };
          $scope.maxDate = {
            format: 'YYYY-MM-DD hh:mm:ss',
            maxDate: $.nowDate(1),
            isinitval: true,
            ishmsVal: true,
            choosefun: function (ele, datas) {
              $scope.minDate.maxDate = datas;
              $(ele).change();
            }
          };
          $.jeDate("#startTime", $scope.minDate);
          $.jeDate("#endTime", $scope.maxDate);
          $(".input-group-btn").click(function () {
            $($(this).prev()).click();
          });

          var now = new Date() * 1;
          $scope.startTime = util.dateFormat(new Date(now - 1000), 'yyyy-MM-dd hh:mm:ss');
          $scope.endTime = util.dateFormat(new Date(now), 'yyyy-MM-dd hh:mm:ss');
        };


        var initData = function () {
          // render data
        };
        var initEvent = function () {
          // bind the event
          // initJedate();
          initDateRangeEvent();
          serviceInfoEvent();
          initSpanDetailFloatEvent();
          initItemTagChangeEvent();
          initSearchTraceEvent();
          initPageEvent();
          loadingEvent();

        };


        initData();
        initEvent();
      }
      ])
      ;
    })
;