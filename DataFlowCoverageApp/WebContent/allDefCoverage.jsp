<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>All Def Coverage</title>
</head>
<body>

<%@ include file="index.jsp" %>
<table id="tableResult" border="0" width="100%" cellspacing="0" cellpadding="0">
  <tbody><tr><td width="50%" valign="top">    <b>DU Pairs for all variables are:</b><br>
   <table cellspacing="0" cellpadding="0" width="100%" bgcolor="#eeffee" border="1">
   <tbody><tr>
     <th>Variable  All-Def Coverage   </th>
   </tr>
   <tr>
  <c:forEach items="${AllDef}" var="defs" >
     <td>${defs}</td>
	</c:forEach>
   </tr>
   </tbody></table>
	
</table>

</body>
</html>