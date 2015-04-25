<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.lang.String" %>
<%@ page import="util.SearchUtil" %>
<% 
	String keyWord = request.getParameter("key");
	keyWord = (keyWord == null) ? "" : keyWord;
	double minPrice = Double.parseDouble(request.getParameter("minPrice") == null ? "0" : request.getParameter("minPrice"));
	double maxPrice = Double.parseDouble(request.getParameter("maxPrice") == null ? "20000" : request.getParameter("maxPrice"));
	double day = Double.parseDouble(request.getParameter("day") == null ? "-1" : request.getParameter("day"));
	String orderBy = request.getParameter("orderby");
	boolean reverse = (request.getParameter("sort") == null || request.getParameter("sort").equals("desc")) ? false : true;
 	String path = application.getRealPath("/traveldata/index");
 	SearchUtil util = SearchUtil.getInstance(new File(path));
	String result = util.search(keyWord, minPrice, maxPrice, day, orderBy, reverse);
%>
<%= result %>
