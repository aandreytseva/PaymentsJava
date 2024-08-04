package repo;

import model.Payment;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class InMemPaymentRepository implements PaymentRepository{
    private final Map<UUID, Payment> paymentMap;

    public InMemPaymentRepository() {
        paymentMap = new HashMap<>();
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        if (isNull(paymentId)) {
            throw new IllegalArgumentException("Payment id must not be null");
        }
        return ofNullable(paymentMap.get(paymentId))
                .map(Payment::copyOf);
    }

    @Override
    public List<Payment> findAll() {
        return paymentMap.values()
                .stream()
                .map(Payment::copyOf)
                .collect(toList());
    }

    @Override
    public Payment save(Payment payment) {
        if (isNull(payment)) {
            throw new IllegalArgumentException("Payment must not be null");
        }

        if (nonNull(payment.getPaymentId()) && findById(payment.getPaymentId()).isPresent()) {
            throw new IllegalArgumentException("Payment with id " + payment.getPaymentId() + "already saved");
        }

        paymentMap.put(payment.getPaymentId(), Payment.copyOf(payment));

        return payment;
    }

    @Override
    public Payment editMessage(UUID paymentId, String message) {
        final Payment payment = paymentMap.get(paymentId);
        if (isNull(payment)) {
            throw new NoSuchElementException("Payment with id " + paymentId + " not found");
        }

        payment.setMessage(message);
        return Payment.copyOf(payment);
    }
}
