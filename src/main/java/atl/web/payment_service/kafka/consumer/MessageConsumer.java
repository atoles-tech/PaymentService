package atl.web.payment_service.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import atl.web.payment_service.dto.PaymentDto;
import atl.web.payment_service.services.PaymentService;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class MessageConsumer {

    private PaymentService paymentService;

    @KafkaListener(topics = "${topic.order-topic}", properties = {"spring.json.value.default.type=atl.web.payment_service.dto.PaymentDto"})
    public void listenOrders(PaymentDto paymentDto){
        paymentService.createPayment(paymentDto);
    }

}
