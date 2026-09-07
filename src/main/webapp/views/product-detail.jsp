<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html><head><title><c:out value="${product.name}"/> | Dragon Store</title><%@ include file="includes/style.jsp" %></head>
<body><%@ include file="includes/header.jsp" %>
<main class="page-shell"><section class="surface product-detail"><c:choose><c:when test="${not empty product.image}"><img class="detail-image" src="<c:out value='${product.image}'/>" alt="<c:out value='${product.name}'/>"></c:when><c:otherwise><div class="detail-placeholder">Chưa có ảnh sản phẩm</div></c:otherwise></c:choose><div><a class="back-link" href="${pageContext.request.contextPath}/product">← Quay lại sản phẩm</a><p class="eyebrow"><c:out value="${product.category.name}"/></p><h1><c:out value="${product.name}"/></h1><p class="price"><c:out value="${product.price}"/> đ</p><p class="detail-meta">Sản phẩm thuộc danh mục <strong><c:out value="${product.category.name}"/></strong></p><p class="description"><c:out value="${product.description}"/></p></div></section></main></body></html>
