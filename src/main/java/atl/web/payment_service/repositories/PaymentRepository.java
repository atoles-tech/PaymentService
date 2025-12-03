package atl.web.payment_service.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import atl.web.payment_service.model.Payment;
import atl.web.payment_service.model.Status;

public interface PaymentRepository extends MongoRepository <Payment, String> {
    List<Payment> findByUserId(Long userId);
    Page<Payment> findByUserId(Long userId, Pageable pageable); 

    List<Payment> findByOrderId(Long orderId);
    Page<Payment> findByOrderId(Long userId, Pageable pageable);

    List<Payment> findByStatus(Status status);

    Page<Payment> findAll(Pageable pageable);

    @Aggregation(pipeline = {
        "{$match: {$and:[{timestamp: {$gte: ?0}},{timestamp: {$lte: ?1}}]}}",
        "{$group: {_id: null, totalSum:{$sum: '$payment_amount'}}}"
    })
    Double getTotalSumBetween(LocalDateTime start, LocalDateTime end);

}
