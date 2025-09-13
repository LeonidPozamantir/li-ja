package leo.lija.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crafty")
public record CraftyConfig(String execPath, String bookPath) {}
