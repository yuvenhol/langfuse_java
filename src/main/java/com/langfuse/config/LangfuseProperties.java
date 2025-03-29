package com.langfuse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "langfuse")
public class LangfuseProperties {
    private String publicKey;
    private String secretKey;
    private String baseUrl;

    private int httpReadTimeOut = 5000;
    private int httpConnectionTimeOut = 1000;


    private Prompt prompt = new Prompt();

    @Data
    public class Prompt {
        private String envLabel = "production";
        private Integer ttlSeconds = 60;

    }

}
