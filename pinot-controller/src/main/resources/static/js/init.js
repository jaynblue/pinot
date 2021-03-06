var JS_BEAUTIFY_SETTINGS = {
  indent_size               : 4,
  indent_char               : ' ',
  preserve_newlines         : true,
  braces_on_own_line        : true,
  keep_array_indentation    : false,
  space_after_anon_function : true
};


$(document).ready(function() {
  EDITOR = CodeMirror.fromTextArea(document.getElementById('query-maker'), {
    lineWrapping: true,
    lineNumbers: true,
    mode: "sql",
    theme: "elegant"
  });

  RESULTS = CodeMirror.fromTextArea(document.getElementById('results-maker'), {
    mode         : 'ruby',
    value        : "",
    indentUnit   : 4,
    lineWrapping : true,
    lineNumbers  : false
  });

  var windowHeight = $(window).height();
  var documentHeight = $(document).height();

  var qH = (.20) * windowHeight + 10;
  var rH = (.60) * windowHeight;

  $('.query-box').css('min-height', qH + 'px');
  $('.query-results').css('min-height', rH + 'px');

  HELPERS.printTables(function() {
    $('.table-name-entry').click(function(e) {
      var tableName = e.currentTarget.attributes[1].nodeValue;
      HELPERS.decorateTableSchema(tableName, rH);
      EDITOR.setValue('');
      HELPERS.populateDefaultQuery(tableName);
      $("#execute-query").click();
    });
  }, qH);

  var table;

  $("#execute-query").click(function() {
    // execute query and draw the results
    var query = EDITOR.getValue().trim();
    var traceEnabled = document.getElementById('trace-enabled').checked;
    HELPERS.executeQuery(query, traceEnabled, function(data) {
      RESULTS.setValue(js_beautify(data, JS_BEAUTIFY_SETTINGS));

      var queryResponse = null;
      try {
        queryResponse = HELPERS.getAsObject(data);
      } catch (err) {
        if (table) {
          table.destroy();
          table = null;
        }

        HELPERS.resetResultsTable();
        return;
      }

      var dataArray = [];
      var columnList = [];

      if (queryResponse) {
        if (queryResponse["selectionResults"]) {
          // Selection query
          columnList = _.map(queryResponse.selectionResults.columns, function (columnName) {
            return {"title": columnName};
          });
          dataArray = queryResponse.selectionResults.results;
        } else if (queryResponse["aggregationResults"] && queryResponse.aggregationResults.length > 0
            && !queryResponse.aggregationResults[0]["groupByResult"]) {
          // Simple aggregation query
          columnList = _.map(queryResponse.aggregationResults, function (aggregationResult) {
            return {"title": aggregationResult.function};
          });

          dataArray.push(_.map(queryResponse.aggregationResults, function (aggregationResult) {
            return aggregationResult.value;
          }));
        } else if (queryResponse["aggregationResults"] && queryResponse.aggregationResults.length > 0
            && queryResponse.aggregationResults[0]["groupByResult"]) {
          // Aggregation group by query
          var columns = queryResponse.aggregationResults[0].groupByColumns;
          columns.push(queryResponse.aggregationResults[0].function);
          columnList = _.map(columns, function (columnName) {
            return {"title": columnName};
          });

          dataArray = _.map(queryResponse.aggregationResults[0].groupByResult, function (aggregationGroup) {
            var row = aggregationGroup.group;
            row.push(aggregationGroup.value);
            return row;
          });
        }
      }

      HELPERS.resetResultsTable();

      table = $('#query-results-table').DataTable({
        destroy: true,
        data: dataArray,
        columns: columnList,
        scrollX: true
      });
    })
  });
});

var HELPERS = {
  resetResultsTable: function() {
    $('#query-results-table-wrapper')
        .empty()
        .append("<table id=\"query-results-table\" class=\"display\" width=\"100%\"/></table>");
  },

  printTables : function(callback, qh) {
    $.get("/tables", function(data) {
      var source   = $("#table-names-template").html();
      var template = Handlebars.compile(source);
      var dataObj = HELPERS.getAsObject(data);
      var d = template(dataObj);
      $(".schema-list-view").html(d);
      $("#table-names").DataTable({
        "searching":true,
        "scrollY": qh + "px",
        "scrollCollapse": true,
        "paging": false,
        "info": false
      });
      callback();
    });
  },

  getAsObject : function(str) {
    if (typeof str === 'string' || str instanceof String) {
      return JSON.parse(str);
    }
    return str;
  },

  decorateTableSchema : function(tableName, th) {
    $.get("/tables/" + tableName + "/schema/", function(data){
      var schema = HELPERS.getAsObject(data);
      var source   = $("#table-schema-template").html();
      var template = Handlebars.compile(source);
      var d = template(schema);
      $(".schema-detail-view").html(d);
      $("#table-schema").DataTable({
        "searching":false,
        "scrollY": th + "px",
        "scrollCollapse": true,
        "paging": false,
        "info": false
      });
    });
  },

  populateDefaultQuery: function(tableName) {
    EDITOR.setValue("select * from " + tableName + " limit 10");
  },

  populateQuery: function(query) {
    var query = EDITOR.getValue().trim();
  },

  executeQuery: function(query, traceEnabled, callback) {
    var url = "/pql?pql=" + encodeURIComponent(query) +"&trace=" + traceEnabled;
    $.ajax({
      type: 'GET',
      url: url,
      contentType: 'application/json; charset=utf-8',
      success: function (text) {
        callback(text);
      },
      dataType: 'text'
    })
    .complete(function(){
    });
  }
};
