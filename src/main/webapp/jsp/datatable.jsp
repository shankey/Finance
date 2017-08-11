<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <script src="<c:url value="/resources/js/jquery.min.js" />"></script>
    <script src="<c:url value="/resources/js/jquery.sumoselect.min.js" />"></script>
    <script src="<c:url value="/resources/js/dataTable.js" />"></script>
    <link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/dataTable.css" />"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/sumoselect.css" />" />
</head>
<body>
<select class="multiSelect">
    <c:forEach var="industry" items="${industries}">
        <option>${industry}</option>
    </c:forEach>
</select>

<br/><br/>
<button onclick="filterSelectedStocks()">View Selected</button>
<table id="example" class="display" cellspacing="0" width="100%">
    <thead>
    <tr>
        <th>Metric</th>
        <th>Report Type</th>
        <th class="col_stock">Stock</th>
        <th>2011</th>
        <th>2012</th>
        <th>2013</th>
        <th>2014</th>
        <th>2015</th>
        <th>2016</th>
        <th>2017</th>
    </tr>
    </thead>
</table>
<h4>D3 Lab</h4>
<script>
    var datatable = null;
    var selectedStocks = [];

    $(document).ready(function() {
        datatable = $('#example').DataTable({
            ajax : "/resources/data/stock_metric_table" ,
            fixedHeader: true,
            paging: false,
            dom: '<"top">rt<"bottom"i><"clear">',
            createdRow: function (row, data, index) {
                    $(row).attr("data-stock", data[2]);
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
                        // Do something
                    });
                    $(".optWrapper").click(function( event ) {
                        event.stopPropagation();
                        // Do something
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
        $(".multiSelect").SumoSelect();
    });
    
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