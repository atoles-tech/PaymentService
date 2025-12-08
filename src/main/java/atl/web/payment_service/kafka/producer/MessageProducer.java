package atl.web.payment_service.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Value("${topic.payment-topic}")
    private String topic;

    public MessageProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(PaymentEvent message) {
        kafkaTemplate.send(topic, message);
    }

}
