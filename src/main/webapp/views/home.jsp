<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head><title>Dragon Store | Sản phẩm mới</title><%@ include file="includes/style.jsp" %></head>
<body><%@ include file="includes/header.jsp" %>
<main class="page-shell">
  <section class="surface hero">
    <div class="hero-copy"><p class="eyebrow">Dragon Store</p><h1>Khám phá sản phẩm vừa cập nhật.</h1><p class="subtle">Chọn sản phẩm phù hợp từ những mặt hàng mới nhất trong cửa hàng.</p></div>
    <div class="hero-actions"><a class="btn" href="${pageContext.request.contextPath}/product">Xem tất cả sản phẩm</a></div>
  </section>
  <section class="surface product-section">
    <div class="toolbar"><div><p class="eyebrow">Mới nhất</p><h1>Sản phẩm mới nhất</h1></div><a class="btn secondary" href="${pageContext.request.contextPath}/product">Xem tất cả</a></div>
    <c:choose><c:when test="${not empty products}"><div class="product-grid"><c:forEach items="${products}" var="product"><a class="product-card" href="${pageContext.request.contextPath}/product/detail?id=${product.productId}"><c:choose><c:when test="${not empty product.image}"><img class="product-image" src="<c:out value='${product.image}'/>" alt="<c:out value='${product.name}'/>"></c:when><c:otherwise><div class="no-image">Chưa có ảnh sản phẩm</div></c:otherwise></c:choose><div class="product-copy"><span class="product-category"><c:out value="${product.category.name}"/></span><h2><c:out value="${product.name}"/></h2><p class="price"><c:out value="${product.price}"/> đ</p></div></a></c:forEach></div></c:when><c:otherwise><div class="empty-state">Chưa có sản phẩm hiển thị. Hãy quay lại sau nhé.</div></c:otherwise></c:choose>
  </section>
</main></body></html>
