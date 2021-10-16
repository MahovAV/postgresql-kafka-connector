package postgresqlkafkaconnector.schedulers;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import postgresqlkafkaconnector.processor.PostgresqlKafkaProcessor;
import postgresqlkafkaconnector.repository.MessageToKafkaRepository;

@Component
@EnableAsync
@RequiredArgsConstructor
public class DbListenerScheduler {
    private final MessageToKafkaRepository messageToKafkaRepository;
    private final PostgresqlKafkaProcessor postgresqlKafkaProcessor;

    @Scheduled(fixedRate = 2000)
    @Async
    public void sendDbMessageToKafka() {
        postgresqlKafkaProcessor.pushChanges(messageToKafkaRepository.getChangesFromDb());
    }

}
