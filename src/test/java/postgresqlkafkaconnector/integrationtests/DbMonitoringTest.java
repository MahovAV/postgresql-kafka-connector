package postgresqlkafkaconnector.integrationtests;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import postgresqlkafkaconnector.AbstractIntegrationKafkaTest;
import postgresqlkafkaconnector.util.BdKafkaHelper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DbMonitoringTest extends AbstractIntegrationKafkaTest {
    @BeforeEach
    public void createTopic(@Value("${topics.postgres-to-kafka}") String topic) {
        var admin = AdminClient.create(Map.of(BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()));
        admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1)));
    }

    @AfterEach
    public void deleteTopic(@Value("${topics.postgres-to-kafka}") String topic) {
        var admin = AdminClient.create(Map.of(BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()));
        admin.deleteTopics(List.of(topic));
    }

    @Autowired
    private BdKafkaHelper bdKafkaHelper;

    @ParameterizedTest
    @ValueSource(longs = {2, 5, 10})
    void db_messages_should_be_in_kafka(Long amount) throws InterruptedException {
        //when
        bdKafkaHelper.produceDbRecordsForKafka(amount);

        TimeUnit.SECONDS.sleep(5);

        //then
        ConsumerRecords<Object, Object> records = bdKafkaHelper.readAllRecordsFromBeginning("postgres-to-kafka");

        assertEquals(amount, records.count(), "All messages from DB should be in Kafka");
    }
}
