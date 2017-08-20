<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <script src="<c:url value="/resources/js/jquery.min.js" />"></script>
    <script src="<c:url value="/resources/js/jquery.sumoselect.min.js" />"></script>
    <script src="<c:url value="/resources/js/dataTable.js" />"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/4.10.0/d3.min.js" ></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/dataTable.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/sumoselect.css" />" />
    <style>
        .details-control{
            cursor: pointer;
        }
    </style>
    <style>
        svg {
            float: right;
        }
        div.tooltip {
            position: absolute;
            text-align: left;
            min-width: 100px;
            padding: 3px 8px;
            font: 12px sans-serif;
            background: lightsteelblue;
            border: 0px;
            border-radius: 2px;
            pointer-events: none;
        }
        .line {
            fill: none;
            opacity: 0.1;
            stroke-linejoin: round;
            stroke-linecap: round;
            stroke-width: 2;
        }
        .dataPoint {
            r: 2;
            opacity: 0.2;
            stroke-opacity: 0.2;
        }
        .selected > .line {
            opacity: 0.9;
            stroke-width: 2;
        }
        .selected > .dataPoint {
            r: 3;
            opacity: 0.6;
        }
        tr td{
            text-align: right;
        }
        tr.graphRow td {
            border-bottom: 1px solid #666666;
        }
        .axis {
            opacity: 0.4;
        }
        .graph:hover > .line {
            opacity: 0.8;
            stroke-width: 3.2;
            z-index: 1000;
        }
        .graph > .dataPoint {
            opacity: 0.4;
        }
        .graph:hover > .dataPoint {
            r: 3;
            opacity: 0.6;
            fill: orange;
            z-index: 1001;
        }
    </style>
</head>
<body>
<select onchange="updateGoLink()" id="selectIndustry">
    <option value="">-- select industry --</option>
    <c:forEach var="industry" items="${industries}">
        <option ${industry == selectedIndustry ? "selected" : ""}>${industry}</option>
    </c:forEach>
</select>
<a id="goLink" href="/dt/${selectedIndustry}"><button>Go</button></a>

<br/><br/>
<c:if test="${not empty industryData}" >
    <button onclick="filterSelectedStocks()">View Selected</button>
    <table id="example" class="display" cellspacing="0" width="100%">
        <thead>
        <tr>
            <th>Metric</th>
            <th>Report Type</th>
            <th class="col_stock">Stock</th>
            <th></th>
            <th>2011</th>
            <th>2012</th>
            <th>2013</th>
            <th>2014</th>
            <th>2015</th>
            <th>2016</th>
            <th>2017</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="row" items="${industryData}">
            <tr data-stock="${row.name}">
                <td>${row.ratio}</td>
                <td>${row.reportType}</td>
                <td>${row.name}</td>
                <td class="details-control"><img src="https://cdn4.iconfinder.com/data/icons/charts-4/24/Line-Chart_1-512.png" height="15px" /></td>
                <td>${row.year_2011}</td>
                <td>${row.year_2012}</td>
                <td>${row.year_2013}</td>
                <td>${row.year_2014}</td>
                <td>${row.year_2015}</td>
                <td>${row.year_2016}</td>
                <td>${row.year_2017}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>
