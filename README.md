# PhongTro247 Backend - Hệ thống tìm kiếm phòng trọ thông minh

## 📋 Mô tả dự án

PhongTro247 là một nền tảng tìm kiếm và cho thuê phòng trọ hiện đại, tích hợp công nghệ AI/ML để cung cấp trải nghiệm tìm kiếm thông minh và cá nhân hóa cho người dùng.

## 🚀 Tính năng hiện tại

### 🔐 Xác thực và Phân quyền
- **JWT Authentication** với refresh token
- **Role-based Authorization** (ADMIN, OWNER, RENTER)
- **Security** với Spring Security
- **Password encryption** với BCrypt

### 👥 Quản lý người dùng
- Đăng ký/Đăng nhập
- Quản lý profile người dùng
- Phân quyền theo vai trò
- Upload avatar với Cloudinary

### 🏠 Quản lý phòng trọ (Đang phát triển)
- **Loại phòng**: Phòng trọ, Chung cư, Nhà nguyên căn, Căn hộ dịch vụ, Studio, Penthouse
- **CRUD operations** cho phòng
- **Tìm kiếm và lọc** theo nhiều tiêu chí
- **Upload hình ảnh** phòng
- **Quản lý tiện ích** phòng
- **Geolocation** với latitude/longitude

## 🛠️ Công nghệ sử dụng

### Backend Framework
- **Spring Boot 3.x**
- **Spring Security 6.x**
- **Spring Data JPA**
- **PostgreSQL Database**
- **JWT (JSON Web Token)**

### Cloud Services
- **Cloudinary** - Quản lý hình ảnh
- **PostgreSQL** - Cơ sở dữ liệu chính

### Testing
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**

### Build Tools
- **Maven**
- **Java 21**

## 📁 Cấu trúc dự án

```
src/main/java/com/phongtro247backend/
├── config/                 # Cấu hình (Security, JWT, Cloudinary)
├── controller/             # REST Controllers
├── dto/                    # Data Transfer Objects
├── entity/                 # JPA Entities
│   └── enums/             # Enums (UserRole, RoomStatus, RoomType)
├── repository/            # JPA Repositories
├── service/               # Business Logic
│   └── ServiceImp/        # Service Implementations
└── payload/               # Response wrappers

src/test/java/             # Unit & Integration Tests
```

## 🔧 Cài đặt và chạy dự án

### Yêu cầu hệ thống
- Java 21+
- Maven 3.6+
- PostgreSQL

### Cài đặt

1. **Clone repository**
```bash
git clone https://github.com/tkhan2004/TimTroNhanh-be.git
cd TimTroNhanh-be
```

2. **Cấu hình database**
```sql
CREATE DATABASE phongtro;
```

3. **Cấu hình application.yml**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/phongtro
    username: your_username
    password: your_password
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

app:
  jwt:
    secret: your_jwt_secret_key
    expiration: 86400000

cloudinary:
  cloud-name: your_cloud_name
  api-key: your_api_key
  api-secret: your_api_secret
```

4. **Chạy ứng dụng**
```bash
./mvnw spring-boot:run
```

5. **Chạy tests**
```bash
./mvnw test
```

## 📚 API Documentation

### Authentication Endpoints
```
POST /api/auth/register     # Đăng ký
POST /api/auth/login        # Đăng nhập
POST /api/auth/logout       # Đăng xuất
POST /api/auth/refresh-token # Refresh token
GET  /api/auth/validate     # Validate token
```

### User Management
```
GET  /api/users/profile     # Lấy profile hiện tại
PUT  /api/users/profile     # Cập nhật profile
GET  /api/users/{id}        # Lấy user theo ID (Admin)
PUT  /api/users/{id}        # Cập nhật user (Admin)
```

### Room Management (Đang phát triển)
```
GET    /api/rooms           # Lấy danh sách phòng (có filter)
GET    /api/rooms/{id}      # Chi tiết phòng
POST   /api/rooms           # Tạo phòng mới (Owner)
PUT    /api/rooms/{id}      # Cập nhật phòng (Owner)
DELETE /api/rooms/{id}      # Xóa phòng (Owner)
```

## 🤖 Định hướng phát triển với AI/ML

### 🎯 Phase 1: Recommendation System
- **Collaborative Filtering**: Gợi ý dựa trên hành vi người dùng tương tự
- **Content-Based Filtering**: Gợi ý dựa trên đặc điểm phòng
- **Hybrid Approach**: Kết hợp cả hai phương pháp

### 🧠 Phase 2: Smart Search & Matching
- **Natural Language Processing**: Tìm kiếm bằng ngôn ngữ tự nhiên
- **Image Recognition**: Phân loại và tìm kiếm theo hình ảnh
- **Price Prediction**: Dự đoán giá phòng hợp lý
- **Location Intelligence**: Phân tích vị trí và tiện ích xung quanh

### 🔮 Phase 3: Advanced AI Features
- **Chatbot AI**: Hỗ trợ tư vấn tự động 24/7
- **Fraud Detection**: Phát hiện tin đăng giả mạo
- **Market Analysis**: Phân tích thị trường bất động sản
- **Personalized Dashboard**: Dashboard cá nhân hóa cho từng user

### 🛠️ AI/ML Technology Stack
```
Machine Learning:
├── Python/FastAPI          # ML Service
├── TensorFlow/PyTorch      # Deep Learning
├── Scikit-learn           # Traditional ML
├── Pandas/NumPy           # Data Processing
└── Redis                  # ML Model Caching

