package postgresqlkafkaconnector.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import postgresqlkafkaconnector.model.KafkaLog;
import postgresqlkafkaconnector.repository.KafkaLogRepository;

@Component
@KafkaListener(id = "db_writers", topics = "${topics.kafka-to-postgres}")
@Slf4j
@RequiredArgsConstructor
public class SpringKafkaDbWriterListener {
    private final KafkaLogRepository kafkaLogRepository;

    @KafkaHandler
    public void writeToDb(String message) {
        log.info("Writing to DB new message from kafka: {}", message);

        kafkaLogRepository.save(KafkaLog.builder()
                .data(message)
                .build()
        );
    }
}
