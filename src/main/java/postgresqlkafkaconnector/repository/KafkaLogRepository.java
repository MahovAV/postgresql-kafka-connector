package postgresqlkafkaconnector.repository;

import org.springframework.data.repository.CrudRepository;
import postgresqlkafkaconnector.model.KafkaLog;

public interface KafkaLogRepository extends CrudRepository<KafkaLog, Long> {
}
