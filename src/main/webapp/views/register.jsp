<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi"><head><title>Tạo tài khoản | Dragon Store</title><%@ include file="includes/style.jsp" %></head>
<body><div class="auth-page"><main class="auth-shell"><div class="auth-brand"><span class="brand-mark">DS</span><span>Dragon Store</span></div><section class="surface auth-card">
  <p class="eyebrow">Bắt đầu ngay</p><h1>Tạo tài khoản</h1><p>Sau khi đăng ký, chúng tôi sẽ gửi mã OTP đến email của bạn.</p><c:if test="${not empty alert}"><p class="alert"><c:out value="${alert}"/></p></c:if>
  <form action="${pageContext.request.contextPath}/register" method="post" data-validate novalidate>
    <label class="field">Tài khoản<input name="username" autocomplete="username" minlength="3" maxlength="50" pattern="[A-Za-z0-9._-]+" title="Dùng chữ, số, dấu chấm, gạch dưới hoặc gạch ngang" required autofocus></label>
    <label class="field">Họ và tên<input name="fullname" autocomplete="name" minlength="2" maxlength="100" required></label>
    <label class="field">Email<input type="email" name="email" autocomplete="email" maxlength="254" required></label>
    <label class="field">Mật khẩu <span class="muted">(8-128 ký tự)</span><input type="password" name="password" autocomplete="new-password" minlength="8" maxlength="128" required></label>
    <label class="field">Số điện thoại <span class="muted">(không bắt buộc)</span><input name="phone" autocomplete="tel" inputmode="tel" pattern="[0-9+() .-]{8,30}" maxlength="30"></label><button type="submit">Tạo tài khoản</button>
  </form>
  <div class="auth-links"><span>Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập</a></span></div>
</section></main></div></body></html>
