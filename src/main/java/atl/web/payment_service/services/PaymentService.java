package atl.web.payment_service.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import atl.web.payment_service.client.UserServiceClient;
import atl.web.payment_service.dto.PaymentDto;
import atl.web.payment_service.dto.PaymentResponse;
import atl.web.payment_service.exceptions.PaymentNotFoundException;
import atl.web.payment_service.kafka.producer.MessageProducer;
import atl.web.payment_service.kafka.producer.PaymentEvent;
import atl.web.payment_service.mappers.PaymentMapper;
import atl.web.payment_service.model.Payment;
import atl.web.payment_service.model.Status;
import atl.web.payment_service.repositories.PaymentRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private PaymentRepository paymentRepository;
    private PaymentMapper paymentMapper;

    private RandomService randomService;

    private MessageProducer messageProducer;

    private UserServiceClient userServiceClient;

    // read
    public List<PaymentResponse> getAll() {
        return paymentMapper.toPaymentResponseList(paymentRepository.findAll());
    }

    public Page<PaymentResponse> getAll(Pageable pageable) {
        return paymentMapper.toPaymentResponsePage(paymentRepository.findAll(pageable));
    }

    public PaymentResponse getById(String id) {
        return paymentMapper.toPaymentResponse(paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id)));
    }

    public List<PaymentResponse> getByUserId(Long userId) {
        return paymentMapper.toPaymentResponseList(paymentRepository.findByUserId(userId));
    }

    public Page<PaymentResponse> getByUserIdPage(Long userId, Pageable pageable) {
        return paymentMapper.toPaymentResponsePage(paymentRepository.findByUserId(userId, pageable));
    }

    public List<PaymentResponse> getByOrderId(Long orderId) {
        return paymentMapper.toPaymentResponseList(paymentRepository.findByOrderId(orderId));
    }

    public Page<PaymentResponse> getByOrderId(Long orderId, Pageable pageable) {
        return paymentMapper.toPaymentResponsePage(paymentRepository.findByOrderId(orderId, pageable));
    }

    // create
    @Transactional
    public void createPayment(PaymentDto paymentDto) {
        Payment payment = paymentMapper.toPayment(paymentDto);

        if (randomService.random() % 2 == 0) {
            payment.setStatus(Status.SUCCESS);
        } else {
            payment.setStatus(Status.FAILED);
        }

        messageProducer.sendMessage(new PaymentEvent(payment.getOrderId(), payment.getStatus().name()));
        paymentMapper.toPaymentResponse(paymentRepository.save(payment));
    }

    // util
    public Boolean isUser(Long userId, String email){
        Long currentUserId = userServiceClient.getUserByEmail(email).getId();
        return currentUserId.equals(userId);
    }

    public Boolean isOrderOwner(Long orderId, String email){
        Long currentUserId = userServiceClient.getUserByEmail(email).getId();
        
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        if(payments.isEmpty()){
            return false;
        }
        
        return payments.getFirst().getUserId().equals(currentUserId);
    }

    public Boolean isOwnerPayment(String paymentId, String email){
        Long currentUserId = userServiceClient.getUserByEmail(email).getId();
        return paymentRepository.findById(paymentId).orElseThrow(
            ()->new PaymentNotFoundException(paymentId)
        ).getUserId().equals(currentUserId);
    }

}
