package com.project.hotel.repository;

import com.project.hotel.entity.Report;
import com.project.hotel.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
