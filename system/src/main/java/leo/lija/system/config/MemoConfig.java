package leo.lija.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "memo")
public record MemoConfig(Version version, Alive alive) {

    public static record Version(int timeout) {}
    public static record Alive(int softTimeout, int hardTimeout) {}
}
