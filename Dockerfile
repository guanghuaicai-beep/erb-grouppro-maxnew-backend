# 基础 Java 17 镜像（和你本地 JDK 版本保持一致）
FROM eclipse-temurin:19-jdk-alpine
WORKDIR /app

# 复制 Maven Wrapper 和 pom.xml
COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml ./

# 下载依赖
RUN ./mvnw dependency:go-offline -B

# 复制源码
COPY src ./src

# 打包（跳过测试）
RUN ./mvnw clean package -DskipTests -B

# 启动命令
CMD ["java", "-jar", "target/*.jar"]