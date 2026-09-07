<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header class="site-header">
  <div class="nav-container">
    <a class="brand" href="${pageContext.request.contextPath}/home"><span class="brand-mark">DS</span><span>Dragon Store</span></a>
    <nav class="site-nav" aria-label="Điều hướng chính">
      <c:choose>
        <c:when test="${sessionScope.account == null}"><a href="${pageContext.request.contextPath}/product">Sản phẩm</a><a href="${pageContext.request.contextPath}/login">Đăng nhập</a><a class="nav-primary" href="${pageContext.request.contextPath}/register">Tạo tài khoản</a></c:when>
        <c:otherwise><a href="${pageContext.request.contextPath}/product">Sản phẩm</a><a href="${pageContext.request.contextPath}/profile">Hồ sơ</a><c:if test="${sessionScope.account.roleid == 1}"><a href="${pageContext.request.contextPath}/admin/products">Quản lý sản phẩm</a><a href="${pageContext.request.contextPath}/admin/categories">Danh mục</a><a href="${pageContext.request.contextPath}/admin/users">Người dùng</a><a href="${pageContext.request.contextPath}/admin/mail-settings">Email OTP</a></c:if><span class="account-name"><c:out value="${sessionScope.account.fullName}"/></span><form class="logout-form" action="${pageContext.request.contextPath}/logout" method="post"><button class="nav-button" type="submit">Đăng xuất</button></form></c:otherwise>
      </c:choose>
    </nav>
  </div>
</header>
