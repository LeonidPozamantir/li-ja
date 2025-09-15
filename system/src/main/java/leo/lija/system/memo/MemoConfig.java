package leo.lija.system.memo;

import leo.lija.system.config.MemoConfigProperties;
import leo.lija.system.db.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoConfig {

    @Bean
    public VersionMemo versionMemo(@Autowired GameRepo repo, @Autowired MemoConfigProperties config) {
        return new VersionMemo(repo::playerOnly, config.version().timeout());
    }
}
