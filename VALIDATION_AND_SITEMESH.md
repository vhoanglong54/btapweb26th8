# SiteMesh Bootstrap và validation Form

Tài liệu này ghi nhận cấu hình giao diện dùng chung và các quy tắc kiểm tra đầu vào. Nó bổ sung cho [ARCHITECTURE.md](ARCHITECTURE.md) và [FUNCTIONAL_DOCUMENTATION.md](FUNCTIONAL_DOCUMENTATION.md).

## SiteMesh Decorator 3

- Dependency `org.sitemesh:sitemesh` dùng SiteMesh 3; `ConfigurableSiteMeshFilter` chỉ intercept URL `/profile` trong `WEB-INF/web.xml`.
- `WEB-INF/sitemesh3.xml` map duy nhất `/profile` tới `WEB-INF/decorators/profile.html` và dùng `dispatch-mode=include`, tương thích response buffer của Tomcat 11.
- Decorator là Bootstrap 5.3.3, đóng gói qua WebJar trong WAR tại `/webjars/bootstrap/5.3.3/...`; ứng dụng không phụ thuộc CDN.
- JSP `views/profile.jsp` chỉ giữ nội dung nghiệp vụ. Decorator cung cấp khung `html`, `head`, thanh điều hướng và vùng body bằng `sitemesh:write`.

Không mở rộng mapping decorator sang các route khác vì các JSP hiện hữu đã tự render đầy đủ tài liệu HTML; điều này tránh lồng `html/body` và tránh thay đổi UI ngoài phạm vi yêu cầu.

## Nguyên tắc validation

Mỗi form nhập liệu kiểm tra theo hai tầng:

1. **Trình duyệt:** các thuộc tính HTML5 (`required`, `minlength`, `maxlength`, `pattern`, `type`) và `assets/js/form-validation.js`. Script thêm trạng thái trực quan sau lần submit, kiểm tra xác nhận mật khẩu và loại/dung lượng ảnh trước khi gửi.
2. **Server:** `RequestValidator` chuẩn hóa và kiểm tra lại mọi dữ liệu tin cậy thấp trước khi gọi service/DAO. Không được coi validation ở trình duyệt là biện pháp bảo mật.

| Form/chức năng | Quy tắc chính trên server |
|---|---|
| Đăng ký, đăng nhập | Username 3--50 ký tự hợp lệ; email đúng dạng; password 8--128; họ tên 2--100; phone nếu có phải đúng mẫu. |
| Kích hoạt/đặt lại mật khẩu | OTP đúng 6 số; password mới và xác nhận phải trùng nhau. |
| Quên mật khẩu | Email bắt buộc và đúng định dạng. |
| Hồ sơ | Họ tên 2--100; phone theo mẫu; avatar PNG/JPG/GIF/WEBP, tối đa 5 MB. |
| Danh mục | Tên bắt buộc, tối đa 100; ảnh cùng whitelist MIME/phần mở rộng và giới hạn 5 MB. |
| Tìm danh mục | Từ khóa không bắt buộc, tối đa 100 ký tự; kiểm tra ở trình duyệt và server trước khi truy vấn. |
| Sản phẩm | Tên tối đa 255; Category ID dương; giá không âm với tối đa 2 số lẻ; URL ảnh chỉ `http`/`https`; mô tả tối đa 2000. |
| SMTP | App Password chuẩn hóa bỏ khoảng trắng, phải 16 ký tự chữ/số; email gửi thử đúng dạng. |

Các form thao tác chỉ dùng ID ẩn (xóa, khóa/mở khóa, gửi lại OTP, logout) không có dữ liệu do người dùng nhập. Controller vẫn kiểm tra ID dương/action hợp lệ và filter hiện có vẫn bảo vệ quyền admin.

## Kiểm thử

```powershell
mvn test
mvn clean package
```

Sau build, kiểm tra WAR có `WEB-INF/lib/bootstrap-5.3.3.jar`, `assets/js/form-validation.js` và `WEB-INF/decorators/profile.html`. Khi chạy Tomcat, kiểm tra `/profile` có header từ decorator và Bootstrap được tải từ chính context của ứng dụng.
