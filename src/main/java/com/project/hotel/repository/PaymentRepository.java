package com.project.hotel.repository;

import com.project.hotel.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    List<Payment> findByPaymentCardId(Long cardId);

    List<Payment> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.status = 'SUCCESS'")
    List<Payment> findSuccessfulPaymentsByUserId(@Param("userId") Long userId);

}

