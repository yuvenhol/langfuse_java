package com.langfuse.prompt.storage;

import com.langfuse.prompt.model.Prompt;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryPromptStorage implements PromptStorage {
    private final ConcurrentHashMap<String, Prompt> promptMap = new ConcurrentHashMap<>();

    @Override
    public Optional<Prompt> get(String promptKey) {
        return Optional.ofNullable(promptMap.get(promptKey));
    }

    @Override
    public void save(Prompt prompt) {
        promptMap.put(prompt.getName(), prompt);
    }

    @Override
    public void delete(String promptKey) {
        promptMap.remove(promptKey);
    }

    @Override
    public boolean exists(String promptKey) {
        return promptMap.containsKey(promptKey);
    }
}
