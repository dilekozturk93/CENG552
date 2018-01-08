<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Invalid Defs Input</title>
</head>
<body>
<%@ include file="index.jsp" %>
<table id="tableResult" border="0" width="100%" cellspacing="0" cellpadding="0">
  <tbody><tr><td width="50%" valign="top">    <font face="Garamond">The input graph is invalid because:<br>
    <font color="red">
    <b>Could not understand the format of the defs. Please check the syntax: a variable name followed by a space-separated sequence of nodes or edges where the varialbes are defined.   <br> Cannot generate a set of test paths to satisfy the coverage</b>
    </font>
    </font>
    <br>
</td></tr>
</tbody>
</table>
</body>
</html>