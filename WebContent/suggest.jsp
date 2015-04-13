<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.lang.String" %>
<%@ page import="Util.TrieTree" %>
<% 
	String keyWord = request.getParameter("key");
	keyWord = (keyWord == null) ? "" : keyWord;
 	/* keyWord = new String(keyWord.getBytes("ISO-8859-1"), 0, keyWord.length(), "utf-8");*/
 	String result = TrieTree.doSearch(keyWord, "/Users/yinchuandong/Documents/workspace2/Testjsp/traveldata/keyword");
 	File file = new File("/Users/yinchuandong/androidapp/project/Testjsp/traveldata/keyword");
%>
<%= keyWord %>
<%= result %>
<%= file.getAbsolutePath() %>
