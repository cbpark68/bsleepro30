<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctxPath" value="${pageContext.request.contextPath}" />
<%
request.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>list</title>
</head>
<style>
.cls1 {
	text-decoration: none;
}

.cls2 {
	text-align: center;
	font-size: 30px;
}
</style>
<script>
	function fn_articleForm(isLogOn, articleForm, loginForm) {
		if (isLogOn != '' && isLogOn != 'false') {
			location.href = articleForm;
		} else {
			alert("로그인 후 글쓰기가 가능합니다.")
			location.href = loginForm + '?action=/board/articleForm.do';
		}
	}
</script>
<body>
	<table align="center" border="1" width="80%">
		<colgroup>
			<col width="5%" />
			<col width="10%" />
			<col width="35%" />
			<col width="10%" />
		</colgroup>
		<tr height="10" align="center" bgcolor="lightgreen">
			<th>글번호</th>
			<th>작성자</th>
			<th>제목</th>
			<th>작성일</th>
		</tr>
		<c:choose>
			<c:when test="${empty articlesList}">
				<th colspan="4">
					<p align="center">
						<span style="font-size: 12px;">등록된 글이 없습니다.</span>
					</p>
				</th>
			</c:when>
			<c:otherwise>
				<c:forEach var="article" items="${articlesList}" varStatus="articleNum">
					<tr align="center">
						<td>${articleNum.count}</td>
						<td>${article.id}</td>
						<td align="left"><span style="padding-left: 30px;"></span> <c:choose>
								<c:when test="${article.level > 1}">
									<c:forEach begin="1" end="${article.level}">
										<span style="padding-left: 20px;"></span>
									</c:forEach>
									<span style="font-size: 12px;">[답변]</span>
									<a class="cls1" href="${ctxPath}/board/viewArticle.do?articleNO=${article.articleNO}">${article.title}</a>
								</c:when>
								<c:otherwise>
									<a class="cls1" href="${ctxPath}/board/viewArticle.do?articleNO=${article.articleNO}">${article.title}</a>
								</c:otherwise>
							</c:choose></td>
						<td><fmt:formatDate value="${article.writeDate}" /></td>
					</tr>
				</c:forEach>
			</c:otherwise>
		</c:choose>
	</table>
	<%-- 	<a class="cls1" href="${ctxPath}/board/articleForm.do"><p class="cls2">글쓰기</p></a> --%>
	<a class="cls1" href="javascript:fn_articleForm('${isLogOn}','${ctxPath}/board/articleForm.do', '${ctxPath}/member/loginForm.do')">
		<p class="cls2">글쓰기</p>
	</a>
</body>
</html>