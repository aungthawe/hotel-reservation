package com.project.hotel.repository;

import com.project.hotel.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User findUserByEmail(String email);

}
