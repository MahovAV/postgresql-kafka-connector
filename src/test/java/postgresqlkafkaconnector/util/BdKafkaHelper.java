package postgresqlkafkaconnector.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;
import postgresqlkafkaconnector.model.MessageToKafka;
import postgresqlkafkaconnector.repository.MessageToKafkaRepository;

import java.util.Properties;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BdKafkaHelper {

    private final MessageToKafkaRepository messageToKafkaRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapUrl;
    @Value("${topics.kafka-to-postgres}")
    private String kafkaToPostgresqlTopic;

    public ConsumerRecords<Object, Object> readAllRecordsFromBeginning(String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapUrl);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());//random group id to read from beginning
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<Object, Object> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Set.of(topic));

        return KafkaTestUtils.getRecords(consumer, 20000);
    }

    public void produceDbRecordsForKafka(Long amount) {
        var id = 1;
        while (amount >= id) {
            messageToKafkaRepository.save(MessageToKafka.builder()
                    .data(String.valueOf(id))
                    .topicToWrite("postgres-to-kafka")
                    .build());
            id++;
        }
    }

    public void produceKafkaMessagesForDb(Long amount) {
        var id = 1;
        while (amount >= id) {
            kafkaTemplate
                    .send(kafkaToPostgresqlTopic, String.valueOf(id), String.valueOf(id))
                    .addCallback(new ListenableFutureCallback<>() {
                        @Override
                        public void onSuccess(final SendResult<String, String> message) {
                            log.info("sent message= " + message + " with offset= " + message.getRecordMetadata().offset());
                        }

                        @Override
                        public void onFailure(final Throwable throwable) {
                            log.error("unable to send message= " + throwable, throwable);
                        }
                    });
            id++;
        }

    }
}
