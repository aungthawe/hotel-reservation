package com.project.hotel.repository;

import com.project.hotel.entity.Customer;
import com.project.hotel.entity.Reservation;
import com.project.hotel.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    List<Reservation> findByRoom(Room room);

    List<Reservation> findByCustomer(Customer customer);

    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId AND r.checkinDate <= :checkoutDate AND r.checkoutDate >= :checkinDate")
    List<Reservation> findByRoomIdAndDateRange(
            @Param("roomId") Long roomId,
            @Param("checkinDate") LocalDate checkinDate,
            @Param("checkoutDate") LocalDate checkoutDate
    );

    @Query("SELECT r FROM Reservation r WHERE r.customer.id = :custId AND r.checkinDate > :twoDaysLater")
    List<Reservation> findEditableReservations(@Param("custId") Long custId,@Param("twoDaysLater") LocalDate twoDaysLater);

    @Query("SELECT DISTINCT r.room FROM Reservation r WHERE :targetDate BETWEEN r.checkinDate AND r.checkoutDate")
    List<Room> findBookedRoomOnDate(@Param("targetDate") LocalDate targetDate);

    @Query("SELECT room FROM Room room WHERE room.id NOT IN (" +
            "SELECT r.room.id FROM Reservation r WHERE :targetDate BETWEEN r.checkinDate AND r.checkoutDate)")
    List<Room> findAvailableRoomsOnDate(@Param("targetDate") LocalDate targetDate);

}
