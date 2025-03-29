package com.langfuse.prompt.storage;

import java.util.Optional;

import com.langfuse.prompt.model.Prompt;

public interface PromptStorage {
    Optional<Prompt> get(String promptKey);
    void save(Prompt prompt);
    void delete(String promptKey);
    boolean exists(String promptKey);
}
