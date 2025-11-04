package leo.lija.system.ai;

import leo.lija.system.config.CraftyConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Bean
    @Primary
    public StupidAi stupidAi() {
        return new StupidAi();
    }

    @Bean
    public CraftyAi craftyAi(CraftyServer server) {
        return new CraftyAi(server);
    }

    @Bean
    public RemoteAi remoteAi(@Value("${ai.remote.url}") String remoteUrl) {
        return new RemoteAi(remoteUrl);
    }
}
