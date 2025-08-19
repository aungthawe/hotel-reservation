package com.project.hotel.configurations;

import com.google.api.client.util.Value;
import com.google.genai.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String apikey;

    @Bean
    public Client genAiClient(){
        return Client.builder().apiKey(apikey).build();
    }
}
