import model.Payment;
import model.Status;
import model.User;
import repo.InMemPaymentRepository;
import repo.InMemUserRepository;
import service.BasicValidationService;
import service.PaymentService;
import service.ValidationService;

import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        // Create instances of repositories
        InMemUserRepository userRepository = new InMemUserRepository();
        InMemPaymentRepository paymentRepository = new InMemPaymentRepository();

        // Create an instance of validation service
        ValidationService validationService = new BasicValidationService();

        // Create an instance of payment service
        PaymentService paymentService = new PaymentService(userRepository, paymentRepository, validationService);

        // Create a payment
        UUID createdPaymentId = null;
        try {
            Payment payment = paymentService.createPayment(1, 100.0);
            createdPaymentId = payment.getPaymentId(); // Capture the payment ID for later use
            System.out.println("Payment created: " + payment.getPaymentId() + ", " + payment.getAmount() + ", " + payment.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating payment: " + e.getMessage());
        }

        // Edit payment message
        if (createdPaymentId != null) {
            try {
                Payment updatedPayment = paymentService.editPaymentMessage(createdPaymentId, "New message");
                System.out.println("Updated payment message: " + updatedPayment.getMessage());
            } catch (Exception e) {
                System.err.println("Error editing payment message: " + e.getMessage());
            }
        } else {
            System.err.println("Failed to create payment, cannot edit message.");
        }

        // Get all payments exceeding a certain amount
        try {
            List<Payment> payments = paymentService.getAllByAmountExceeding(50.0);
            System.out.println("Payments exceeding 50.0:");
            payments.forEach(p -> System.out.println(p.getPaymentId() + ": " + p.getAmount()));
        } catch (Exception e) {
            System.err.println("Error retrieving payments: " + e.getMessage());
        }
    }
}
