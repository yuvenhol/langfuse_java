package com.langfuse.prompt;

import com.langfuse.client.LangfuseClient;
import com.langfuse.config.LangfuseProperties;
import com.langfuse.prompt.model.Prompt;
import com.langfuse.prompt.storage.PromptStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DefaultPromptManager implements PromptManager {
    private final LangfuseClient langfuseClient;
    private final PromptStorage promptStorage;
    private final LangfuseProperties properties;

    private ThreadPoolTaskExecutor executor;

    {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("PromptUpdate-");
        executor.initialize();
    }


    public DefaultPromptManager(LangfuseProperties properties, PromptStorage promptStorage) {
        this.properties = properties;
        this.langfuseClient = new LangfuseClient(properties);
        this.promptStorage = promptStorage;
    }

    @PostConstruct
    public void init() {
        getAndCacheAllPrompts();
        log.info("Prompt manager initialized");
    }

    @Override
    public Optional<Prompt> getPrompt(String promptKey) {
        // First check if prompt exists in storage
        Optional<Prompt> storedPrompt = promptStorage.get(promptKey);
        if (storedPrompt.isPresent()) {
            Prompt prompt = storedPrompt.get();
            // Check if prompt needs refresh
            if (System.currentTimeMillis() - prompt.getTimestamp() > properties.getPrompt().getTtlSeconds() * 1000) {
                // Trigger async refresh
                executor.submit(() -> refreshPromptAsync(prompt));
            }
            return storedPrompt;
        }

        // If not in storage, fetch from Langfuse
        return getAndSavePrompt(promptKey);
    }

    @Override
    public void clearCache(String promptKey) {
        log.debug("Clearing cache for prompt: {}", promptKey);
        promptStorage.delete(promptKey);
    }

    @Override
    public List<Prompt> getAndCacheAllPrompts() {
        log.info("Fetching all prompts");
        // Fetch all prompts from Langfuse
        List<Prompt> prompts = langfuseClient.getAllPrompts();
        // Save them to storage
        for (Prompt prompt : prompts) {
            promptStorage.save(prompt);
        }
        log.info("Saved {} prompts to storage", prompts.size());
        return prompts;
    }



    public void refreshPromptAsync(Prompt prompt) {
        try {
            String promptKey = prompt.getName();
            log.info("Starting async refresh for prompt: {}", promptKey);
            Optional<Prompt> refreshed = getAndSavePrompt(promptKey);
            if (refreshed.isPresent()) {
                Prompt refreshedPrompt = refreshed.get();
                if (refreshedPrompt.getVersion() != prompt.getVersion()) {
                    log.info("Successfully refreshed prompt {} (version: {})", promptKey, refreshedPrompt.getVersion());
                }
            }
        } catch (Exception e) {
            log.error("Failed to refresh prompt {} asynchronously", prompt.getName(), e);
        }
    }

    private Optional<Prompt> getAndSavePrompt(String promptKey) {
        try {
            Optional<Prompt> promptOpt = langfuseClient.getPrompt(promptKey);

            // If prompt was found, save it to storage
            promptOpt.ifPresent(promptStorage::save);

            return promptOpt;
        } catch (Exception e) {
            log.error("Error fetching prompt {} from Langfuse", promptKey, e);
            return Optional.empty();
        }
    }

}
