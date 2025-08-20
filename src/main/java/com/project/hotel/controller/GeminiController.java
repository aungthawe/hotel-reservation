package com.project.hotel.controller;


import com.project.hotel.entity.Room;
import com.project.hotel.service.GeminiService;
import com.project.hotel.service.RoomService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Controller
@RestController("/gemini")
public class GeminiController {

    @Autowired
    private  GeminiService geminiService;
    @Autowired
    private RoomService roomService;

    @GetMapping("/ask")
    public String recommendRoom(@RequestParam String userinput, Model model){
        try {
            JSONObject json = geminiService.generateRoomRecommendation(userinput);

            String checkin = json.optString("checkinDate");
            String checkout = json.optString("checkoutDate");
            String roomType = json.optString("roomType","single");
            int capacity = json.optInt("capacity", 1);
            String textAnswer = json.optString("text","Here your favorable rooms to stay!");

            try {
                LocalDate checkinDate = LocalDate.parse(checkin, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate checkoutDate = LocalDate.parse(checkout, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                List<Room> results = roomService.findAvailableRooms(roomType,capacity,checkinDate,checkoutDate);
                model.addAttribute("geminiAnswer",textAnswer);
                model.addAttribute("geminiRooms",results);
            }catch (java.time.format.DateTimeParseException e) {
                model.addAttribute("geminiAnswer", "Sorry, there was an issue processing the dates. Please try a different date format.");
                model.addAttribute("geminiRooms", Collections.emptyList());
            }

            return "index";
        } catch (Exception e) {
            model.addAttribute("error","Sorry, We have error in generating room base on your input !!");
            return "index";
        }
    }

}
