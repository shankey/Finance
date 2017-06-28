(function () {
    var myConnector = tableau.makeConnector();

    myConnector.getSchema = function (schemaCallback) {
        var cols = [
            { id : "bseId", alias : "bseId", dataType : tableau.dataTypeEnum.int },
            { id : "high", alias : "high", dataType : tableau.dataTypeEnum.float },
            { id : "low", alias : "low", dataType : tableau.dataTypeEnum.float },
            { id : "open", alias : "open", dataType : tableau.dataTypeEnum.float },
            { id : "close", alias : "close", dataType : tableau.dataTypeEnum.float },
            { id : "priceDate", alias : "priceDate", dataType : tableau.dataTypeEnum.string },
            { id : "volume", alias : "volume", dataType : tableau.dataTypeEnum.int }
        ];

        var tableInfo = {
            id : "prices",
            alias : "prices",
            columns : cols
        };

        schemaCallback([tableInfo]);
    };

    myConnector.getData = function(table, doneCallback) {
        $.getJSON("http://localhost:8080/spring-mvc-demo/prices.html", function(resp) {
            var feat = resp,
                tableData = [];

            console.log(feat);

            // Iterate over the JSON object
            for (var i = 0, len = feat.length; i < len; i++) {
                tableData.push({
                    "bseId": feat[i].bseId,
                    "high": feat[i].high,
                    "low": feat[i].low,
                    "open": feat[i].open,
                    "close": feat[i].close,
                    "priceDate": feat[i].priceDate,
                    "volume": feat[i].volume
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
        tableau.connectionName = "Price Feed";
        tableau.submit();
    });
});