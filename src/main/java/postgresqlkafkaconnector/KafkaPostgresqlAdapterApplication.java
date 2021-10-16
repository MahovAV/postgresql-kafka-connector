package postgresqlkafkaconnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KafkaPostgresqlAdapterApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaPostgresqlAdapterApplication.class, args);
    }
}
