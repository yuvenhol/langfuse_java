# LLMOps SDK

### 概述
LLMOps SDK 提供与Langfuse等LLmOps平台的无缝集成，用于高效的提示词管理和LLM操作等功能。该SDK支持Spring Boot自动配置，可以轻松集成到您的Spring Boot应用程序中，简化LLM操作流程。


### 特性
- Spring Boot自动配置，快速集成。
- prompt: 目前使用内存cache，启动时缓存全部prompt，过期数据异步刷新。支持在langfuse服务crash等异常情况下promptManager仍然可用。
- trace: coming soon...
### 安装

在您的`pom.xml`中添加以下依赖：

```xml
<dependency>
    <groupId>com.langfuse</groupId>
    <artifactId>framework-llmops-sdk</artifactId>
    <version>{version}</version>
</dependency>
```

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