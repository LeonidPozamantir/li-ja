package leo.lija.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {

    private final ExecutorConfigProperties config;

    @Bean
    @Primary
    public TaskExecutor actionsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getActions().coreSize());
        executor.setMaxPoolSize(config.getActions().maxSize());
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("actions-");
        executor.initialize();
        return executor;
    }
}
