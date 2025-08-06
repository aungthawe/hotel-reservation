package com.project.hotel.service;

import com.project.hotel.entity.Room;
import com.project.hotel.entity.RoomType;
import com.project.hotel.repository.ReservationRepository;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.RoomTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private  RoomImageService roomImageService;

    public List<Room> getDiscountedRooms() {
        return roomRepository.findDiscountedRooms();
    }

    public void saveRoom(Room room, MultipartFile imageFile) {


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

        try {
            String imagePath = roomImageService.saveImage(imageFile, room.getRoomNumber());
            if (imagePath != null) {
                newroom.setImagePath(imagePath);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }



        roomRepository.save(newroom);

    }

    public void updateRoom(Room updatedRoom,MultipartFile imageFile) {
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

        if (imageFile != null && !imageFile.isEmpty()) {


            try {
                roomImageService.deleteImage(existingRoom.getImagePath());
                String imagePath = roomImageService.saveImage(imageFile, updatedRoom.getRoomNumber());
                if (imagePath != null) {
                    existingRoom.setImagePath(imagePath);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            existingRoom.setImagePath(updatedRoom.getImagePath());
        }

        roomRepository.save(existingRoom);
    }

    public Room getRoomById(Long roomId){
        return roomRepository.findById(roomId).orElseThrow();
    }

    public boolean deleteRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));
        if (room != null) {
            try {
                roomImageService.deleteImage(room.getImagePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
