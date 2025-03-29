package com.langfuse.prompt.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ChatMessage {
    private String role;
    private String content;
}
