package leo.lija.app.ai;

import leo.lija.app.config.CraftyConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CraftyAi craftyAi(@Autowired CraftyConfigProperties config) {
        return new CraftyAi(config);
    }
}
