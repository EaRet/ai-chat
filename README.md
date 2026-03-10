# AI Chat

一个基于 LangChain4j + Spring Boot + Vue 3 的智能对话系统。

## 功能特性

- **智能对话**：集成阿里云通义千问大语言模型，支持流式响应
- **AI 编程助手**：内置代码辅助功能，可帮助用户解答编程问题
- **面试题生成**：提供面试题生成工具
- **RAG 知识库**：支持检索增强生成
- **MCP 工具集成**：支持 Model Context Protocol 工具调用
- **输入安全过滤**：内置安全guardrail，保护系统安全

## 技术栈

### 后端
- Java 21
- Spring Boot 3.5.11
- LangChain4j 1.1.0 (集成阿里云 DashScope 通义千问)
- LangChain4j MCP
- Jsoup (网页爬取)

### 前端
- Vue 3
- Vite
- Axios

## 项目结构

```
ai-chat/
├── src/                    # Spring Boot 后端源码
├── ai-chat-front/          # Vue 前端项目
├── pom.xml                 # Maven 配置
└── mvnw                    # Maven wrapper
```

## 快速启动

### 后端启动

```bash
# 配置 application.yml 中的 DashScope API Key
./mvnw spring-boot:run
```

### 前端启动

```bash
cd ai-chat-front
npm install
npm run dev
```

## 配置说明

在 `src/main/resources/application.yml` 中配置：

```yaml
dashscope:
  api-key: your-api-key
```

## License

MIT
