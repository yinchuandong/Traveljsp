<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.lang.String" %>
<%@ page import="util.SuggestUtil" %>
<% 
	String keyWord = request.getParameter("key");
	keyWord = (keyWord == null) ? "" : keyWord;
 	/* keyWord = new String(keyWord.getBytes("ISO-8859-1"), 0, keyWord.length(), "utf-8");*/
/*  	String result = TrieTree.doSearch(keyWord, "/Users/yinchuandong/androidapp/workspace2/Traveljsp/traveldata/keyword");
 */ 	
 	String path = application.getRealPath("/traveldata/keyword.txt");
	SuggestUtil model = SuggestUtil.getInstance(new File(path));
	String result = model.suggest(keyWord);
 	
%>
<%= result %>
