# Dragon Store

Dragon Store là ứng dụng quản lý Category và Product viết bằng Java 17, Servlet/JSP, SQL Server và Maven. Các chức năng tài khoản gồm đăng ký, kích hoạt OTP qua email, đăng nhập, ghi nhớ đăng nhập, quên mật khẩu và tự quản lý hồ sơ.

- [Thiết kế hệ thống](ARCHITECTURE.md): kiến trúc, phân lớp, quan hệ dữ liệu, endpoint và các quyết định kỹ thuật.
- [Tài liệu vận hành chức năng](FUNCTIONAL_DOCUMENTATION.md): luồng OTP, Products và checklist nghiệm thu.

## Trạng thái yêu cầu bài tập

| Yêu cầu | Trạng thái | Điểm truy cập |
|---|---|---|
| Kích hoạt account bằng OTP email khi đăng ký | Đã triển khai; OTP gửi từ Gmail admin sau khi lưu App Password trên web. | `/register` → `/activate` |
| Đăng nhập, ghi nhớ và đăng xuất | Đã triển khai; logout xóa cả session lẫn cookie ghi nhớ. | `/login`, `/logout` |
| Quản lý người dùng | Đã triển khai; admin xem trạng thái OTP, gửi lại OTP và khóa/mở tài khoản. | `/admin/users` |
| Quên mật khẩu qua OTP email | Đã triển khai; dùng cùng cấu hình SMTP trên giao diện admin. | `/forgot-password` → `/reset-password` |
| Hồ sơ người dùng qua JPA + SiteMesh | Đã triển khai; user tự sửa họ tên, số điện thoại, ảnh đại diện multipart. | `/profile` |
| Product quan hệ 1-n Category và CRUD admin | Đã triển khai. | `/admin/products` |
| Hiển thị 10 Product mới nhất | Đã triển khai. | `/home` |
| Hiển thị Product phân trang 6 bản ghi/trang | Đã triển khai. | `/product?page=N` |
| Chi tiết Product từ home và danh sách | Đã triển khai. | `/product/detail?id=N` |

