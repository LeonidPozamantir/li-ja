package leo.lija.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    String host;

    @Bean
    public JedisPool pool() {
        return new JedisPool(host, 6379);
    }
}
