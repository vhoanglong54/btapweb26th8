<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head><title>Dragon Store | Sản phẩm</title><%@ include file="includes/style.jsp" %></head>
<body><%@ include file="includes/header.jsp" %>
<main class="page-shell"><section class="surface product-section"><div class="page-heading"><div><p class="eyebrow">Danh mục cửa hàng</p><h1>Tất cả sản phẩm</h1><p class="subtle">Duyệt sản phẩm đang được hiển thị trong cửa hàng.</p></div></div>
<c:choose><c:when test="${not empty products}"><div class="product-grid"><c:forEach items="${products}" var="product"><a class="product-card" href="${pageContext.request.contextPath}/product/detail?id=${product.productId}"><c:choose><c:when test="${not empty product.image}"><img class="product-image" src="<c:out value='${product.image}'/>" alt="<c:out value='${product.name}'/>"></c:when><c:otherwise><div class="no-image">Chưa có ảnh sản phẩm</div></c:otherwise></c:choose><div class="product-copy"><span class="product-category"><c:out value="${product.category.name}"/></span><h2><c:out value="${product.name}"/></h2><p class="price"><c:out value="${product.price}"/> đ</p></div></a></c:forEach></div></c:when><c:otherwise><div class="empty-state">Chưa có sản phẩm hiển thị.</div></c:otherwise></c:choose>
<nav class="pagination" aria-label="Phân trang"><c:if test="${page > 1}"><a class="btn secondary" href="${pageContext.request.contextPath}/product?page=${page - 1}">← Trang trước</a></c:if><span>Trang ${page} trên ${totalPages}</span><c:if test="${page < totalPages}"><a class="btn secondary" href="${pageContext.request.contextPath}/product?page=${page + 1}">Trang sau →</a></c:if></nav>
</section></main></body></html>
