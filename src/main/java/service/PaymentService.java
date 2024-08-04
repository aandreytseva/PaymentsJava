package service;

import model.Payment;
import model.User;
import repo.PaymentRepository;
import repo.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaymentService {
    private UserRepository userRepository;
    private PaymentRepository paymentRepository;
    private ValidationService validationService;

    public PaymentService(UserRepository userRepository, PaymentRepository paymentRepository,
                          ValidationService validationService) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.validationService = validationService;
    }

    public Payment createPayment(Integer userId, Double amount) {
        validationService.validateUserId(userId);
        validationService.validateAmount(amount);

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        validationService.validateUser(user);

        final String paymentMessage = "Payment from user " + user.getName();
        final Payment payment = new Payment(user.getId(), amount, paymentMessage);
        return paymentRepository.save(payment);
    }

    public Payment editPaymentMessage(UUID paymentId, String newMessage) {
        validationService.validatePaymentId(paymentId);
        validationService.validateMessage(newMessage);

        return paymentRepository.editMessage(paymentId, newMessage);
    }

    public List<Payment> getAllByAmountExceeding(Double amount) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getAmount() > amount)
                .collect(Collectors.toList());
    }
}
