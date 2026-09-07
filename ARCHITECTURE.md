# Thiết kế hệ thống Dragon Store

## Mục tiêu và ranh giới

Đây là ứng dụng web server-rendered, tập trung vào bài tập Servlet/JSP: quản lý danh mục và sản phẩm, kèm luồng tài khoản bằng email OTP. Client không gọi REST API; HTML được render ở server bằng JSP và dữ liệu được lưu trong SQL Server.

## Kiến trúc tổng thể

```text
Browser
  → Filter (chỉ /admin/*)
  → Servlet Controller
  → Service (nghiệp vụ, kiểm tra dữ liệu)
  → DAO (JDBC hoặc JPA/Hibernate)
  → SQL Server
  → Controller đặt dữ liệu vào request/session
  → JSP render HTML
  → Browser
```

Controller không chứa SQL; JSP chỉ hiển thị bằng JSTL/c:out; Service quyết định quy tắc nghiệp vụ; DAO là lớp duy nhất đọc/ghi database. Cách tách này giúp thay đổi giao diện, nghiệp vụ hoặc truy cập dữ liệu ít ảnh hưởng lẫn nhau.

## Thành phần và trách nhiệm

| Thành phần | Vị trí | Trách nhiệm |
|---|---|---|
| Controller | `controller/` | Nhận HTTP request, đọc tham số, gọi Service, redirect/forward JSP. |
| Filter | `filter/AccountStatusFilter`, `AdminAuthorizationFilter` | Làm mới trạng thái account mỗi request; bảo vệ `/admin/*`: chưa login chuyển `/login`, không phải `roleid=1` trả 403. |
| Service | `service/`, `service/impl/` | Kiểm tra và điều phối đăng ký/login/OTP; kiểm tra Product/Category trước khi ghi. |
| DAO JDBC | `dao/impl/UserDaoImpl`, `OtpDaoImpl`, `SmtpSettingsDaoImpl` | Dùng `PreparedStatement` cho User, OTP và cấu hình SMTP. |
| DAO JPA | `CategoryDao`, `ProductDaoImpl` | Dùng EntityManager, transaction và JPQL cho Category/Product. |
| Entity/Model | `entity/`, `model/User` | `Category`, `Product` là JPA entity; `User` là model JDBC. |
| View | `webapp/views/` | JSP theo trang công khai, xác thực và quản trị. |
| Cấu hình | `connection/`, `config/`, `META-INF/` | JDBC, EntityManagerFactory và persistence unit. |
| Vận hành local | `scripts/run.ps1`, `run.cmd`, `.vscode/tasks.json` | Build, deploy WAR, start Tomcat và mở trang login chỉ bằng một thao tác. |

## Thiết kế trải nghiệm giao diện

`views/includes/style.jsp` là design system dùng chung, giúp giao diện không bị lệch giữa các JSP:

- Header sticky và điều hướng phân theo khách, user và admin; quyền vẫn được bảo vệ ở server bởi filter, không chỉ ẩn link trên UI.
- Trang public dùng hero, card Product và empty state để người dùng hiểu ngay mục đích trang khi database chưa có dữ liệu.
- Form xác thực dùng cùng layout, label, focus state, autocomplete và nội dung giải thích OTP; người dùng không phải đoán bước tiếp theo.
- Admin dùng toolbar, form grid, badge trạng thái và table wrapper ngang để thao tác tốt trên màn hình nhỏ.
- CSS chỉ dùng system font, không phụ thuộc CDN hay asset bên ngoài; Product image vẫn là URL do admin cung cấp và Category image dùng upload hiện có.

Các thay đổi UX chỉ ảnh hưởng markup/CSS và khả năng đọc; name của input, method form, URL, controller và quy tắc nghiệp vụ giữ nguyên.

## Vì sao dùng cả JDBC và JPA

`User`/`UserOtp` dùng JDBC vì đây là luồng tài khoản với các lệnh SQL ngắn, trực tiếp và cập nhật OTP có điều kiện. `Category`/`Product` dùng JPA vì có quan hệ đối tượng và các truy vấn phân trang/danh sách. Hai cách cùng dùng SQL Server, nên cấu hình ở `DBConnection.java` và `persistence.xml` bắt buộc phải đồng nhất.

`JpaConfig` tạo một `EntityManagerFactory` dùng chung cho toàn ứng dụng. Mỗi thao tác DAO mở `EntityManager` riêng và đóng nó ngay sau khi xong; transaction được commit hoặc rollback trong DAO. Vì vậy request đồng thời không chia sẻ `EntityManager`.

## Mô hình dữ liệu

```text
Category (1) ─────< Product (n)

User (1) ─────< UserOtp (n, liên kết logic theo email + purpose)

SmtpSettings (1 cấu hình cục bộ do admin quản lý)
```

