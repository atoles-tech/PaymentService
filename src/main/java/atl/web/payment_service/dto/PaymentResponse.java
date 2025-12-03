package atl.web.payment_service.dto;

import java.time.LocalDateTime;

import atl.web.payment_service.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PaymentResponse {
    private String id;
    private Long orderId;
    private Long userId;
    private Status status;
    private LocalDateTime timestamp;
    private Double paymentAmount;
}
