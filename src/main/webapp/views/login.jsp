<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi"><head><title>Đăng nhập | Dragon Store</title><%@ include file="includes/style.jsp" %></head>
<body><div class="auth-page"><main class="auth-shell"><div class="auth-brand"><span class="brand-mark">DS</span><span>Dragon Store</span></div><section class="surface auth-card">
  <p class="eyebrow">Chào mừng trở lại</p><h1>Đăng nhập</h1><p>Tiếp tục quản lý và khám phá sản phẩm của bạn.</p>
  <c:if test="${param.logout == '1'}"><p class="success">Bạn đã đăng xuất thành công.</p></c:if><c:if test="${param.activated == '1'}"><p class="success">Kích hoạt thành công. Bạn có thể đăng nhập.</p></c:if><c:if test="${param.reset == '1'}"><p class="success">Đặt lại mật khẩu thành công. Hãy đăng nhập.</p></c:if><c:if test="${param.disabled == '1'}"><p class="alert">Tài khoản đã bị quản trị viên tạm khóa.</p></c:if><c:if test="${not empty alert}"><p class="alert"><c:out value="${alert}"/></p></c:if>
  <form action="${pageContext.request.contextPath}/login" method="post" data-validate novalidate>
    <label class="field">Tài khoản<input name="username" autocomplete="username" minlength="3" maxlength="50" pattern="[A-Za-z0-9._-]+" required autofocus></label>
    <label class="field">Mật khẩu<input type="password" name="password" autocomplete="current-password" maxlength="128" required></label>
    <label class="check-field"><input type="checkbox" name="remember"> Ghi nhớ đăng nhập trong 30 phút</label><button type="submit">Đăng nhập</button>
  </form>
  <div class="auth-links"><a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a><span>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register">Đăng ký</a></span></div>
</section></main></div></body></html>
