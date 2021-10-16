package postgresqlkafkaconnector.integrationtests;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import postgresqlkafkaconnector.AbstractIntegrationKafkaTest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KafkaMonitoringTest extends AbstractIntegrationKafkaTest {
    @BeforeAll
    public static void createTopic(@Value("${topics.kafka-to-postgres}") String topic) {
        var admin = AdminClient.create(Map.of(BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()));
        admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1)));
    }

    @AfterAll
    public static void deleteTopic(@Value("${topics.kafka-to-postgres}") String topic) {
        var admin = AdminClient.create(Map.of(BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()));
        admin.deleteTopics(List.of(topic));
    }

    @ParameterizedTest
    @ValueSource(longs = {2, 5, 10})
    void kafka_messages_should_be_in_db(Long amount) throws InterruptedException {
        // When
        bdKafkaHelper.produceKafkaMessagesForDb(amount);

        TimeUnit.SECONDS.sleep(2);

        // Then
        assertEquals(amount, kafkaLogRepository.count(), "All messages from Kafka should be in DB");
    }


}