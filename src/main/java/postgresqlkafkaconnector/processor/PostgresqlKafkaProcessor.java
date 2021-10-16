package postgresqlkafkaconnector.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostgresqlKafkaProcessor {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void pushChanges(List<String> changes) {
        log.info("Processing changes from db: {}", changes);

        changes.forEach(this::processMessage);
    }

    private void processMessage(String change) {
        try {
            JsonNode changeObj = OBJECT_MAPPER.readTree(change).get("change").get(0);
            String changedTable = changeObj.get("table").asText();
            if (changedTable.equals("t_message_to_kafka")) {
                JsonNode columnValues = changeObj.get("columnvalues");
                String id = columnValues.get(0).asText();
                String data = columnValues.get(1).asText();
                String topic = columnValues.get(2).asText();

                kafkaTemplate.send(topic, id, data);
            }
        } catch (JsonProcessingException e) {
            log.error("Error while parsing change: {}", change, e);
        }
    }
}
