package leo.lija.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "memo")
public record MemoConfigProperties(Timeout version, Alive alive, Timeout watcher, Timeout username, Timeout hook, Timeout finisherLock, Timeout socket) {

    public static record Timeout(int timeout) {}
    public static record Alive(int softTimeout, int hardTimeout) {}
}
