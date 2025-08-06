package com.project.hotel.service;

import com.project.hotel.constants.PaymentConstants;
import com.project.hotel.entity.Payment;
import com.project.hotel.entity.PaymentCard;
import com.project.hotel.entity.User;
import com.project.hotel.repository.PaymentCardRepository;
import com.project.hotel.repository.PaymentRepository;
import com.project.hotel.security.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private  PaymentRepository paymentRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Autowired
    private EncryptionUtil encryptionUtil;

    //    Payment Operations
    public  Payment getPaymentById(long paymentId){
        return paymentRepository.findById(paymentId).orElseThrow();
    }
    public List<Payment> getPaymentsByUserId(long userId){
        return paymentRepository.findByUserId(userId);
    }
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentByCardId(long cardId){
        return paymentRepository.findByPaymentCardId(cardId);
    }

    public Payment processPayment(User user, PaymentCard paymentCard, Double amount){
        /*
        TODO:integrate with payment gateway , process actual transaction
        simulate success payment record creation
        */

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setPaymentCard(paymentCard);
        payment.setAmount(amount);
        payment.setStatus(PaymentConstants.SUCCESS);
        payment.setPaymentTime(LocalDate.now());

        return savePayment(payment);

    }

    //--------PaymentCard Operations----------------

    private String maskCardNumber(String cardNumber) {
        // Only for display (not encrypted): **** **** **** 1234
        if (cardNumber.length() >= 4) {
            return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        } else {
            return "****";
        }
    }

    public void savePaymentCard(PaymentCard paymentCard){
        try {
            paymentCard.setMaskedCardNumber(maskCardNumber(paymentCard.getCardNumber()));
            String encryptedCardNumber  = encryptionUtil.encrypt(paymentCard.getCardNumber());
            paymentCard.setCardNumber(encryptedCardNumber);


            paymentCardRepository.save(paymentCard);
        }catch (Exception e){
            throw new RuntimeException("Encryption Failed : ",e);
        }

    }
    public List<PaymentCard> getCardsByUserId(Long userId){
        return paymentCardRepository.findByUserId(userId);
    }
    public PaymentCard getCardByIdAndUserId(Long cardId, Long userId) {
        return paymentCardRepository.findByIdAndUserId(cardId, userId).orElseThrow();
    }
    public void deletePaymentCard(Long cardId, Long userId) {
        paymentCardRepository.findByIdAndUserId(cardId, userId).ifPresent(paymentCardRepository::delete);
    }

}
