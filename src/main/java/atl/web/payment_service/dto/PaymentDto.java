package atl.web.payment_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentDto {
    @NotNull(message = "Order is required")
    private Long orderId;

    @NotNull(message = "User is required")
    private Long userId;
    
    @DecimalMin(value = "0.0", message = "Amount must be more than 0")
    private Double paymentAmount;    
}
