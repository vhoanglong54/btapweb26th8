<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="topbar">
  <a class="brand" href="${pageContext.request.contextPath}/home">Servlet CRUD MVC</a>
  <nav>
    <c:choose>
      <c:when test="${sessionScope.account == null}"><a href="${pageContext.request.contextPath}/login">Đăng nhập</a><a href="${pageContext.request.contextPath}/register">Đăng ký</a></c:when>
      <c:otherwise><span>Xin chào, <c:out value="${sessionScope.account.fullName}"/></span><a href="${pageContext.request.contextPath}/logout">Đăng xuất</a></c:otherwise>
    </c:choose>
  </nav>
</header>
