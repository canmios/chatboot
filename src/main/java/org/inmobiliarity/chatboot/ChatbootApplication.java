package org.inmobiliarity.chatboot;

import org.inmobiliarity.chatboot.config.WasiApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WasiApiProperties.class)
public class ChatbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatbootApplication.class, args);
    }

}