| Bảng | Vai trò |
|---|---|
| `Category` | Danh mục; gồm tên, ảnh và trạng thái. |
| `Product` | Tên, mô tả, giá, URL ảnh, trạng thái, thời điểm tạo và `category_id`. |
| `User` | Tài khoản, role, cờ `active` (đã xác thực OTP) và `enabled` (được admin cho phép truy cập). |
| `UserOtp` | OTP hash, mục đích, hạn dùng, số lần sai và trạng thái đã dùng. |
| `SmtpSettings` | Một cấu hình SMTP cục bộ để gửi OTP, chỉ admin ghi qua giao diện. |

`Product.category_id` là foreign key bắt buộc đến `Category.cate_id`. Mapping `Category.products` dùng cascade/orphan removal: khi xóa Category qua JPA, Product thuộc Category đó cũng được xóa để không vi phạm khóa ngoại.

`UserOtp` không có foreign key vật lý đến `User`: điều này cho phép luồng OTP tra cứu nhanh theo email và purpose, nhưng Service chỉ phát OTP reset khi email đã tồn tại. OTP kích hoạt được phát ngay sau khi User mới được tạo.

## Các luồng request chính

### Tài khoản

```text
/register → tạo User(active=0) → tạo + gửi OTP(ACTIVATE)
          → /activate xác thực OTP → User(active=1) → /login

/forgot-password → tạo + gửi OTP(RESET_PASSWORD)
                 → /reset-password xác thực OTP → cập nhật mật khẩu

Admin /admin/mail-settings → lưu SMTP → gửi email thử → OTP dùng cấu hình đã lưu
```

`LoginController` tạo session `account` chỉ cho user active. Nếu chọn ghi nhớ, browser lưu cookie username 30 phút và `/login` tạo lại session sau khi kiểm tra user vẫn active. `WaitingController` chuyển admin (`roleid=1`) tới Category admin, user thường tới `/home`.

`/logout` hỗ trợ POST từ nút điều hướng (và GET để tương thích link cũ). Luồng logout hủy session **và** gửi lại cookie ghi nhớ với `Max-Age=0`; vì vậy user đã chọn “Ghi nhớ đăng nhập” cũng không bị tự đăng nhập lại sau khi logout.

### Sản phẩm

```text
Admin /admin/products → ProductService → ProductDao → Product + Category

/home                 → 10 Product status=1 mới nhất
/product?page=N       → Product status=1, 6 bản ghi/trang
/product/detail?id=N  → một Product status=1
```

Quản trị viên thấy cả Product hiện/ẩn để quản lý; các trang công khai chỉ thấy `status=1`. Xóa Product dùng `POST`; URL công khai không cung cấp thao tác ghi dữ liệu.

## Bản đồ URL

| URL | Vai trò |
|---|---|
| `/login`, `/logout`, `/register` | Đăng nhập, đăng xuất, đăng ký. |
| `/activate`, `/forgot-password`, `/reset-password` | Kích hoạt và phục hồi tài khoản bằng OTP. |
| `/home`, `/product`, `/product/detail?id=N` | Trang sản phẩm công khai. |
| `/admin/categories`, `/admin/category/*` | CRUD Category, chỉ admin. |
| `/admin/products`, `/admin/product/*` | CRUD Product, chỉ admin. |
| `/admin/users` | Danh sách user, gửi lại OTP, khóa/mở tài khoản; chỉ admin. |
| `/admin/mail-settings` | Cấu hình và gửi thử email OTP, chỉ admin. |
| `/image?fname=...` | Đọc ảnh Category đã upload; chặn đường dẫn tuyệt đối và `..`. |

## Bảo mật hiện có và giới hạn

- Mật khẩu mới dùng PBKDF2-HMAC-SHA256 với salt riêng; mật khẩu mẫu dạng cũ sẽ được nâng cấp sau lần login thành công đầu tiên.
- OTP là số ngẫu nhiên 6 chữ số, database chỉ giữ SHA-256, hạn 10 phút, dùng một lần; gửi lại làm vô hiệu mã cũ và tối đa 5 lần nhập sai.
- App Password của Gmail `vhoanglong54@gmail.com` được admin lưu qua giao diện local (mật khẩu write-only) hoặc dùng biến môi trường/system property làm dự phòng. Source và Git không chứa credential.
- SQL dùng `PreparedStatement` cho mọi giá trị động của User/OTP.

Đây là bài tập; trước khi đưa lên production nên thêm CSRF token cho các form ghi dữ liệu, cookie `Secure`/`HttpOnly`/`SameSite`, rate limit cho login/OTP, log có kiểm soát, đưa SMTP password sang secret manager/biến môi trường, cấu hình database qua biến môi trường và tài khoản DB quyền tối thiểu.

## Vòng đời build/deploy

Maven biên dịch Java 17, đóng gói JSP/resources/dependencies thành `target/ServletCRUDMVC.war`. Tomcat cung cấp Servlet/JSP API ở runtime; dependencies có scope `provided` không nằm trong WAR. Khi WAR được deploy, annotation `@WebServlet` và `@WebFilter` được container quét để đăng ký route/filter.
