<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>

<head>
<title>Industries to scrape</title>

<meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</head>

<body>
<div class="container-fluid">
    <div class="checkbox">
        <label><input type="checkbox" onchange="selectAll(this)" />Select All</label>
    </div>
  <form action="mcscrape" method="GET" role="form">
      <c:forEach var="ind" items="${industries}">
          <div class="checkbox">
            <label><input type="checkbox" class="checkbox" name="industry" value="<c:out value="${ind}" />"><c:out value="${ind}" /></label>
          </div>

      </c:forEach>

      <button type="submit" class="btn btn-default">Submit</button>
  </form>
</div>
<script>
    function selectAll(selectAll) {
        if($(selectAll).is(":checked")) {
            $(".checkbox").prop('checked', true);
        }else{
            $(".checkbox").prop('checked', false);
        }
    }
</script>


</body>

</html>