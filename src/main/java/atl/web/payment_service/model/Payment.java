package atl.web.payment_service.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "payments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Payment {

    @Id
    private String id;

    @Indexed
    @Field(name = "order_id")
    private Long orderId;

    @Indexed
    @Field(name = "user_id")
    private Long userId;

    @Field(name = "status")
    private Status status;

    @Field(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Field(name = "payment_amount")
    private Double paymentAmount;
}