Chi tiết controller, DAO và cách nghiệm thu cho từng dòng nằm trong [FUNCTIONAL_DOCUMENTATION.md](FUNCTIONAL_DOCUMENTATION.md#6-ma-trận-yêu-cầu-bài-tập).

## Điều kiện chạy

- JDK 17 (`java -version` trả về 17).
- Maven 3.8+ (`mvn -version`).
- SQL Server đang chạy và cho phép kết nối TCP. Cấu hình mặc định của project là `127.0.0.1:1433`.
- Tomcat 11 (hoặc servlet container hỗ trợ Jakarta Servlet 6) để chạy file WAR.

## Chạy lần đầu

### Cách nhanh nhất trên máy hiện tại

Chỉ cần nhấp đúp [run.cmd](run.cmd). File này tự build WAR, copy vào Tomcat, khởi động Tomcat nếu cần và mở trang login.

Trong VS Code, nhấn `Ctrl+Shift+B`, chọn **Run Dragon Store**. Task có cùng hành vi với `run.cmd`.

Mặc định script dùng Java 17, Maven và Tomcat tại `D:\.tools_web`. Nếu cài nơi khác, đặt ba biến môi trường `JAVA_HOME`, `MAVEN_HOME`, `TOMCAT_HOME` trước khi chạy. Không cần đổi source code.

### Thiết lập lần đầu

1. Mở SQL Server Management Studio bằng tài khoản có quyền tạo database/login, chạy toàn bộ [database/ServletCRUDMVC.sql](database/ServletCRUDMVC.sql).

   Script tạo database `ServletCRUDMVC`, SQL login `vuhoanglong`, các bảng `Category`, `User`, `UserOtp`, `SmtpSettings`, `Product` và tài khoản admin mẫu. Script có thể chạy lại: các bảng, cột và index chỉ được tạo khi chưa tồn tại. Cột `User.active` là trạng thái xác thực OTP; `User.enabled` là quyền truy cập do admin khóa/mở.

2. Kiểm tra hoặc đổi cấu hình database ở **cả hai** nơi sau để chúng cùng trỏ đến một database:

   - [DBConnection.java](src/main/java/vn/iotstar/connection/DBConnection.java): JDBC cho `User` và `UserOtp`.
   - [persistence.xml](src/main/resources/META-INF/persistence.xml): JPA/Hibernate cho `Category` và `Product`.

   Nếu SQL Server dùng instance hoặc cổng khác, đổi `SERVER`/`PORT` trong `DBConnection.java` và URL `jakarta.persistence.jdbc.url` trong `persistence.xml` tương ứng.

3. Cấu hình email OTP hoàn toàn trên giao diện:

   - Chạy project, đăng nhập `vuhoanglong / vuhoanglong`.
   - Trên thanh menu, chọn **Email OTP**.
   - Hệ thống cố định người gửi là `vhoanglong54@gmail.com` qua Gmail SMTP (`smtp.gmail.com`, cổng `587`, STARTTLS). Bật Xác minh 2 bước cho chính Gmail này và tạo một **App Password**, rồi dán App Password vào giao diện.
   - Nhập email nhận thư thử, bấm **Lưu và gửi thử**. Khi nhận được thư, đăng ký/kích hoạt/quên mật khẩu sẽ gửi OTP thật.

   App Password là dữ liệu nhạy cảm: màn hình chỉ cho ghi, không bao giờ hiển thị lại mật khẩu đã lưu. Khi triển khai production, thay cấu hình local này bằng secret manager hoặc biến môi trường của server.

4. Tại thư mục project, build WAR:

   ```powershell
   mvn clean package
   ```

   Khi thành công sẽ có `target/ServletCRUDMVC.war`.

5. Chép WAR vào thư mục `webapps` của Tomcat và khởi động Tomcat. Với cấu hình mặc định của Tomcat, mở:

   ```text
   http://localhost:8081/ServletCRUDMVC/login
   ```

   Nếu đổi HTTP port hoặc đổi tên WAR/context path, thay URL theo cấu hình đó. `index.jsp` cũng chuyển hướng đến `/login`.

6. Đăng nhập quản trị để tạo Category trước, sau đó tạo Product:

   ```text
   username: vuhoanglong
   password: vuhoanglong
   ```

   Tài khoản mẫu được tạo ở trạng thái đã kích hoạt. Đổi mật khẩu này khi dùng ngoài môi trường bài tập.

## Kiểm tra sau khi chạy

1. Mở `/admin/categories`, thêm một Category.
2. Mở `/admin/products`, thêm tối thiểu 11 Product, rồi kiểm tra thêm/sửa/xóa/ẩn.
3. Kiểm tra `/home` chỉ hiện tối đa 10 Product mới nhất, `/product` hiện 6 Product mỗi trang và trang chi tiết hoạt động.
4. Với admin, mở **Email OTP**, lưu và gửi thử App Password của `vhoanglong54@gmail.com`; sau đó đăng ký một tài khoản mới, hoàn tất OTP và kiểm tra quên mật khẩu. Trang **Người dùng** cho phép kiểm tra trạng thái và gửi lại OTP khi cần.
5. Sau khi đăng nhập, mở **Hồ sơ** hoặc `/profile`, cập nhật họ tên, số điện thoại và thử chọn một ảnh hợp lệ; lưu thành công phải quay lại trang hồ sơ, tên trên session được cập nhật và ảnh đại diện hiển thị.

Lệnh kiểm tra trước khi deploy:

```powershell
mvn test
mvn clean package
```

## Upload ảnh

Category và Profile cho phép upload PNG/JPG/GIF/WEBP. Ảnh đại diện Profile được nhận bằng `multipart/form-data`, giới hạn 5 MB, đổi tên UUID và lưu vào `profile/`; chỉ tên file do server sinh mới được ghi database. Thiết lập `APP_UPLOAD_DIR` để chọn thư mục lưu ảnh; nếu bỏ trống, ứng dụng lưu tại `ServletCRUDMVC/upload` bên trong home directory của user chạy Tomcat. Thư mục phải có quyền ghi.
