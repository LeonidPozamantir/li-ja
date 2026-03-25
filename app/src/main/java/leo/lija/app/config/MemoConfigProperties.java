package leo.lija.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "memo")
public record MemoConfigProperties(Timeout hook, Timeout finisherLock, Timeout username) {

    public record Timeout(int timeout) {}
}
