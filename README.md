# Personal Finance Tracker

别问，问就是专业课期末大作业

## 技术栈

### 后端
- Spring Boot 3.5.7
- Spring Security
- MySQL

### 前端
- Vue 3
- Vite
- Vuetify
- Vue Router
- Pinia
- Axios

## 快速开始

### 🚀 开发模式

```bash
# 终端 1 - 启动后端
mvnw.cmd spring-boot:run

# 终端 2 - 启动前端
cd frontend
pnpm install
pnpm run dev
```

访问 http://localhost:3000 查看前端应用

### 📦 生产构建

```bash
mvnw.cmd clean package
java -jar target/personal-finance-tracker-0.0.1-SNAPSHOT.jar
```

访问 http://localhost:8000

## API 路由

- 前端: `/`
- 后端 API: `/api/*`

## 配置

请参考 `src/main/resources/application.properties` 获取项目配置
