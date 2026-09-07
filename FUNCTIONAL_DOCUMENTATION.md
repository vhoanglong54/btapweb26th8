# Tài liệu vận hành: tài khoản, OTP và sản phẩm

Tài liệu này mô tả nghiệp vụ và nghiệm thu. Xem [ARCHITECTURE.md](ARCHITECTURE.md) để hiểu phân lớp, dữ liệu và các endpoint; xem [README.md](README.md) để chạy project từ đầu.

## 1. Chuẩn bị môi trường

1. Chạy lại script [database/ServletCRUDMVC.sql](database/ServletCRUDMVC.sql). Script có thể chạy lặp lại an toàn: bổ sung cột `User.active`/`User.enabled`, tạo `UserOtp`, `SmtpSettings`, `Product`, index và khóa ngoại `Product.category_id -> Category.cate_id`.
2. Cấu hình SMTP trên web: login admin `vuhoanglong/vuhoanglong`, mở `/admin/mail-settings` (menu **Email OTP**) và nhập **App Password của `vhoanglong54@gmail.com`**. Người gửi, host `smtp.gmail.com`, port `587` và STARTTLS đã được cố định theo Gmail này. Nhập email nhận thư thử và chọn **Lưu và gửi thử** để xác thực trước khi người dùng đăng ký.

   Mật khẩu là trường write-only: sau khi lưu, browser không đọc lại giá trị. Cấu hình này phù hợp môi trường bài tập/local; production nên dùng secret manager hoặc biến môi trường. Nếu chưa lưu trên UI, service tự dùng `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`, `MAIL_STARTTLS` từ system property/biến môi trường làm dự phòng.
3. Build WAR: `mvn clean package`. File xuất ra là `target/ServletCRUDMVC.war`.

## 2. Luồng tài khoản và OTP

### Đăng ký và kích hoạt

`POST /register` kiểm tra trường bắt buộc, mật khẩu tối thiểu 8 ký tự và tính duy nhất của username/email/phone. User được lưu với `active = 0`, mật khẩu được hash PBKDF2-SHA256 (120.000 vòng). Sau đó hệ thống sinh OTP ngẫu nhiên 6 số và gửi email.

Mã không được lưu dạng rõ: bảng `UserOtp` chỉ chứa SHA-256 của OTP. OTP có hạn 10 phút, một lần dùng; gửi lại OTP sẽ vô hiệu mã cũ. `POST /activate` xác thực mã, đổi `User.active` thành `1`, rồi mới cho phép đăng nhập. Tối đa 5 lần nhập sai được tính trên mã gần nhất.

Nếu SMTP gửi lỗi sau khi user đã tạo, trang kích hoạt vẫn mở để người dùng cấu hình xong có thể bấm **Gửi lại OTP**; không tạo bản ghi user trùng.

### Đăng nhập

`POST /login` chỉ tạo session `account` khi username/password hợp lệ, user đã kích hoạt (`active = 1`) và đang được phép truy cập (`enabled = 1`). Cookie `username` (nếu chọn ghi nhớ) cũng chỉ khôi phục session khi hai điều kiện này còn đúng. `AccountStatusFilter` tải lại user ở mỗi request có session, vì vậy khóa tài khoản từ admin có hiệu lực ngay ở request tiếp theo. Tài khoản mẫu cũ đang lưu password rõ vẫn đăng nhập được một lần; khi thành công nó được tự đổi sang PBKDF2 để tương thích dữ liệu cũ mà không làm mất tài khoản quản trị.

`/admin/*` có filter: chưa đăng nhập được chuyển `/login`; tài khoản không phải `roleid = 1` nhận HTTP 403.

`/admin/users` cho admin xem thông tin người dùng mà không hiển thị password. Hai trạng thái được tách rõ: `active` là đã hoàn tất OTP email; `enabled` là được phép truy cập. Admin có thể khóa/mở tài khoản khác và gửi lại OTP cho tài khoản còn chờ kích hoạt. Admin không thể tự khóa tài khoản phiên hiện tại.

### Quên mật khẩu

`/forgot-password` nhận email, phát OTP mục đích `RESET_PASSWORD` và lưu email trong session tạm. Tại `/reset-password`, người dùng nhập OTP và mật khẩu mới (ít nhất 8 ký tự). Chỉ khi OTP hợp lệ mật khẩu mới được hash PBKDF2 và ghi xuống database. OTP đã dùng hoặc hết hạn không dùng lại được.

## 3. Luồng Products

`Product` có quan hệ nhiều-một bắt buộc với `Category`; một `Category` có danh sách `products`. Dữ liệu chính: tên, mô tả, giá không âm, URL ảnh, trạng thái, thời điểm tạo và category.

