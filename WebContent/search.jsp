<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/json; charset=GBK" pageEncoding="GBK"%>
<%@ page import="java.lang.String" %>
<%@ page import="Util.SearchUtil" %>
<%
	String keyWord = request.getParameter("key");
	keyWord = (keyWord == null) ? "" : keyWord;
	keyWord = new String(keyWord.getBytes("ISO-8859-1"), 0, keyWord.length(), "utf-8");
	String result = SearchUtil.search(keyWord);
%>
<%= result %>