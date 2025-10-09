package leo.lija.app.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "async")
@RequiredArgsConstructor
@Getter
public class ExecutorConfigProperties {

    private final AsyncConfig actions;

    record AsyncConfig(int coreSize, int maxSize) {}

}
