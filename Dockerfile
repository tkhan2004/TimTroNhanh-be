# GIAI ĐOẠN 1: Build (Dùng JDK đầy đủ để biên dịch code)
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

# Sao chép Maven Wrapper
COPY .mvn/ ./.mvn
COPY mvnw .

# Cấp quyền thực thi cho file mvnw
RUN chmod +x mvnw
# -------------------------

# Sao chép file cấu hình Maven
COPY pom.xml .

# Sao chép toàn bộ code
COPY src ./src

# Chạy lệnh build để tạo file JAR
RUN ./mvnw clean package -DskipTests


# GIAI ĐOẠN 2: Run (Dùng JRE nhỏ gọn để chạy)
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Chỉ sao chép file JAR đã được build từ Giai đoạn 1
COPY --from=builder /app/target/PhongTro247-backend-0.0.1-SNAPSHOT.jar PhongTro247-backend-0.0.1-SNAPSHOT.jar

# Thực thi
ENTRYPOINT ["java","-jar","/app/PhongTro247-backend-0.0.1-SNAPSHOT.jar"]