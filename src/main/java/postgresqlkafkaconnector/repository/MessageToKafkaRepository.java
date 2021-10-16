package postgresqlkafkaconnector.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import postgresqlkafkaconnector.model.MessageToKafka;

import java.util.List;

public interface MessageToKafkaRepository extends CrudRepository<MessageToKafka, Long> {
    @Query(nativeQuery = true, value = "SELECT data FROM pg_logical_slot_get_changes('test_slot', NULL, NULL, 'pretty-print', '1')")
    List<String> getChangesFromDb();

}
