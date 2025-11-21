package leo.lija.app.config;

import leo.lija.app.game.History;
import leo.lija.app.game.HubMemo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import java.util.function.Supplier;

@Configuration
public class GameConfig {

    @Bean
    public HubMemo hubMemo(TaskExecutor executor, SocketIOService socketIOService, @Value("${game.message.lifetime}") int gameTimeout) {
        Supplier<History> gameHistory = () -> new History(gameTimeout);
        return new HubMemo(executor, socketIOService, gameHistory);
    }
}
