package com.langfuse.prompt.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Prompt {
    private String name;
    private String version;
    private Long timestamp;
    
    public enum Type {
        CHAT,
        TEXT
    }
    
    private Type type;
    
    // For TEXT type
    private String prompt;
    
    // For CHAT type
    private List<ChatMessage> messages;
    
    private List<String> labels;
    private List<String> tags;
    private String commitMessage;
}
