package com.project.hotel.controller;

import com.project.hotel.entity.Room;
import com.project.hotel.entity.RoomType;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.RoomTypeRepository;
import com.project.hotel.service.RoomService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public String saveRoom(@ModelAttribute("roomtosave") Room room,
                           @RequestParam("imageFile")MultipartFile imageFile,
                           @RequestParam(name = "availability",required = false) Boolean availability,
                           RedirectAttributes redirectAttributes){
        try {

            roomService.saveRoom(room,imageFile);

            redirectAttributes.addFlashAttribute("message","Room added success!!");
            redirectAttributes.addFlashAttribute("scrollTo","admin-section");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("error","Save failed: "+e.getMessage());
            return "redirect:/error";
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
            @ModelAttribute("room") Room room,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,Model model,RedirectAttributes redirectAttributes
    ) {
        Room existingRoom = roomService.getRoomById(room.getId());
        if (existingRoom == null) {
            redirectAttributes.addFlashAttribute("error","Room Not Found");
            return "redirect:/";
        }

        roomService.updateRoom(room,imageFile);

        redirectAttributes.addFlashAttribute("message","Room Edit Success!");
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

        List<Room> rooms = roomService.findAvailableRooms(roomType, capacity, checkinDate, checkoutDate);

        model.addAttribute("searchResults", rooms);
        model.addAttribute("roomType", roomType);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);
        model.addAttribute("capacity", capacity);
        model.addAttribute("scrollTo","search-result-section");

        return "index";
    }

}
