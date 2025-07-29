package com.project.hotel.repository;

import com.project.hotel.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    List<PaymentCard> findByUserId(Long userId);

    Optional<PaymentCard> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserId(Long userId);
}

