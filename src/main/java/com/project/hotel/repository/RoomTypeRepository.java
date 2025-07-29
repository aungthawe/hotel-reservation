package com.project.hotel.repository;

import com.project.hotel.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    RoomType findByTypeName(String typeName);
}


