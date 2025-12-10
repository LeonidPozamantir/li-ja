package leo.lija.app.config;

import leo.lija.app.game.History;
import leo.lija.app.game.HubMaster;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class GameConfig {

    @Bean
    public HubMaster hubMaster(SocketIOService socketIOService, @Value("${game.message.lifetime}") int gameTimeout, @Value("${game.uid.timeout}") int uidTimeout, @Value("${game.hub.timeout}") int hubTimeout) {
        Supplier<History> gameHistory = () -> new History(gameTimeout);
        return new HubMaster(socketIOService, gameHistory, uidTimeout, hubTimeout);
    }
}
