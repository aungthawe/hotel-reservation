package com.project.hotel.service;

import com.project.hotel.entity.Reservation;
import com.project.hotel.entity.Room;
import com.project.hotel.entity.RoomType;
import com.project.hotel.repository.ReservationRepository;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public List<Room> getDiscountedRooms() {
        return roomRepository.findDiscountedRooms();
    }

    public void saveRoom(Room room){


        Room newroom = new Room();
        newroom.setRoomNumber(room.getRoomNumber());
        RoomType roomType = roomTypeRepository.findById(room.getRoomType().getId()).orElseThrow(() -> new RuntimeException("RoomType not found"));
        newroom.setRoomType(roomType);
        newroom.setPrice((room.getPrice()));
        newroom.setCapacity(room.getCapacity());
        newroom.setAvailability(room.getAvailability());
        newroom.setStatus(room.getStatus());
        newroom.setDescription(room.getDescription());
        newroom.setFeatures(room.getFeatures());
        newroom.setDiscount(room.getDiscount());


        roomRepository.save(newroom);

    }

    public void updateRoom(Room updatedRoom) {
        Room existingRoom = roomRepository.findById(updatedRoom.getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + updatedRoom.getId()));

        existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        existingRoom.setPrice(updatedRoom.getPrice());
        existingRoom.setCapacity(updatedRoom.getCapacity());
        existingRoom.setStatus(updatedRoom.getStatus());
        existingRoom.setAvailability(updatedRoom.getAvailability());
        existingRoom.setDescription(updatedRoom.getDescription());
        existingRoom.setFeatures(updatedRoom.getFeatures());
        existingRoom.setDiscount(updatedRoom.getDiscount());

        roomRepository.save(existingRoom);
    }

    public Room getRoomById(Long roomId){
        return roomRepository.findById(roomId).orElseThrow();
    }

    public boolean deleteRoomById(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        if (room.isPresent()) {
            roomRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Room> findAvailableRooms(String roomType, Integer capacity, LocalDate checkinDate, LocalDate checkoutDate) {

        //return roomRepository.findAvailableRooms("double", 2, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 5));
        return roomRepository.findAvailableRooms(roomType, capacity, checkinDate, checkoutDate);
    }

    public  List<Room> getBookedRooms(LocalDate date){
        return reservationRepository.findBookedRoomOnDate(date);
    }

    public  List<Room> getAvailableRooms(LocalDate date){
        return reservationRepository.findAvailableRoomsOnDate(date);
    }


}
