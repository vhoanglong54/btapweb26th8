<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
  <title>Quản lý người dùng | Dragon Store</title>
  <%@ include file="../includes/style.jsp" %>
</head>
<body>
  <%@ include file="../includes/header.jsp" %>
  <main class="page-shell">
    <section class="surface admin-surface">
      <div class="page-heading">
        <div>
          <p class="eyebrow">Quản trị hệ thống</p>
          <h1>Người dùng</h1>
          <p class="subtle">Theo dõi tài khoản, trạng thái kích hoạt OTP và quyền truy cập.</p>
        </div>
      </div>
      <c:if test="${param.updated == '1'}"><p class="success">Đã cập nhật trạng thái tài khoản.</p></c:if>
      <c:if test="${not empty alert}"><p class="alert"><c:out value="${alert}"/></p></c:if>
      <div class="table-wrap">
        <table>
          <thead><tr><th>Người dùng</th><th>Liên hệ</th><th>Vai trò</th><th>Xác thực email</th><th>Truy cập</th><th>Tham gia</th><th>Thao tác</th></tr></thead>
          <tbody>
            <c:forEach items="${users}" var="user">
              <tr>
                <td><strong><c:out value="${user.fullName}"/></strong><br><span class="muted">@<c:out value="${user.userName}"/></span></td>
                <td><c:out value="${user.email}"/><c:if test="${not empty user.phone}"><br><span class="muted"><c:out value="${user.phone}"/></span></c:if></td>
                <td><span class="badge ${user.roleid == 1 ? 'active' : 'hidden'}">${user.roleid == 1 ? 'Admin' : 'Thành viên'}</span></td>
                <td><span class="badge ${user.active ? 'active' : 'hidden'}">${user.active ? 'Đã kích hoạt' : 'Chờ OTP'}</span></td>
                <td><span class="badge ${user.enabled ? 'active' : 'hidden'}">${user.enabled ? 'Đang mở' : 'Đã khóa'}</span></td>
                <td><c:out value="${user.createdDate}"/></td>
                <td><div class="actions">
                  <c:if test="${not user.active && user.enabled}"><form class="inline-form" action="${pageContext.request.contextPath}/admin/users" method="post"><input type="hidden" name="id" value="${user.id}"><button class="btn secondary" name="action" value="resend-activation" type="submit">Gửi lại OTP</button></form></c:if>
                  <c:if test="${sessionScope.account.id != user.id}"><form class="inline-form" action="${pageContext.request.contextPath}/admin/users" method="post" onsubmit="return confirm('${user.enabled ? 'Khóa' : 'Mở khóa'} tài khoản này?')"><input type="hidden" name="id" value="${user.id}"><button class="btn ${user.enabled ? 'danger' : 'secondary'}" name="action" value="toggle-enabled" type="submit">${user.enabled ? 'Khóa' : 'Mở khóa'}</button></form></c:if>
                </div></td>
              </tr>
            </c:forEach>
            <c:if test="${empty users}"><tr><td colspan="7">Chưa có người dùng.</td></tr></c:if>
          </tbody>
        </table>
      </div>
      <p class="form-note">Admin không thể tự khóa chính tài khoản đang đăng nhập. “Đã kích hoạt” là kết quả OTP; “Đang mở/Đã khóa” là quyền truy cập do admin quản lý.</p>
    </section>
  </main>
</body>
</html>
