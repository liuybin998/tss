<%@page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8"%>

<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!doctype html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport"
		  content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
	<meta http-equiv="X-UA-Compatible" content="ie=edge">
	<title>Document</title>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.3.1.js"></script>
	<script>
        $(function(){
            $("#page").blur(function(){
                var xhr;
                //获取username
                var pages = $(this).val();
                if(window.XMLHttpRequest) {
                    xhr = new XMLHttpRequest();
                } else {
                    xhr = new ActiveXObject("Microsoft.XMLHTTP");
                }
				//准备请求
                xhr.open("GET", "${pageContext.request.contextPath}/poetry/find?text=${sessionScope.text}&type=${sessionScope.type}&pageSize=${sessionScope.pageSize}&page="+pages);
                //xhr.setRequestHeader("content-type", "application/x-www-form-urlencoded");
                //处理请求
                xhr.onreadystatechange = function(){
                    if(xhr.readyState == 4 && xhr.status ==  200){
                       location.reload(true);
                    }
                };
                xhr.send(null);
			});
		});
	</script>

</head>
<body>
<form action="${pageContext.request.contextPath}/poetry/find?page=1&pageSize=10" method="post">
	<div>
		<img src="header_logo.gif"><br>
		<input type="text" name="text" value="${sessionScope.text}" style="font-size:22px;width: 500px;height: 30px"> <input type="submit" value="搜索" style="width: 60px;height: 30px">
		<a style="padding-left: 700px" href="${pageContext.request.contextPath}/index.jsp"><input type="button" value="首页"></a>
		<p>
			您要搜索的内容是：
			诗句<input type="radio" value="content" name="type" checked="checked">
			作者<input type="radio" value="author" name="type">
			诗名<input type="radio" value="title" name="type">
		</p>
	</div>
</form>
<span style="color: #808080;font-size: 10px">
	为您找到相关结果为${sessionScope.count}条
</span>
<div>
	<c:forEach items="${sessionScope.list}" var="list">

		<div style="border: 1px #164616 solid" id="asd">
			<span><a href="">${list.title}</a></span><br>
			<span style="font-size: 15px">--${list.poet.name}</span><br>
			<span>&nbsp;&nbsp;&nbsp;&nbsp;${list.content}</span>
			<hr>
		</div>

	</c:forEach>
	<div style="text-align: center;">

		<a href="${pageContext.request.contextPath}/poetry/find?text=${sessionScope.text}&type=${sessionScope.type}&page=${sessionScope.page-1}&pageSize=${sessionScope.pageSize}"><input type="button" value="上一页"></a>
		<span>当前页: <input name="sss" type="text" id="page" style="width: 25px" value="${sessionScope.page}"></span>  共  ${sessionScope.pages}页
		<a href="${pageContext.request.contextPath}/poetry/find?text=${sessionScope.text}&type=${sessionScope.type}&page=${sessionScope.page+1}&pageSize=${sessionScope.pageSize}"><input type="button" value="下一页"></a>
	</div>
	</div>

</body>
</html>