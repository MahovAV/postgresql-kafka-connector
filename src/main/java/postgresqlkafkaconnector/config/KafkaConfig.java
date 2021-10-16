package postgresqlkafkaconnector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@EnableKafka
@Configuration
public class KafkaConfig {
    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(10);
    }
}