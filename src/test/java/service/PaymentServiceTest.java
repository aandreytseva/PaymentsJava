package service;

import model.Payment;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import repo.PaymentRepository;
import repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static model.Status.ACTIVE;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PaymentServiceTest {
    PaymentService pmSvs;
    PaymentRepository pmRepo;
    ValidationService validationSvs;
    UserRepository userRepo;

    @BeforeEach
    public void setUp() {
        pmRepo = mock(PaymentRepository.class);
        validationSvs = mock(ValidationService.class);
        userRepo = mock(UserRepository.class);
        pmSvs = new PaymentService(userRepo, pmRepo, validationSvs);
    }

    @AfterEach
    void check() {
        verifyNoMoreInteractions(pmRepo);
        verifyNoMoreInteractions(validationSvs);
        verifyNoMoreInteractions(userRepo);
    }

    @Test
    void createPayment() {
        //given
        Integer userid = 100;
        User user = new User(userid, "Nastiuha", ACTIVE);
        Double amount = 150d;
        String msg = "Payment from user ";

        //when
        when(userRepo.findById(userid)).thenReturn(Optional.of(user));
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        //action
        pmSvs.createPayment(userid, amount);

        //verify
        verify(validationSvs).validateUser(user);
        verify(validationSvs).validateUserId(userid);
        verify(validationSvs).validateAmount(amount);
        verify(userRepo).findById(userid);
        verify(pmRepo).save(paymentCaptor.capture());
        Payment paymentActual = paymentCaptor.getValue();

        assertThat(paymentActual).isNotNull();
        assertThat(paymentActual.getPaymentId()).isNotNull();
        assertThat(paymentActual.getUserId()).isEqualTo(userid);
        assertThat(paymentActual.getAmount()).isEqualTo(amount);
        assertThat(paymentActual.getMessage()).startsWith(msg);
    }

    @Test
    void createPaymentException() {
        //given
        Integer userid = 100;
        Double amount = 150d;

        //when
        when(userRepo.findById(userid)).thenReturn(Optional.empty());

        //action
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            pmSvs.createPayment(userid, amount);
        });

        //verify
        verify(validationSvs).validateUserId(userid);
        verify(validationSvs).validateAmount(amount);
        verify(userRepo).findById(userid);

        assertThat(exception.getMessage()).startsWith("User with id ");
    }

    @Test
    void editMessage() {
        Payment pm = new Payment(102, 150d, "Message to change");
        String newMsg = "My message";
        Payment pmReturnedByPmRepo = new Payment(102, 150d, newMsg);
        when(pmRepo.editMessage(pm.getPaymentId(), newMsg)).thenReturn(pmReturnedByPmRepo);

        assertThat(pmSvs.editPaymentMessage(pm.getPaymentId(), newMsg)).isEqualTo(pmReturnedByPmRepo);

        verify(validationSvs).validatePaymentId(pm.getPaymentId());
        verify(validationSvs).validateMessage(newMsg);
    }

    @Test
    void getAllByAmountExceeding() {
        Payment pm = new Payment(1, 2.1, "First payment");
        Payment pm1 = new Payment(1, 6.5d, "Second payment");
        Payment pm2 = new Payment(2, 2d, "Third payment");
        Payment pm3 = new Payment(2, 10.5d, "Fourth payment");
        Payment pm4 = new Payment(7, 50d, "Fith payment");

        List<Payment> pmList = new ArrayList<>();
        pmList.add(pm);
        pmList.add(pm1);
        pmList.add(pm2);
        pmList.add(pm3);
        pmList.add(pm4);
        when(pmRepo.findAll()).thenReturn(pmList);

        assertThat(pmSvs.getAllByAmountExceeding(50d).size()).isEqualTo(0);
        assertThat(pmSvs.getAllByAmountExceeding(7d).size()).isEqualTo(2);
    }
}
