
(function () {
    var myConnector = tableau.makeConnector();



    myConnector.getSchema = function (schemaCallback) {
        var cols = [
            { id : "EPS_type", alias : "reportType", dataType : tableau.dataTypeEnum.string },
            { id : "EPS_bseId", alias : "bseId", dataType : tableau.dataTypeEnum.int },
            { id : "EPS_reportDate", alias : "reportDate", dataType : tableau.dataTypeEnum.string },
            { id : "EPS_key", alias : "reportKeyMapping", dataType : tableau.dataTypeEnum.string },
            { id : "EPS_value", alias : "reportValue", dataType : tableau.dataTypeEnum.float }
        ];

        var tableInfo = {
            id : "EPS",
            alias : "EPS",
            columns : cols
        };

        schemaCallback([tableInfo]);
    };

    myConnector.getData = function(table, doneCallback) {
        $.getJSON("http://localhost:8080/spring-mvc-demo/eps.html", function(resp) {
            var feat = resp,
                tableData = [];

            console.log(feat);

            // Iterate over the JSON object
            for (var i = 0, len = feat.length; i < len; i++) {
                tableData.push({
                    "EPS_type": feat[i].reportType,
                    "EPS_bseId": feat[i].bseId,
                    "EPS_reportDate": feat[i].reportDate,
                    "EPS_key": feat[i].reportKeyMapping,
                    "EPS_value": feat[i].reportValue
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
        tableau.connectionName = "EPS Feed";
        tableau.submit();
    });
});