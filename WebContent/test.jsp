<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    %>
<%@ page import="util.Test" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		if(Test.flag == 0){
			Test.flag = 1;
		}else{
			Test.flag = 2;
		}
	%>
	<%= Test.flag%>
	<h1>Testjsp project, test for deploying to directory webapps, hhh</h1>
</body>
</html>