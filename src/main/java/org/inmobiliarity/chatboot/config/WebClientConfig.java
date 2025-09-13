package org.inmobiliarity.chatboot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder, WasiApiProperties properties) {
        log.info("Wasi baseUrl configured: {} version: {}", properties.baseUrl(), properties.version());
        return builder
                .baseUrl(properties.baseUrl() + "/" + properties.version())
                .defaultHeader("id_company", properties.companyId())
                .defaultHeader("wasi_token", properties.token())
                .build();
    }
}

