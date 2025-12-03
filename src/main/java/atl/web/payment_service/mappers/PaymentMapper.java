package atl.web.payment_service.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import atl.web.payment_service.dto.PaymentDto;
import atl.web.payment_service.dto.PaymentResponse;
import atl.web.payment_service.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", ignore = true)
    Payment toPayment(PaymentDto paymentDto);

    PaymentResponse toPaymentResponse(Payment payment);

    List<PaymentResponse> toPaymentResponseList(List<Payment> payments);

    default Page<PaymentResponse> toPaymentResponsePage(Page<Payment> page){
        if(page == null){
            return Page.empty();
        }
        
        List<PaymentResponse> paymentResponses = toPaymentResponseList(page.getContent());

        return new PageImpl<>(paymentResponses, page.getPageable(), page.getTotalPages());
    }

}
