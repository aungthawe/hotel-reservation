package com.project.hotel.repository;

import com.project.hotel.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Customer findByUserId(Long userId);
}
