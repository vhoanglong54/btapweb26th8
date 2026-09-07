<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
  <title>Cấu hình email OTP | Dragon Store</title>
  <%@ include file="../includes/style.jsp" %>
</head>
<body>
  <%@ include file="../includes/header.jsp" %>
  <main class="page-shell">
    <section class="surface admin-surface settings-surface">
      <div class="page-heading">
        <div>
          <p class="eyebrow">Quản trị hệ thống</p>
          <h1>Email OTP</h1>
          <p class="subtle">OTP luôn được gửi từ Gmail quản trị: vhoanglong54@gmail.com.</p>
        </div>
      </div>
      <c:if test="${param.saved == '1'}"><p class="success">Đã lưu cấu hình email OTP.</p></c:if>
      <c:if test="${not empty success}"><p class="success"><c:out value="${success}"/></p></c:if>
      <c:if test="${not empty alert}"><p class="alert"><c:out value="${alert}"/></p></c:if>
      <div class="info-panel">
        <strong>Thiết lập một lần, sau đó OTP tự gửi.</strong>
        <span>Đăng nhập Google Account của vhoanglong54@gmail.com, bật Xác minh 2 bước và tạo <em>App Password</em>. Không dùng mật khẩu Gmail thông thường.</span>
      </div>
      <form action="${pageContext.request.contextPath}/admin/mail-settings" method="post">
        <div class="form-grid">
          <div class="mail-identity full"><span>Email gửi</span><strong>vhoanglong54@gmail.com</strong><small>Gmail SMTP · smtp.gmail.com · cổng 587 · STARTTLS</small></div>
          <label class="field full">Gmail App Password<c:choose><c:when test="${passwordSaved}"><span class="muted"> (đã lưu; để trống để giữ nguyên)</span></c:when><c:otherwise><span class="muted"> (mã 16 ký tự)</span></c:otherwise></c:choose><input type="password" name="password" autocomplete="new-password" placeholder="${passwordSaved ? 'Đã lưu, nhập lại chỉ khi muốn thay đổi' : 'Dán App Password tại đây'}"></label>
          <label class="field full">Email nhận thư thử <span class="muted">(không bắt buộc khi chỉ lưu)</span><input type="email" name="testEmail" placeholder="Nhập email để kiểm tra gửi thử"></label>
        </div>
        <div class="form-actions">
          <button name="action" value="save" type="submit">Lưu cấu hình</button>
          <button class="btn secondary" name="action" value="test" type="submit">Lưu và gửi thử</button>
        </div>
      </form>
      <p class="form-note">Chỉ admin truy cập trang này. App Password không hiển thị lại sau khi lưu. Với môi trường triển khai thật, nên dùng secret/biến môi trường thay cho cơ sở dữ liệu.</p>
    </section>
  </main>
</body>
</html>
