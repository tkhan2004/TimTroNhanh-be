#
FROM  eclipse-temurin:21-jdk-jammy
# copy file jar từ local vào docker image
COPY target/PhongTro247-backend-0.0.1-SNAPSHOT.jar PhongTro247-backend-0.0.1-SNAPSHOT.jar
# thực thi
ENTRYPOINT ["java","-jar","/PhongTro247-backend-0.0.1-SNAPSHOT.jar"]
