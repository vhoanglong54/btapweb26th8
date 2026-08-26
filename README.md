
## Kiến trúc hệ thống

Ứng dụng dùng kiến trúc **MVC kết hợp 3 tầng**. Luồng xử lý là:

`Browser → JSP form → Servlet Controller → Service → DAO → SQL Server`

Kết quả trả ngược về:

`SQL Server → DAO → Service → Controller → JSP View → Browser`

| Thành phần | Vị trí | Tác dụng |
|---|---|---|
| Model | `src/main/java/vn/iotstar/model` | Các lớp dữ liệu `User`, `Category`; biểu diễn bản ghi lấy từ hoặc lưu xuống SQL Server. |
| DAO | `src/main/java/vn/iotstar/dao` và `dao/impl` | Chứa SQL và thao tác JDBC: đọc/thêm/sửa/xóa `User` và `Category`. DAO không xử lý giao diện. |
| Service | `src/main/java/vn/iotstar/service` và `service/impl` | Chứa nghiệp vụ: kiểm tra đăng nhập, kiểm tra dữ liệu trùng khi đăng ký, điều phối CRUD Category. |
| Controller | `src/main/java/vn/iotstar/controller` | Các Servlet nhận request, gọi Service, quản lý session/cookie, chuyển tiếp JSP hoặc redirect. |
| View | `src/main/webapp/views` | Các trang JSP hiển thị form login/register, trang chủ và quản lý Category. JSTL dùng để lặp, điều kiện và escape dữ liệu. |
| Connection | `connection/DBConnection.java`, `config/JpaConfig.java` | Login vẫn dùng JDBC; Category/Video dùng JPA/Hibernate đến SQL Server `127.0.0.1:1433`, CSDL `ServletCRUDMVC`, tài khoản `vuhoanglong`. |
| Utility | `util/Constant.java` | Chứa hằng dùng chung: tên session, cookie, đường dẫn JSP và thư mục lưu ảnh. |
| Database | `database/ServletCRUDMVC.sql` | Tạo SQL Server login, CSDL, bảng `User`, `Category` và dữ liệu tài khoản quản trị. |

## Các task đã hoàn thành

- Tạo Maven WAR project Java 17 với các dependency Servlet, JSP, JSTL, Microsoft SQL Server JDBC, Commons FileUpload và Commons IO.
- Tạo CSDL `ServletCRUDMVC`, bảng `[User]` và `Category`.
- Tạo SQL Server login và tài khoản ứng dụng: `vuhoanglong` / `vuhoanglong`.
- Thực hiện đăng ký tài khoản, kiểm tra trùng `username`, `email`, `phone` trước khi ghi xuống CSDL.
- Thực hiện đăng nhập, kiểm tra tài khoản/mật khẩu, lưu đối tượng `User` vào `HttpSession`.
- Thực hiện cookie “Ghi nhớ đăng nhập” trong 30 phút và tái tạo session từ cookie.
- Điều hướng theo `roleid`: role `1` đến trang quản lý Category, role khác đến trang chủ.
- Thực hiện CRUD Category: danh sách, tìm kiếm, thêm, sửa, xóa.
- Hỗ trợ upload ảnh PNG/JPG/GIF/WEBP, lưu ảnh trong `D:\btap_web1\upload\category`, và hiển thị ảnh qua Servlet `/image`.
- Cấu hình `web.xml` để session dùng cookie.
- Đã build thành công WAR tại `target/ServletCRUDMVC.war` và triển khai Tomcat 11 cục bộ.
- Category CRUD dùng JPA/Hibernate; bảng `Video` có quan hệ nhiều-một với `Category`.

## Thiết lập và chạy ứng dụng

1. Công cụ dùng chung được đặt ngoài project tại `D:\.tools`:
   - JDK: `D:\.tools\jdk-17.0.20.1+1`
   - Maven: `D:\.tools\apache-maven-3.9.11`
   - Tomcat: `D:\.tools\apache-tomcat-11.0.25`
2. Mở SQL Server Management Studio bằng tài khoản Windows có quyền quản trị và chạy [database/ServletCRUDMVC.sql](database/ServletCRUDMVC.sql). Script tạo login SQL Server, CSDL, bảng và tài khoản mẫu.
3. Chạy `mvn clean package` bằng JDK 17. File WAR sinh ra là `target/ServletCRUDMVC.war`.
4. Triển khai WAR vào `D:\.tools\apache-tomcat-11.0.25\webapps`. Trên máy này Tomcat chạy cổng `8081` vì cổng `8080` đã được dịch vụ khác sử dụng.
5. Mở `http://localhost:8081/ServletCRUDMVC/`.
5. Đăng nhập bằng:

   - Tài khoản: `vuhoanglong`
   - Mật khẩu: `vuhoanglong`

## Kiểm thử chức năng

1. Đăng nhập với tài khoản mẫu, chọn **Ghi nhớ đăng nhập** và xác nhận chuyển đến Category.
2. Thêm Category với ảnh đại diện; kiểm tra ảnh hiển thị trong danh sách.
3. Sửa tên/ảnh và xóa Category.
4. Đăng ký tài khoản mới; thử nhập lại username, email hoặc phone đã có để kiểm tra cảnh báo trùng.
5. Đăng xuất, truy cập lại `/login` trong thời gian 30 phút để kiểm tra cookie khôi phục session.

## Lưu ý môi trường hiện tại

- SQL Server Express được bật TCP/IP cổng `1433`; nếu máy khác dùng instance hoặc cổng khác, chỉnh `PORT`/`INSTANCE` trong [DBConnection.java](src/main/java/vn/iotstar/connection/DBConnection.java).
- Tài liệu minh họa lưu ảnh tại `E:\upload`, nhưng máy hiện tại không có ổ `E:` nên ứng dụng dùng `D:\btap_web1\upload`.
- Dự án sử dụng `jakarta.servlet` và Jakarta Persistence 3.x, vì thế chạy với **Tomcat 11**. Không deploy WAR này vào Tomcat 9.
