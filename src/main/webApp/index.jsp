<%@page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
</head>
<body>
    <div style="text-align: center">
        <form action="${pageContext.request.contextPath}/poetry/find?page=1&pageSize=10" method="post">
            <br>
            <br>
            <br>
            <br>
            <div><h1>唐诗检索系统  </h1>
                    <h3 style="color: green">当前版本：V.1.0.0（绿色版）</h3>
                <img src="header_logo.gif">
            </div>
            <br>
            <br>
            <div>
                <input type="text" name="text" style="font-size:22px;width: 500px;height: 30px"> <input type="submit" value="搜索" style="width: 60px;height: 30px">
                <p>
                    您要搜索的内容是：
                    诗句<input type="radio" value="content" name="type" checked="checked">
                    作者<input type="radio" value="author" name="type">
                    诗名<input type="radio" value="title" name="type">
                </p>
            </div>
        </form>
    </div>
</body>
</html>






