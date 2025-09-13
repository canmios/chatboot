package org.inmobiliarity.chatboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wasi.api")
public record WasiApiProperties(
        String baseUrl,
        String version,
        String companyId,
        String token
) { }
