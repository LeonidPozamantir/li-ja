package leo.lija.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai.crafty")
public record CraftyConfigProperties(String execPath, String bookPath) {}
