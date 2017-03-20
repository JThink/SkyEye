/**
 * jquery plugin, render the table
 *
 * @author JThink
 */
(function(fac) {
  // check for AMD usage
  if ((typeof define === "function") && define.amd) {
    define(["jquery"], fac);
  } else {
    // browser globals
    fac(jQuery);
  }
}(function($) {
  $.fn.table = function(pagination, callback, data, pageCount, currentPage) {
    if (data == undefined || data.length == 0) {
      this.empty();
      this.parent().find('.no-data').remove();
      this.parent().append('<span class="no-data">' + 'No data available' + '</span>');
      return;
    }
    this.parent().find('.no-data').remove();
    var target = this;
    var parent = pagination.parent();
    resetData(target, data, currentPage, pageCount);
    var $pagination = pagination.remove().clone();
    parent.append($pagination);
    if (totalPage(data, pageCount) == 1) {
      $pagination.addClass('hidden');
    } else {
      $pagination.removeClass('hidden');
    }
    $pagination.jqPagination({
      link_string: '/?page={page_number}',
      current_page: currentPage,
      page_string: 'pagination',
      max_page: totalPage(data, pageCount), 
      paged: function(page) {
        callback(page);
        resetData(target, data, page, pageCount);
      }
    });
  };

  $.fn.tableNoPaging = function(data, title) {
    if (data == undefined || data.length == 0) {
      this.empty();
      this.parent().find('.no-data').remove();
      this.parent().append('<span class="no-data">' + 'No data available' + '</span>');
      if (title) {
        // render title
        var thead = new String('<thead><tr>');
        $.each(title, function(key, value) {
          thead = thead.concat('<th>' + value + '</th>');
        });
        thead = thead.concat('</tr></thead>');
        this.append(thead);
      }
      return;
    }
    this.parent().find('.no-data').remove();
    this.empty();
    var thead = formatHead(data[0]);
    var tbody = formatBody(data);
    var target = this;
    this.append(thead);
    this.append(tbody);
    $('.expand').unbind();
    $('.collapse').unbind();
    $('.expand').on('click', expand);
  };

  var resetData = function(target, data, page, pageCount) {
    if (data == undefined || data.length == 0) {
      return;
    }
    // empty current content
    target.empty();

    var subData = [];
    var maxCount = 0;
    if (page == totalPage(data, pageCount)) {
      // if is the last page
      maxCount = data.length;
    } else {
      maxCount = page * pageCount
    }
    var count = 0;
    for (var i = (page - 1) * pageCount; i < maxCount; ++i) {
      subData[count++] = data[i];
    }
    // set the date into the tablel
    if (subData == undefined || subData.length == 0) {
      return;
    }
    var thead = formatHead(subData[0]);
    var tbody = formatBody(subData);
    target.append(thead);
    target.append(tbody);

    $('.expand').unbind();
    $('.expand').on('click', expand);
  };

  var totalPage = function(data, pageCount) {
    var mod = data.length % pageCount;
    var page = parseInt(data.length / pageCount);
    return mod == 0 ? page : page + 1;
  };
  var formatHead = function(data) {
    var thead = new String('<thead><tr>');
    $.each(data, function(key, value) {
      if (key == 'children') {
        // continue
        return true;
      }
      thead = thead.concat('<th>' + key + '</th>');
    });
    thead = thead.concat('</tr></thead>');
    return thead;
  };
  var formatBody = function(data) {
    var tbody = new String('<tbody>');
    $.each(data, function(key, value) {
      tbody = tbody.concat('<tr>');
      var count = 0;
      var trs;
      $.each(value, function(k, v) {
        if (k == 'children') {
          // continue
          return true;
        }
        var tdContent;
        if (count == 0 && hasChildren(value)) {
          // if the td is the first
          tdContent = tbody.concat('<td class="expand"><div class="expand-left">' + v + '&nbsp;&nbsp;&nbsp;' + '</div><a href="javascript:void(0)">+</a></td>');
          trs = formatChildren(value['children'], getParentName(value));
        } else {
          tdContent = tbody.concat('<td>' + v + '</td>');
        }
        tbody = tdContent;
        count++;
      });
      tbody = tbody.concat('</tr>');
      tbody = tbody.concat(trs);
    });
    tbody = tbody.concat('</tbody>');
    return tbody;
  };
  var formatChildren = function(data, parent) {
    var tr = new String();
    $.each(data, function(key, value) {
      tr = tr.concat('<tr class="children hidden"' + 'parent=' + parent +'>');
      var count = 0;
      $.each(value, function(k, v) {
        if (count == 0) {
          tr = tr.concat('<td>' + '&nbsp;&nbsp;&nbsp;' + v + '</td>');
        } else {
          if (v == 'view detail') {
            tr = tr.concat('<td><a href="javascript:void(0)" class="btn btn-link view-detail">' + 'view detail' + '</a></td>');
          } else {
            tr = tr.concat('<td>' + v + '</td>');
          }
        }
        count++;
      });
      tr = tr.concat('</tr>');
    });
    return tr;
  };
  var expand = function() {
    var $this = $(this);
    var suffix = $this.children('.expand-left').text().trim();
    var $textIcon = $this.children('a');
    $this.unbind();
    $this.on('click', collapse);
    $textIcon.text($textIcon.text().replace('+', '-'));
    $.each($('.children'), function(key, value) {
      var parent = $(this).attr('parent');
      if (parent == suffix.replace(new RegExp(' ', "gm"), '')) {
        $(this).removeClass('hidden');
      }
    });
  };
  var collapse = function() {
    var $this = $(this);
    var suffix = $this.children('.expand-left').text().trim();
    var $textIcon = $this.children('a');
    $this.unbind();
    $this.on('click', expand);
    $textIcon.text($textIcon.text().replace('-', '+'));
    $.each($('.children'), function(key, value) {
      var parent = $(this).attr('parent');
      if (parent == suffix.replace(new RegExp(' ', "gm"), '')) {
        $(this).addClass('hidden');
      }
    });
  };
  var hasChildren = function(data) {
    var children = data['children'];
    if (children != undefined && children.length != 0) {
      return true;
    }
    return false;
  };
  var getParentName = function(data) {
    var count = 0;
    var parent;
    $.each(data, function(k, v) {
      if (count == 0) {
        parent = v;
      }
      count++;
    });
    return parent.replace(new RegExp(' ', "gm"), '');
  }
}));