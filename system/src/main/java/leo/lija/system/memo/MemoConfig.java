package leo.lija.system.memo;

import leo.lija.system.config.MemoConfigProperties;
import leo.lija.system.db.EntryRepo;
import leo.lija.system.db.GameRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemoConfig {

    @Bean
    public VersionMemo versionMemo(GameRepo repo, MemoConfigProperties config) {
        return new VersionMemo(repo::playerOnly, config.version().timeout());
    }

    @Bean
    public EntryMemo entryMemo(EntryRepo repo) {
        return new EntryMemo(repo::lastId);
    }
}
