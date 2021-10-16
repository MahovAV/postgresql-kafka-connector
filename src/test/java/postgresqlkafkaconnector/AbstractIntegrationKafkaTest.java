package postgresqlkafkaconnector;

import lombok.SneakyThrows;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import postgresqlkafkaconnector.repository.KafkaLogRepository;
import postgresqlkafkaconnector.util.BdKafkaHelper;

import java.nio.file.Path;
import java.util.concurrent.ScheduledExecutorService;

@SpringBootTest
@Testcontainers
public abstract class AbstractIntegrationKafkaTest {

    @ClassRule
    protected static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
    @ClassRule
    protected static GenericContainer postgreSQLContainer = new GenericContainer(
            new ImageFromDockerfile("postgresql-with-wal2json", false)
                    .withDockerfile(getPathFromClasspath("postgresqlwithwal2json/Dockerfile")))
            .withExposedPorts(5432);


    @Autowired
    protected KafkaLogRepository kafkaLogRepository;
    @Autowired
    protected BdKafkaHelper bdKafkaHelper;
    @Autowired
    protected ScheduledExecutorService scheduledExecutorService;

    static {
        kafkaContainer.start();
        postgreSQLContainer.start();
    }


    @DynamicPropertySource
    static void setKafkaAndPostgresqlUris(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:" + postgreSQLContainer.getMappedPort(5432) + "/postgres");
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "secret");


        registry.add("spring.kafka.bootstrap-servers", () -> kafkaContainer.getBootstrapServers());
    }

    @SneakyThrows
    private static Path getPathFromClasspath(String path) {
        return new ClassPathResource(path).getFile().toPath();
    }

    @AfterEach
    public void afterEach() {
        kafkaLogRepository.deleteAll();
    }
}
