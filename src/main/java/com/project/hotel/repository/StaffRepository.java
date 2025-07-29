package com.project.hotel.repository;

import com.project.hotel.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StaffRepository extends JpaRepository<Staff,Long> {
    Staff findByUserId(Long userId);
}
