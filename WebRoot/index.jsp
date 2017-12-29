<%@ page language="java" import="java.util.*" pageEncoding="GB18030"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>吉林汇通签名1.0</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
</head>

<body>
	<h1 align="center"> 吉林汇通签名1.0 </h1>
	<br>
	<br>
	<table align="center" border="1" width="800px">
	<tr>
		<td align="center">
			<form action="servlet/UpdateAPK"
		method="post" enctype="multipart/form-data">
				<input type="file" name="uploadfile" /> <input type="submit">
			</form>
		</td>
	</tr>
	

</body>
</html>
