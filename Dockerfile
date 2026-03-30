# 基礎 Java 17 鏡像
FROM eclipse-temurin:19-jdk-alpine
WORKDIR /app

# 複製 Maven Wrapper 和 pom.xml
COPY mvnw ./
COPY .mvn ./.mvn
COPY pom.xml ./

# 下載依賴
RUN ./mvnw dependency:go-offline -B

# 複製原始碼
COPY src ./src

# 打包（跳過測試）
RUN ./mvnw clean package -DskipTests -B

# 把打包好的 jar 複製到當前目錄，方便啟動
RUN cp target/*.jar app.jar

# 啟動命令（直接用當前目錄的 app.jar）
CMD ["java", "-jar", "app.jar"]