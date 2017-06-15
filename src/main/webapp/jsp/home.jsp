<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
<title>c:out JSTL core tag example</title> </head>

<body>

<c:forEach var="listVar" items="${stockSymbolsList}">
    <c:out value="${listVar.company}" /> <br>
    <c:out value="${listVar.status}" />
</c:forEach>

</body>

</html>