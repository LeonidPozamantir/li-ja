package leo.lija.app.config;

import leo.lija.app.game.HubMemo;
import leo.lija.app.socket.History;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
public class GameConfig {

    @Bean
    public HubMemo hubMemo(SocketIOService socketIOService, @Value("${game.message.lifetime}") int gameTimeout, @Value("${memo.game-hub.timeout}") int gameHubTimeout) {
        Supplier<History> gameHistory = () -> new History(gameTimeout);
        return new HubMemo(socketIOService, gameHistory, gameHubTimeout);
    }
}
