# 后端 Dockerfile
FROM eclipse-temurin:25-jre-alpine

# 安装必要的工具
RUN apk add --no-cache curl

# 设置工作目录
WORKDIR /app

# 复制构建好的 jar 包
COPY target/eldercare-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用（使用 docker profile）
ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.profiles.active=docker"]
