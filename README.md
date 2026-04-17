Commerce API

1. Mô tả project:
Đây là dự án backend thương mại điện tử viết bằng Spring Boot. Dự án có các chức năng chính gồm đăng ký tài khoản, đăng nhập, làm mới token, quản lý sản phẩm, quản lý danh mục và quản lý đơn hàng.

2. Công nghệ sử dụng: Java 21
Spring Boot 4,
Spring Web MVC,
Spring Security,
JWT jjwt,
Spring Data JPA,
MySQL,
Flyway,
JUnit 5,
Mockito,
JaCoCo

3. Hướng dẫn cài đặt:

- Yêu cầu:
  - JDK 21
  - MySQL (database: mor_training, cổng mặc định: 3306)
  - IntelliJ

- Tạo các biến môi trường sau:
    - DB_USERNAME
    - DB_PASSWORD
    - JWT_SECRET
    - JWT_EXPIRATION_MS
    - JWT_REFRESH_EXPIRATION_MS
- Danh sách enpoint:
  - Nhóm Auth
    - POST /auth/register dùng để đăng ký tài khoản mới. 
    - POST /auth/login dùng để đăng nhập và nhận access token cùng refresh token. 
    - POST /auth/refresh có tham số refreshToken để cấp token mới. 
    - POST /auth/logout có tham số refreshToken để thu hồi refresh token. 

  - Nhóm Product
    - GET /products dùng để lấy danh sách sản phẩm theo phân trang. 
    - GET /products/with-category dùng để lấy danh sách sản phẩm kèm thông tin danh mục. 
    - GET /products/{productId} dùng để lấy chi tiết sản phẩm. 
    - POST /products dùng để tạo sản phẩm mới. 
    - PUT /products/{id} dùng để cập nhật sản phẩm. 
    - DELETE /products/{id} dùng để xóa sản phẩm. 

  - Nhóm Category
    - GET /category dùng để lấy danh sách danh mục. 
    - POST /category dùng để tạo danh mục mới. 

  - Nhóm Order
    - GET /order dùng để lấy danh sách đơn hàng. 
    - GET /order/{orderId} dùng để lấy chi tiết đơn hàng. 
    - POST /order dùng để tạo đơn hàng mới. 
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/26fd4011-66a3-4ffc-ac29-be5ada67d664" />

