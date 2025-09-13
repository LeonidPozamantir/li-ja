package leo.lija.system.ai;

import leo.lija.system.config.CraftyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Autowired
    private CraftyConfig craftyConfig;

    @Bean
    @Primary
    public StupidAi stupidAi() {
        return new StupidAi();
    }

    @Bean
    public CraftyAi craftyAi(@Autowired CraftyConfig config2) {
        return new CraftyAi(craftyConfig);
    }
}