| Nơi dùng | URL | Hành vi |
|---|---|---|
| Quản trị | `/admin/products` | Danh sách cả sản phẩm hiện/ẩn; thêm, sửa, xóa (xóa dùng POST). |
| Trang chủ | `/home` | Đọc 10 sản phẩm `status=1` mới nhất theo `created_at DESC, product_id DESC`. |
| Danh sách công khai | `/product?page=N` | Chỉ đọc sản phẩm hiển thị, 6 sản phẩm/trang; trang sai được đưa về trang hợp lệ. |
| Chi tiết | `/product/detail?id=N` | Hiển thị một sản phẩm đang hiển thị; ID không hợp lệ/ẩn/không tồn tại trả 400 hoặc 404. |

Khi xóa Category, mapping JPA cascade sẽ xóa Product liên quan trong cùng thao tác, tránh vi phạm khóa ngoại.

## 4. Kiểm thử trước khi deploy

Chạy các lệnh sau tại thư mục project:

```powershell
mvn test
mvn clean package
```

Sau khi build thành công, file deploy là `target/ServletCRUDMVC.war`. Smoke test SMTP cần thông tin SMTP thực tế; sau khi cấu hình và deploy WAR, kiểm thử thủ công theo checklist dưới đây.

## 5. Checklist nghiệm thu thủ công

1. Đăng ký email mới, kiểm tra không thể login trước kích hoạt; nhận OTP, xác thực và login thành công.
2. Nhập OTP sai, gửi lại OTP, thử OTP cũ, OTP mới và OTP đã dùng.
3. Quên mật khẩu, xác nhận OTP, đặt mật khẩu mới, login bằng mật khẩu mới.
4. Login admin `vuhoanglong/vuhoanglong`, tạo ít nhất 11 Product ở các Category khác nhau; kiểm tra thêm/sửa/xóa/ẩn.
5. Kiểm tra `/home` có đúng tối đa 10 sản phẩm mới nhất, `/product` có đúng 6 sản phẩm mỗi trang và link chi tiết hoạt động ở cả hai trang.
6. Login admin, mở **Email OTP**, nhập cấu hình Gmail, bấm **Lưu và gửi thử** và kiểm tra email nhận được trước khi kiểm thử OTP.

## 6. Ma trận yêu cầu bài tập

| Yêu cầu | Hiện thực | Cách kiểm tra |
|---|---|---|
| Kích hoạt tài khoản bằng OTP email | `RegisterController` tạo user inactive; `ActivateAccountController` xác thực OTP `ACTIVATE`. | Đăng ký account mới, lấy OTP email, kích hoạt rồi login. |
| Đăng nhập | `LoginController`, `UserService`, session `account` và cookie ghi nhớ. | Login bằng `vuhoanglong/vuhoanglong`. |
| Đăng xuất | `LogoutController` hủy session và xóa cookie ghi nhớ; nút header gửi POST. | Login kèm “Ghi nhớ”, bấm Đăng xuất, mở lại `/login` phải vẫn ở trang login. |
| Quản lý người dùng | `/admin/users`; admin xem danh sách, trạng thái OTP và khóa/mở hoặc gửi lại OTP. | Login admin, mở Người dùng, thử gửi lại OTP cho account chưa active hoặc khóa/mở account khác. |
| Cấu hình gửi email bằng giao diện | `/admin/mail-settings`, `SmtpSettingsDaoImpl`, `SmtpMailService`; filter chỉ cho admin. | Lưu Gmail SMTP, bấm “Lưu và gửi thử”, nhận email kiểm tra. |
| Quên mật khẩu qua OTP | `ForgotPasswordController`, `ResetPasswordController`, OTP `RESET_PASSWORD`. | Nhập email đã đăng ký, xác thực OTP, đặt mật khẩu mới. |
| Bảng Product quan hệ 1-n Category | `Product.category_id` là foreign key; `Category.products` là `@OneToMany`. | Chỉ tạo Product sau khi đã có Category; kiểm tra Product hiển thị đúng Category. |
| CRUD Product | `/admin/products`, `/admin/product/add`, `/edit`, `/delete`; filter giới hạn admin. | Thêm, sửa, ẩn/hiện và xóa Product bằng admin. |
| 10 Product mới nhất ở trang chủ | `HomeController` gọi `newest(10)`. | Tạo hơn 10 Product hiển thị, mở `/home`. |
| Phân trang 6 Product | `ProductListController` dùng `PAGE_SIZE = 6`. | Mở `/product?page=1`, `/product?page=2`. |
| Chi tiết Product | Link từ card đến `/product/detail?id=N`. | Bấm một Product ở `/home` hoặc `/product`. |

## 7. Điều kiện để OTP gửi email thật

Code gửi email đã hoàn tất qua SMTP. Với môi trường bài tập, admin lưu App Password của `vhoanglong54@gmail.com` qua `/admin/mail-settings`; `SmtpMailService` lấy cấu hình này trước. Nếu chưa có, service dùng `MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM` (và tùy chọn `MAIL_PORT`, `MAIL_STARTTLS`) từ system property/biến môi trường. Nếu cả hai đều thiếu, tạo user vẫn thành công nhưng màn hình kích hoạt hiển thị lỗi gửi OTP và cho phép gửi lại sau khi cấu hình.
