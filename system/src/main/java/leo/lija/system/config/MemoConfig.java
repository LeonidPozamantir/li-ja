package leo.lija.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "memo")
public record MemoConfig(Timeout version, Alive alive, Timeout watcher, Timeout username) {

    public static record Timeout(int timeout) {}
    public static record Alive(int softTimeout, int hardTimeout) {}
}
