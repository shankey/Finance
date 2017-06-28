var dateFormat = "DD-MM-YYYY";
(function () {
    var myConnector = tableau.makeConnector();



    myConnector.getSchema = function (schemaCallback) {
        var cols = [
            { id : "PROFIT_type", alias : "reportType", dataType : tableau.dataTypeEnum.string },
            { id : "PROFIT_bseId", alias : "bseId", dataType : tableau.dataTypeEnum.int },
            { id : "PROFIT_reportDate", alias : "reportDate", dataType : tableau.dataTypeEnum.string },
            { id : "PROFIT_key", alias : "reportKeyMapping", dataType : tableau.dataTypeEnum.string },
            { id : "PROFIT_value", alias : "reportValue", dataType : tableau.dataTypeEnum.float }
        ];

        var tableInfo = {
            id : "profits",
            alias : "profits",
            columns : cols
        };

        schemaCallback([tableInfo]);
    };

    myConnector.getData = function(table, doneCallback) {
        $.getJSON("http://localhost:8080/spring-mvc-demo/profits.html", function(resp) {
            var feat = resp,
                tableData = [];

            console.log(feat);

            // Iterate over the JSON object
            for (var i = 0, len = feat.length; i < len; i++) {
                tableData.push({
                    "PROFIT_type": feat[i].reportType,
                    "PROFIT_bseId": feat[i].bseId,
                    "PROFIT_reportDate": feat[i].reportDate,
                    "PROFIT_key": feat[i].reportKeyMapping,
                    "PROFIT_value": feat[i].reportValue
                });
            }

            table.appendRows(tableData);
            doneCallback();
        });
    };

    tableau.registerConnector(myConnector);
})();

$(document).ready(function () {
    $("#submitButton").click(function () {
        tableau.connectionName = "Net Profit Feed";
        tableau.submit();
    });
});