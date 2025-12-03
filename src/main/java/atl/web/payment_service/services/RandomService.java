package atl.web.payment_service.services;

import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RandomService {
    
    private Random random = new Random();

    public Integer random(){
        return random.nextInt(0, 100);
    }

}
