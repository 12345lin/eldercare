package com.wmm.eldercare.core.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置类
 *
 * <p>Spring AI 2.0 的自动配置只提供 {@code ChatClient.Builder}，
 * 不直接注册 {@code ChatClient} Bean（builder 模式），需要在这里 build 一次。</p>
 */
@Configuration
public class AiChatConfig {

    /**
     * 创建 ChatClient Bean，供 ChatService / AssessmentService 注入使用
     */
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}