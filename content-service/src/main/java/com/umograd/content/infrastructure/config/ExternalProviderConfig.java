package com.umograd.content.infrastructure.config;

import com.umograd.content.application.task.command.ImportTasksHandler;
import com.umograd.content.domain.external.ContentProvider;
import com.umograd.content.domain.task.TaskRepository;
import com.umograd.content.infrastructure.external.HttpContentProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ExternalProviderConfig {

    @Bean
    public WebClient contentWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    public ContentProvider contentProvider(WebClient contentWebClient) {
        // Replace with your actual external provider base URL
        return new HttpContentProvider(contentWebClient, "https://external-provider.example.com");
    }

    @Bean
    public ImportTasksHandler importTasksHandler(TaskRepository taskRepository, ContentProvider contentProvider) {
        return new ImportTasksHandler(taskRepository, contentProvider);
    }
}
