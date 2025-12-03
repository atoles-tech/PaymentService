package atl.web.payment_service.exceptions;

public class PaymentNotFoundException extends RuntimeException{

    public PaymentNotFoundException(String id){
        super("Payment with id=" + id + " not found");
    }
    
}
