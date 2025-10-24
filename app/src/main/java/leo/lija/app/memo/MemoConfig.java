package leo.lija.app.memo;

import leo.lija.app.config.MemoConfigProperties;
import leo.lija.app.db.GameRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoConfig {

    @Bean
    public VersionMemo versionMemo(GameRepo repo, MemoConfigProperties config) {
        return new VersionMemo(repo::pov, config.version().timeout());
    }

}
