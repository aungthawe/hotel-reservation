package com.project.hotel.controller;

import com.project.hotel.entity.Room;
import com.project.hotel.entity.RoomType;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.RoomTypeRepository;
import com.project.hotel.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    @Autowired
    private RoomService roomService;
    @GetMapping
    public String getAllRooms(Model model){

        return "homepage";
    }

    @PostMapping("/addRoom")
    public String saveRoom(@ModelAttribute Room room, RedirectAttributes redirectAttributes){
        try {
            roomService.saveRoom(room);
            redirectAttributes.addAttribute("message","Room added success!!");
            redirectAttributes.addAttribute("scrollTo","admin-section");
        }catch (Exception e){
            redirectAttributes.addAttribute("error",": "+e.getMessage());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editRoom(@PathVariable Long id, Model model) {
        Room room = roomService.getRoomById(id);
        if (room == null) {
            model.addAttribute("error","Room Not Found!");
            return "redirect:/";
        }

        model.addAttribute("room", room);
        return "room/edit-form";
    }

    @PostMapping("/update")
    public String updateRoomByParams(
            @RequestParam("id") Long id,
            @RequestParam("roomNumber") String roomNumber,
            @RequestParam("roomType") String roomType,
            @RequestParam("price") Double price,
            @RequestParam("capacity") Integer capacity,
            @RequestParam("status") String status,
            @RequestParam("availability") boolean availability,
            @RequestParam("description") String description,
            @RequestParam("features") String features,
            @RequestParam(value = "discount", required = false ,defaultValue = "1") Integer discount,Model model
    ) {
        Room room = roomService.getRoomById(id);
        RoomType selectedType = roomTypeRepository.findByTypeName(roomType);

        room.setRoomNumber(roomNumber);
        room.setRoomType(selectedType);
        room.setPrice(price);
        room.setCapacity(capacity);
        room.setStatus(status);
        room.setAvailability(availability);
        room.setDescription(description);
        room.setFeatures(features);
        room.setDiscount(discount);

        roomService.updateRoom(room);

        model.addAttribute("message","Room Edit Success!");
        return "redirect:/";
    }


    @DeleteMapping("/deleteRoom/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        System.out.println("Deleting room with ID: " + id); // Debugging log

        boolean deleted = roomService.deleteRoomById(id);
        return deleted ? ResponseEntity.ok("success") : ResponseEntity.status(404).body("notFound");
    }

    @GetMapping("/checkRoomNumber")
    @ResponseBody
    public String checkRoomNumber(@RequestParam String roomNumber){
        boolean existroom = roomRepository.existsByRoomNumber(roomNumber);
        return existroom?"exists":"available";
    }

    @GetMapping("/search")
    public String searchRooms(@RequestParam String roomType,
                              @RequestParam LocalDate checkinDate,
                              @RequestParam LocalDate checkoutDate,
                              @RequestParam Integer capacity,
                              Model model) {

        // Validate dates
        if (checkinDate == null || checkoutDate == null ||
                checkinDate.isAfter(checkoutDate) ||
                checkinDate.isBefore(LocalDate.now())) {
            model.addAttribute("error", "Invalid check-in or check-out date. Please select valid dates.");
            return "index"; // return directly with error message
        }

        // Perform the search
        List<Room> rooms = roomService.findAvailableRooms(roomType, capacity, checkinDate, checkoutDate);

        // Just pass search-specific data via model
        model.addAttribute("searchResults", rooms);
        model.addAttribute("roomType", roomType);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);
        model.addAttribute("capacity", capacity);

        return "index";
    }

}
