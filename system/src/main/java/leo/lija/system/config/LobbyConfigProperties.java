package leo.lija.system.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lobby")
public record LobbyConfigProperties(Poll poll) {

    public static record Poll(int duration, int sleep) {}
}
