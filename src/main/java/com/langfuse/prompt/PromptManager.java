package com.langfuse.prompt;

import java.util.List;
import java.util.Optional;

import com.langfuse.prompt.model.Prompt;

public interface PromptManager {
    /**
     * Get a prompt by its key.
     * If it not exists, fetch it from Langfuse and cache it if not already cached
     * 
     * If the prompt is out of time,it will be async updated
     * 
     * @param promptKey the key of the prompt
     * 
     * If the prompt is not found, an empty Optional will be returned
     * @return Optional containing the prompt if found
     */
    Optional<Prompt> getPrompt(String promptKey);

    /**
     * Fetch all prompts from Langfuse and cache them
     * 
     * This method is called when the application starts
     */
    List<Prompt> getAndCacheAllPrompts();

    /**
     * Clear the cache for a specific prompt
     * @param promptKey the ID of the prompt to clear from cache
     */
    void clearCache(String promptKey);
    
}
