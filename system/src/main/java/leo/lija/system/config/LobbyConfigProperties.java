package leo.lija.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lobby")
public record LobbyConfigProperties(Sync sync) {

    public static record Sync(int duration, int sleep, Max message, Max entry) {

        public static record Max(int max) {}
    }
}