NLP & Computer Vision:
├── Transformers (Hugging Face)
├── OpenCV                 # Image Processing
├── YOLO/ResNet           # Object Detection
└── spaCy/NLTK            # Text Processing
```

## 🏗️ Kiến trúc Microservices

### 🎯 Microservices Architecture Plan

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │    │  Load Balancer  │    │   Web Client    │
│   (Spring Cloud │    │     (Nginx)     │    │   (React/Vue)   │
│    Gateway)     │    │                 │    │                 │
└─────────┬───────┘    └─────────────────┘    └─────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Service Mesh (Istio)                         │
└─────────────────────────────────────────────────────────────────┘
        │            │                      │           │
        │            │                      │           │
        ▼            ▼                      ▼           ▼
    ┌─────────┐ ┌─────────┐            ┌─────────┐ ┌─────────┐
    │   Web   │ │  Chat   │            │   AI    │ │ Payment │
    │ Service │ │ Service │            │ Service │ │ Service │
    └─────────┘ └─────────┘            └─────────┘ └─────────┘
        │           │                       │           │
        ▼           ▼                       ▼           ▼
    ┌─────────┐ ┌─────────┐            ┌─────────┐ ┌─────────┐
    │ User DB │ │ Chat DB │            │  ML DB  │ │ Pay DB  │
    │ (MySQL) │ │(MongoDB)│            │(MongoDB)│ │ (MySQL) │
    └─────────┘ └─────────┘            └─────────┘ └─────────┘
```

### 📦 Planned Microservices

#### 1. **Web Service** (Hiện tại - Đang thực hiện)
- Authentication & Authorization
- User Profile Management
- Role Management- Room CRUD Operations
- Search & Filtering
- Image Management
- Geolocation Services

#### 2. **Chat Service** (Tương lai)
- Real-time Messaging
- WebSocket Support
- Message History
- AI Chatbot Integration

#### 3. **AI/ML Service** (Tương lai)
- Recommendation Engine
- Price Prediction
- Image Recognition
- NLP Search

#### 4. **Payment Service** (Tương lai)
- Payment Processing
- Transaction History
- Subscription Management
- Invoice Generation

#### 5. **Notification Service** (Tương lai)
- Email Notifications
- Push Notifications
- SMS Integration
- Real-time Alerts

### 🔧 Microservices Technology Stack
```
Service Discovery:
├── Eureka Server          # Service Registry
├── Spring Cloud Config    # Configuration Management
└── Spring Cloud Gateway   # API Gateway

Communication:
├── REST APIs              # Synchronous
├── Apache Kafka          # Asynchronous Messaging
├── WebSocket             # Real-time Communication
└── gRPC                  # High-performance RPC

Monitoring & Observability:
├── Prometheus            # Metrics Collection
├── Grafana              # Monitoring Dashboard
├── ELK Stack            # Logging
├── Jaeger               # Distributed Tracing
└── Spring Boot Actuator # Health Checks

Containerization:
├── Docker               # Containerization
├── Kubernetes          # Orchestration
├── Helm                # Package Management
└── Istio               # Service Mesh
```

## 🗺️ Roadmap phát triển

### Foundation
- ✅ User Authentication & Authorization
- ✅ Basic Room Management
- 🔄 Room CRUD Operations
- 🔄 Search & Filtering

### Core Features
- 📋 Chat System
- 📋 Advanced Search
- 📋 Mobile API

### AI Integration
- 📋 Recommendation System
- 📋 Price Prediction
- 📋 Image Recognition
- 📋 NLP Search

### Microservices
- 📋 Service Decomposition
- 📋 API Gateway
- 📋 Service Discovery
- 📋 Monitoring & Logging

### Advanced AI
- 📋 Advanced ML Models
- 📋 Real-time Analytics
- 📋 Predictive Analytics
- 📋 AI Chatbot

## 📞 Liên hệ

- **Developer**: Khang Nguyen
- **Email**: thanhkhangdev@gmail.com
- **GitHub**: [@tkhan2004](https://github.com/tkhan2004)
- **Project Link**: [https://github.com/tkhan2004/TimTroNhanh-be](https://github.com/tkhan2004/PhongTro247-backend)

---

⭐ **Star this repo if you find it helpful!**
