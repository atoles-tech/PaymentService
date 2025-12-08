package atl.web.payment_service.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import atl.web.payment_service.dto.PaymentResponse;
import atl.web.payment_service.services.PaymentService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class PaymentController {

    private PaymentService paymentService;

    @GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (size == 0) {
            return ResponseEntity.ok(paymentService.getAll());
        }

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        return ResponseEntity.ok(paymentService.getAll(PageRequest.of(page, size, sort)));
    }

    @GetMapping("/payments/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @paymentService.isOwnerPayment(#id, authentication.name))")
    public ResponseEntity<PaymentResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @GetMapping("/users/{userId}/payments")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @paymentService.isUser(#userId, authentication.name))") // TODO: add user
    public ResponseEntity<?> findByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (size == 0) {
            return ResponseEntity.ok(paymentService.getByUserId(userId));
        }

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        return ResponseEntity.ok(paymentService.getByUserIdPage(userId, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/orders/{orderId}/payments")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @paymentService.isOrderOwner(#orderId, authentication.name))") // TODO: add user
    public ResponseEntity<?> findByOrderId(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "0") Integer size,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (size == 0) {
            return ResponseEntity.ok(paymentService.getByOrderId(orderId));
        }

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        return ResponseEntity.ok(paymentService.getByOrderId(orderId, PageRequest.of(page, size, sort)));
    }

}
