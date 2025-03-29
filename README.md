# Langfuse-java SDK

[English](#english) | [中文](#chinese)

<a id="english"></a>
## English

### Overview
Langfuse-java SDK provides integration with Langfuse for efficient prompt management and LLM operations. This SDK supports Spring Boot auto-configuration, making it easy to integrate into your Spring Boot applications and simplifying LLM operations.

### Features
- Spring Boot auto-configuration for quick integration.
- Prompt management: Currently uses in-memory cache, caches all prompts at startup, and asynchronously refreshes expired data. The PromptManager remains available even when the Langfuse service crashes.
- Tracing: Coming soon...

### Installation

Add the following configuration to your `application.properties` or `application.yml`:

```yaml
langfuse:
    base-url: https://your-langfuse-instance.com
    public-key: your-public-key
    secret-key: your-secret-key
    prompt:
      env-label: your-environment-label # dev, test, preview, production based on environment
      ttl-seconds: 60 # prompt cache time
      
    http-connection-time-out: 1000 # http connection timeout
    http-read-time-out: 5000 # http read timeout
```

### Usage
Since this SDK uses Spring technology, you need to inject the required beans into the Spring container and then use them in your Spring Boot application.

```java
@SpringBootApplication(scanBasePackages = {"your-package", "com.langfuse"})
```

```java
@Autowired
private PromptManager promptManager;

// Get a single prompt
Optional<Prompt> prompt = promptManager.getPrompt("your-prompt-key");
```

---

<a id="chinese"></a>
## 中文

### 概述
Langfuse-java SDK 提供与Langfuse平台的无缝集成，用于高效的提示词管理和LLM操作等功能。该SDK支持Spring Boot自动配置，可以轻松集成到您的Spring Boot应用程序中，简化LLM操作流程。

### 特性
- Spring Boot自动配置，快速集成。
- prompt: 目前使用内存cache，启动时缓存全部prompt，过期数据异步刷新。支持在langfuse服务crash等异常情况下promptManager仍然可用。
- trace: coming soon...

### 安装

在您的`application.properties`或`application.yml`中添加以下配置：

```yaml
langfuse:
    base-url: https://your-langfuse-instance.com
    public-key: your-public-key
    secret-key: your-secret-key
    prompt:
      env-label: your-environment-label #dev、test、preview、production 根据环境配置
      ttl-seconds: 60 #prompt缓存时间
      
    http-connection-time-out: 1000 #http连接超时时间
    http-read-time-out: 5000 #http读取超时时间
```

### 使用方法
由于使用了Spring相关技术，您需要将所需Bean注入到Spring容器中，然后在您的Spring Boot应用程序中使用。
```java
@SpringBootApplication(scanBasePackages = {"your-package","com.langfuse"})
```

```java
@Autowired
private PromptManager promptManager;

// 获取单个提示词
Optional<Prompt> prompt = promptManager.getPrompt("your-prompt-key");
```