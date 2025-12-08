package atl.web.payment_service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import atl.web.payment_service.client.UserServiceClient;
import atl.web.payment_service.exceptions.PaymentNotFoundException;
import atl.web.payment_service.kafka.producer.MessageProducer;
import atl.web.payment_service.mappers.PaymentMapper;
import atl.web.payment_service.repositories.PaymentRepository;
import atl.web.payment_service.services.PaymentService;
import atl.web.payment_service.services.RandomService;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTests {
    
    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private RandomService randomService;

    @Mock
    private MessageProducer messageProducer;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("Should throw exception when payment not found")
    public void shouldThrowException_WhenPaymentNotFound(){
        when(paymentRepository.findById("123")).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, ()->paymentService.getById("123"));                
    }

}
