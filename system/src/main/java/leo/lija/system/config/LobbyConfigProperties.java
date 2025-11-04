package leo.lija.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lobby")
public record LobbyConfigProperties(Sync sync) {

    public record Sync(Max message, Max entry) {

        public record Max(int max) {}
    }
}