<h4>D3 Lab</h4>
<script>
    var datatable = null;
    var selectedStocks = [];

    $(document).ready(function() {
        initializeTable();
    });

    ///************* Graph ************//
    // set the dimensions and margins of the graph
    var margin = {top: 20, right: 20, bottom: 30, left: 40},
        width = 900 - margin.left - margin.right,
        height = 200 - margin.top - margin.bottom;
    // parse the date / time
    var parseTime = d3.timeParse("%Y-%m-%d");
    // define the line
    var valueline = d3.line()
        .x(function(d) { return x(d.date); })
        .y(function(d) { return y(d.value); }).curve(d3.curveMonotoneX);
    // set the ranges
    var x = d3.scaleTime().range([0, width]);
    var y = d3.scaleLinear().range([height, 0]);
    var formatTime = d3.timeFormat("%e %b, %Y");
    var tooltip = d3.select("body").append("div")
        .attr("class", "tooltip")
        .style("opacity", 0);
    var bseDiv = d3.select("body").append("div")
        .attr("class", "");
    var selectedMetrics = [];
    var minDate=parseTime("2011-01-01"), maxDate=parseTime("2017-12-31");
    var myDataCache = [];



    var bseMapData = {
        "equity_Share_Capital" : "Equity Share Capital",
        "EPS" : "Diluted EPS",
        "roce" : "Return On Capital Employed(%)",
        "P_by_L" : "P/L After Tax from Ordinary Activities",
    };
    function initializeGraph (d ) {
        var div = $("<td colspan='8'>");
        $(div).attr("style","padding:1px");

        var myData;
        if (d.name in myDataCache) {
            myData = $.parseJSON(myDataCache[d.name]);
            createChart(myData, div);
        }else{
            var jqxhr = $.ajax( "/gph/${selectedIndustry}/"+d.name, function(data) {
                console.log( "success" );
            })
            .done(function(data) {
                myData = $.parseJSON(data);
                myDataCache[d.name] = JSON.stringify(myData);
                createChart(myData, div);
            })
            .fail(function() {
                console.log( "error" );
            })
            .always(function() {
                console.log( "complete" );
            });
        }
        return div;
    }
    function createChart(myData, div){
        for (var bse in myData){
            var bseData = myData[bse];
            for (var metric in bseData){
                myData[bse][metric].sort(compareDate);
                myData[bse][metric].forEach(function(d) {
                    d.date = parseTime(d.date);
                    d.value = +d.value;
                });
            }
        }
        for (var bse in myData){
            drawChart(myData, div, bse);
        }
    };

    function drawChart(myData, div, bse) {
        var bseData = myData[bse];

        //d3.select($(div).get(0));

        var svg = d3.select($(div).get(0)).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform",
                "translate(" + margin.left + "," + margin.top + ")");

        //$(div).append(svg);

        var minValue=null,
            maxValue=null;
        for (var metric in bseData){
            var tmp = null;
            if (selectedMetrics.length==0 || selectedMetrics.indexOf(metric) > -1) {
                console.debug(metric + " is selected");
                tmp = d3.max(bseData[metric].map(function (datum) { return datum.value; }));
                maxValue = maxValue==null ? tmp : Math.max(tmp, maxValue);
            }
            tmp = d3.min(bseData[metric].map(function (datum) { return datum.value; }));
            minValue = minValue==null ? tmp : Math.min(tmp, minValue);
        }
        x.domain([minDate, maxDate]);
        y.domain([minValue, maxValue]);
        for(var metric in bseData){
            drawLine(myData, svg, bse, metric);
        }
        // Add the X Axis
        svg.append("text").text(bseMapData[bse]);
        svg.append("g")
            .attr("class", "axis")
            .attr("transform", "translate(0," + (height) + ")")
            .call(d3.axisBottom(x)
                .tickFormat(d3.timeFormat("%Y-%b")))
            .selectAll("text")
            .style("text-anchor", "end")
            .attr("dx", "4em")
            .attr("dy", "1.2em");
            //.attr("transform", "rotate(-35)");
        // Add the Y Axis
        svg.append("g").call(d3.axisLeft(y));
        // append the svg obgect to the body of the page
        // appends a 'group' element to 'svg'
        // moves the 'group' element to the top left margin

        return svg;
    }

    function drawLine(myData, svg, bse, metric) {
        var data = myData[bse][metric];
        var isSelected = "";
        if (selectedMetrics.indexOf(metric)>-1){
            isSelected = " selected ";
        };
        var group = svg.append("g")
            .attr("id", bse+"_"+metric)
            .attr("class", "graph graph_"+metric + isSelected)
            .on("click", function(d){
                var index = selectedMetrics.indexOf(metric);
                if (index > -1){
                    $(".graph_"+metric).removeClass("selected");
                    selectedMetrics.splice(index, 1);
                }else{
                    $(".graph_"+metric).addClass("selected");
                    selectedMetrics.push(metric);
                }
                bseDiv.html(JSON.stringify(selectedMetrics));
            })
            .on("mouseover", function(d) {
                tooltip.transition()
                    .duration(200)
                    .style("opacity", .9);
                tooltip.html("Metric:" + metric)
                    .style("left", (d3.event.pageX + 5) + "px")
                    .style("top", (d3.event.pageY ) + "px");
            })
            .on("mouseout", function(d) {
                tooltip.transition()
                    .duration(500)
                    .style("opacity", 0);
            });
        // Add the valueline path.
        group.append("path")
            .data([data])
            .attr("class", "line " + bse + " bseID" + metric)
            .attr("id", "line_"+bse+"_bseID"+metric)
            .attr("d", valueline)
            .attr("stroke", "black" /*metricsMapData[metric].color */)
        ;
        // add the dots with tooltips
        group.selectAll("dot")
            .data(data)
            .enter().append("circle")
            .attr("class", "dataPoint")
            .attr("fill", "transparent")
            .attr("stroke", "black"/*metricsMapData[metric].color*/)
            .attr("cx", function(d) { return x(d.date); })
            .attr("cy", function(d) { return y(d.value); })
            .on("mouseover", function(d) {
                tooltip.transition()
                    .duration(200)
                    .style("opacity", .9);
                tooltip.html(formatTime(d.date) + ": <b>" + d.value + "</b><br/>Metric:" + metric)
                    .style("left", (d3.event.pageX + 5) + "px")
                    .style("top", (d3.event.pageY ) + "px");
                event.stopPropagation();
            })
        ;
    }

    function compareDate(a,b) {
        if (a.date < b.date)
            return -1;
        if (a.date > b.date)
            return 1;
        return 0;
    }






    /*********** data table functions  ****************/
    function initializeTable() {
        $("#selectIndustry").SumoSelect({search: true});

        datatable = $('#example').DataTable({
            "columns": [
                { "data": "ratio" },
                { "data": "reportType" },
                { "data": "name" },
                { "data": "Details", "orderable": false },
                { "data": "Year_2011" },
                { "data": "Year_2012" },
                { "data": "Year_2013" },
                { "data": "Year_2014" },
                { "data": "Year_2015" },
                { "data": "Year_2016" },
                { "data": "Year_2017" }
            ],
            order : [[2, 'asc']],
            fixedHeader: true,
            paging: false,
            dom: '<"top">rt<"bottom"i><"clear">',
            createdRow: function (row, data, index) {
                $(row).attr("data-stock", data.name);
            },
            drawCallback: function( settings ) {
                $("tr.selected").removeClass('selected');
                selectedStocks.forEach(function (value) {
                    var selector = "[data-stock='"+ value +"']";
                    $(selector).addClass('selected');
                })
            },
            initComplete: function () {
                this.api().columns([0, 1]).every( function (index) {
                    var column = this;
                    var select = $('<br/><select multiple style="width: 85%;"></select>')
                        .appendTo( $(column.header()) )
                        .on( 'change', function () {
//                            var val = $.fn.dataTable.util.escapeRegex(
//                                $(this).val()
//                            );
                            var val = $(this).val().join("|");
                            column.search( val ? '^'+val+'$' : '', true, false )
                                .draw();
                        });

                    column.data().unique().sort().each( function ( d, j ) {
                        select.append( '<option value="'+d+'">'+d+'</option>' )
                    } );
                    select.SumoSelect();
                    $(select).click(function( event ) {
                        event.stopPropagation();
                    });
                    $(".optWrapper").click(function( event ) {
                        event.stopPropagation();
                    });
                } );
            }
        });

        $('#example tbody').on( 'click', 'tr', function () {
            var stock = $(this).attr("data-stock");
            var index = selectedStocks.indexOf(stock);
            if (index<0){
                selectedStocks.push(stock);
            }else{
                selectedStocks.splice(index, 1);
            }
            $("tr.selected").removeClass('selected');
            selectedStocks.forEach(function (value) {
                var selector = "[data-stock='"+ value +"']";
                $(selector).addClass('selected');
            })
        });

        // Add event listener for opening and closing details
        $('#example tbody').on('click', 'td.details-control', function (event) {
            event.stopPropagation();
            var tr = $(this).closest('tr');
            var row = datatable.row( tr );

            if ( row.child.isShown() ) {
                // This row is already open - close it
                row.child.hide();
                tr.removeClass('shown');
            }
            else {
                // Open this row
                //row.child( initializeGraph(row.data()) ).show();
                var rowIdx = "abc";
                row.child('').show();

                $(row.child()).empty().append($("<td colspan='3'>"))
                    .append(initializeGraph(row.data()));
                $(row.child()).addClass("graphRow");

                tr.addClass('shown');
            }
        } );
    }

    function updateGoLink() {
        $("#goLink").attr("href", "/dt/" + $("#selectIndustry").val());
    }
    
    function filterSelectedStocks() {
        var val = selectedStocks.join("|");
        selectedStocks = [];
        datatable.columns([2]).every( function (index) {
            console.log(this);
            var column = this;
            column.search( val ? '^'+val+'$' : '', true, false )
                .draw();
        });
    }

</script>
</body>
</html>