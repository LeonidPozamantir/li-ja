package leo.lija.app.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    @Value("${socket.host:localhost}")
    private String host;

    @Value("${socket.port:9092}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer(
            @Value("${async.websockets.parallelism}") int parallelism,
            @Value("${async.websockets.max-size}") int maxSize
    ) {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);

        // Enable CORS for React app
        config.setOrigin("*");

        // Connection settings
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);

        int cores = Runtime.getRuntime().availableProcessors();
        config.setWorkerThreads(Math.min(cores * parallelism, maxSize));

        return new SocketIOServer(config);
    }
}