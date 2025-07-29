package com.project.hotel.repository;

import com.project.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room,Long> {

//    @Query("SELECT * FROM Room where availability = 1")
//    List<Room> findAvailableRooms();
    @Query("SELECT r FROM Room r WHERE LOWER(r.roomNumber) = LOWER(:roomNumber)")
    Optional<Room> findByRoomNumber(@Param("roomNumber") String roomNumber);


    boolean existsByRoomNumber(String roomNumber);

    @Query("SELECT r FROM Room r WHERE r.roomType.id = :roomType " +
            "AND r.capacity >= :capacity " +
            "AND r.availability = true " +
            "AND NOT EXISTS (" +
            "   SELECT res FROM Reservation res WHERE res.room.id = r.id " +
            "   AND (:checkinDate BETWEEN res.checkinDate AND res.checkoutDate " +
            "   OR :checkoutDate BETWEEN res.checkinDate AND res.checkoutDate " +
            "   OR (res.checkinDate BETWEEN :checkinDate AND :checkoutDate))" +
            ")")
    List<Room> findAvailableRooms(@Param("roomType") String roomType,
                                  @Param("capacity") Integer capacity,
                                  @Param("checkinDate") LocalDate checkinDate,
                                  @Param("checkoutDate") LocalDate checkoutDate);

    @Query("SELECT r FROM Room r WHERE r.discount > 0")
    List<Room> findDiscountedRooms();
}
