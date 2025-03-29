package com.langfuse.prompt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.langfuse.config.LangfuseProperties;
import com.langfuse.prompt.DefaultPromptManager;
import com.langfuse.prompt.model.Prompt;
import com.langfuse.prompt.storage.InMemoryPromptStorage;
import com.langfuse.prompt.storage.PromptStorage;
import org.springframework.util.Assert;

@ExtendWith(MockitoExtension.class)
class DefaultPromptManagerTest {

    private DefaultPromptManager promptManager;
    private LangfuseProperties properties;
    private PromptStorage promptStorage;

    @BeforeEach
    void setUp() {
        properties = new LangfuseProperties();
        properties.setPublicKey("pk-lf-f365ca79-49ce-46a7-9d4c-aa0562c87927");
        properties.setSecretKey("sk-lf-6d0ee311-77ef-419a-9607-52315b047c36");
        properties.setBaseUrl("http://127.0.0.1:3000");
        
        promptStorage = new InMemoryPromptStorage();
        promptManager = new DefaultPromptManager(properties, promptStorage);
    }

    @Test
    void getPrompt() {
        Optional<Prompt> prompt = promptManager.getPrompt("test");
        assertTrue(prompt.isPresent());
    }

    @Test
    void getAllPrompts() {
        List<Prompt> prompt = promptManager.getAndCacheAllPrompts();
        Assert.notEmpty(prompt,"prompts should not be empty");
    }

}
