package atl.web.payment_service;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import atl.web.payment_service.client.AuthServiceClient;
import atl.web.payment_service.client.UserServiceClient;
import atl.web.payment_service.dto.UserInfoDto;
import atl.web.payment_service.dto.ValidateTokenRequestDto;
import atl.web.payment_service.kafka.consumer.MessageConsumer;
import atl.web.payment_service.kafka.producer.MessageProducer;
import atl.web.payment_service.model.Payment;
import atl.web.payment_service.model.Status;
import atl.web.payment_service.repositories.PaymentRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentServiceIntegrationTests {
 
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoBean
    private AuthServiceClient authServiceClient;

    @MockitoBean
    private UserServiceClient userServiceClient;

    @MockitoBean
    private MessageConsumer messageConsumer;

    @MockitoBean
    private MessageProducer messageProducer;

    @Container
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
        registry.add("spring.data.mongodb.port", ()->mongoDBContainer.getMappedPort(27017));

        registry.add("spring.liquibase.enabled", () -> "false");
        registry.add("spring.cache.type", () -> "none");
        registry.add("eureka.client.enabled", () -> "false");
    }

    private void setupMockAuth(String role, String username) {
        when(authServiceClient.validateToken(any(ValidateTokenRequestDto.class))).thenReturn(true);
        when(authServiceClient.exrtactEmail(any(ValidateTokenRequestDto.class))).thenReturn(username);
        when(authServiceClient.extractRole(any(ValidateTokenRequestDto.class))).thenReturn(role);
    }

    private void setupMockUserService(String email, Long id) {
        when(userServiceClient.getUserByEmail(email)).thenReturn(
                UserInfoDto.builder()
                        .id(id)
                        .name("Test")
                        .surname("User")
                        .email(email)
                        .build());

        when(userServiceClient.getUser(id)).thenReturn(
                UserInfoDto.builder()
                        .id(id)
                        .name("Test")
                        .surname("User")
                        .email(email)
                        .build());
    }

    private String getAuthHeader() {
        return "Bearer token";
    }

    private Payment createTestPayment(){
        return paymentRepository.save(new Payment(null, 123L, 123L, Status.SUCCESS, LocalDateTime.now(), 2.0));
    }

    @AfterEach
    private void afterEach(){
        paymentRepository.deleteAll();
    }

    @Test
    public void shouldReturnForbiden_WhenUserIsNotOwnerOfPayments() throws Exception{
        String paymentId = createTestPayment().getId();
        setupMockAuth("ROLE_USER", "email.diff@gmail.com");
        setupMockUserService("email.diff@gmail.com",321L);

        mockMvc.perform(get("/api/v1/payments/"+paymentId)
            .header("Authorization", getAuthHeader())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn();
      
    }

    @Test
    public void shouldReturnPayment_WhenUserIsOwnerOfPayment() throws Exception{
        String paymentId = createTestPayment().getId();
        setupMockAuth("ROLE_USER", "email.diff@gmail.com");
        setupMockUserService("email.diff@gmail.com",123L);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/payments/"+paymentId)
            .header("Authorization", getAuthHeader())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        Payment payment = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),Payment.class);
    
        assertEquals(payment.getId(), paymentId);
    }

    @Test
    public void shouldReturnPayments_WhenUserIsOwner() throws Exception{
        String paymentId = createTestPayment().getId();
        setupMockAuth("ROLE_USER", "email.diff@gmail.com");
        setupMockUserService("email.diff@gmail.com",123L);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/orders/123/payments")
            .header("Authorization", getAuthHeader())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
        
        List<Payment> payments = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),List.class);
    
        assertEquals(1, payments.size());
    }

    @Test
    public void shouldReturnForbiden_WhenUserIsNotOwner() throws Exception{
        String paymentId = createTestPayment().getId();
        setupMockAuth("ROLE_USER", "email.diff@gmail.com");
        setupMockUserService("email.diff@gmail.com",321L);

        mockMvc.perform(get("/api/v1/orders/123/payments")
            .header("Authorization", getAuthHeader())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn();
    }

    @Test
    public void shouldReturnPayments_WhenUserIsOwnerPayments() throws Exception{
        String paymentId = createTestPayment().getId();
        setupMockAuth("ROLE_USER", "email.diff@gmail.com");
        setupMockUserService("email.diff@gmail.com",123L);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/users/123/payments")
            .header("Authorization", getAuthHeader())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        List<Payment> payments = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),List.class);
    
        assertEquals(1, payments.size());
    }

    @Test
    public void shouldReturnForbiden_WhenUserIsNotOwnerPayments() throws Exception{
        String paymentId = createTestPayment().getId();
        setupMockAuth("ROLE_USER", "email.diff@gmail.com");
        setupMockUserService("email.diff@gmail.com",321L);

        mockMvc.perform(get("/api/v1/users/123/payments")
            .header("Authorization", getAuthHeader())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden())
            .andReturn();
    }
}
