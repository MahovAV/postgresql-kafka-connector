package postgresqlkafkaconnector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "t_message_to_kafka")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageToKafka {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String topicToWrite;
    @Column
    private String data;
}
