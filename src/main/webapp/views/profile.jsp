<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <title>Hồ sơ cá nhân | Dragon Store</title>
  <%@ include file="includes/style.jsp" %>
</head>
<body>
  <main class="page-shell">
    <section class="surface profile-surface">
      <div class="page-heading">
        <div>
          <p class="eyebrow">Tài khoản</p>
          <h1>Hồ sơ cá nhân</h1>
          <p class="subtle">Cập nhật thông tin hiển thị và ảnh đại diện của bạn.</p>
        </div>
      </div>
      <c:if test="${param.updated == '1'}"><p class="success">Đã cập nhật hồ sơ.</p></c:if>
      <c:if test="${not empty alert}"><p class="alert"><c:out value="${alert}"/></p></c:if>
      <form action="${pageContext.request.contextPath}/profile" method="post" enctype="multipart/form-data">
        <div class="profile-grid">
          <aside class="avatar-panel">
            <c:choose>
              <c:when test="${not empty profile.avatar}">
                <c:url value="/image" var="avatarUrl"><c:param name="fname" value="${profile.avatar}"/></c:url>
                <img class="avatar-image" src="${avatarUrl}" alt="Ảnh đại diện của <c:out value='${profile.fullName}'/>">
              </c:when>
              <c:otherwise><div class="avatar-placeholder" aria-hidden="true">${profile.fullName.substring(0, 1)}</div></c:otherwise>
            </c:choose>
            <p class="muted">PNG, JPG, GIF hoặc WEBP<br>Tối đa 5 MB</p>
          </aside>
          <div>
            <label class="field">Họ và tên<input name="fullName" value="<c:out value='${profile.fullName}'/>" autocomplete="name" required autofocus></label>
            <label class="field">Số điện thoại <span class="muted">(không bắt buộc)</span><input name="phone" value="<c:out value='${profile.phone}'/>" autocomplete="tel" inputmode="tel" maxlength="30"></label>
            <label class="field">Ảnh đại diện <span class="muted">(để trống nếu giữ ảnh hiện tại)</span><input type="file" name="image" accept="image/png,image/jpeg,image/gif,image/webp"></label>
            <div class="form-actions"><button type="submit">Lưu hồ sơ</button></div>
          </div>
        </div>
      </form>
    </section>
  </main>
</body>
</html>
