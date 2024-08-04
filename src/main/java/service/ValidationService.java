package service;

import model.User;

import java.util.UUID;

public interface ValidationService {
    void validateAmount(Double amount);

    void validatePaymentId(UUID paymentId);

    void validateUserId(Integer userId);

    default void validateUser(User user) {

    }

    void validateMessage(String message);
}
