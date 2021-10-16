package postgresqlkafkaconnector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "t_kafka_log")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String data;
}
