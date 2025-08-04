package com.project.hotel.controller;

import com.project.hotel.constants.RoleConstants;
import com.project.hotel.entity.*;
import com.project.hotel.repository.CustomerRepository;
import com.project.hotel.repository.ReportRepository;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.StaffRepository;
import com.project.hotel.service.ReservationService;
import com.project.hotel.service.RoomService;
import com.project.hotel.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/a")
public class AdminController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private StaffRepository staffRepository;


    @GetMapping("/home")
    public String getHome(HttpSession session, Model model,RedirectAttributes redirectAttributes){
        String role = (String) session.getAttribute("usercookierole");
        if (role ==  null){
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid attempt to access admin.");
            return "redirect:/error";
        }
        if(RoleConstants.MANAGER.equals(role)){
            Room roomtosave = new Room();
            roomtosave.setAvailability(true);
            List<Staff> staffs =  staffRepository.findAll();
            List<Report> reports = reportRepository.findAll();
            List<Room> allRooms = roomRepository.findAll();
            List<User> users = userService.findAllUsers();
            List<Customer> allcustomers = customerRepository.findAll();
            List<Reservation> reservations = reservationService.getAllReservations();
            LocalDate today = LocalDate.now();
            List<Room> booked = roomService.getBookedRooms(today);
            List<Room> available = roomService.getAvailableRooms(today);

            Map<Long, Boolean> editableMap = new HashMap<>();
            for (Reservation res : reservations) {
                boolean editable = reservationService.canEditReservation(res);
                editableMap.put(res.getId(), editable);
            }

            model.addAttribute("reports",reports);
            model.addAttribute("staffs",staffs);
            model.addAttribute("roomtosave",roomtosave);
            model.addAttribute("users",users);
            model.addAttribute("allRooms",allRooms);
            model.addAttribute("customers",allcustomers);
            model.addAttribute("reservations",reservations);
            model.addAttribute("editableMap",editableMap);
            model.addAttribute("bookedRooms", booked);
            model.addAttribute("availableRooms", available);
            model.addAttribute("date", today);
            model.addAttribute("searchResults",session.getAttribute("searchResults"));

            model.addAttribute("isadmin",true);

            return "/admin/adminhome";
        }else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid attempt to access admin.");
            return "redirect:/error";
        }

    }

    @GetMapping("/staff-home")
    public String getStaffHome(HttpSession session, Model model,RedirectAttributes redirectAttributes){
        String role = (String) session.getAttribute("usercookierole");
        if (role ==  null){
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid attempt to access admin.");
            return "redirect:/error";
        }
        if(RoleConstants.STAFF.equals(role)){

            List<Reservation> reservations = reservationService.getAllReservations();
            LocalDate today = LocalDate.now();
            User user = userService.findUserByUsername((String)session.getAttribute("username199"));
            Staff staff = userService.findStaffByUserId(user.getId());

            model.addAttribute("reservations",reservations);
            model.addAttribute("date", today);
            model.addAttribute("staff",staff);
            model.addAttribute("searchResults",session.getAttribute("searchResults"));

            model.addAttribute("isstaff",true);

            return "/admin/staffhome";
        }else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid attempt to access admin.");
            return "redirect:/error";
        }

    }


    @GetMapping("/reservation/cancel/{id}")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpSession session) {
        Reservation reservation = reservationService.findById(id);

        if (!reservationService.canEditReservation(reservation)) {
            redirectAttributes.addFlashAttribute("error", "Cannot cancel within 2 days of check-in.");
            return "redirect:/a/home";
        }

        reservationService.cancelReservation(id);
        redirectAttributes.addFlashAttribute("message", "Reservation cancelled.");
        return "redirect:/a/home";
    }

    @GetMapping("/reservation/edit/{id}")
    public String showEditForm(@PathVariable Long id,Model model,HttpSession session,RedirectAttributes redirectAttributes){
        Reservation reservation = reservationService.findById(id);
        if(reservation == null){
            model.addAttribute("error","Reservation not found");
            return "redirect:/a/home";
        }

        if (!reservationService.canEditReservation(reservation)) {
            model.addAttribute("error", "Cannot edit reservation within 2 days of check-in.");
            return "redirect:/account";
        }

        model.addAttribute("reservation", reservation);
        return "/admin/edit-form";

    }

    @PostMapping("/reservation/edit/{id}")
    public String editReservation(@PathVariable Long id,
                                  @RequestParam("checkinDate") LocalDate checkinDate,
                                  @RequestParam("checkoutDate") LocalDate checkoutDate,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes){
        Reservation reservation = reservationService.getReservationById(id);

        if (!reservationService.isRoomAvailableExcludingReservation(reservation.getRoom(), checkinDate, checkoutDate, reservation.getId())) {

            redirectAttributes.addFlashAttribute("error", "The selected dates are not available.");
            return "redirect:/a/reservation/edit/" + id;
        }

        reservationService.updateReservationDates(id, checkinDate, checkoutDate);
        redirectAttributes.addFlashAttribute("message", "Reservation updated successfully.");
        return "redirect:/a/home";
    }

    @PostMapping("/assignStaff")
    public String assignStaff(@RequestParam("userId") Long userId, RedirectAttributes redirectAttributes) {

        User user = userService.findUserById(userId);
        Staff staff = userService.findStaffByUserId(userId);
        if (staff == null) {
            user.setRole(RoleConstants.STAFF);
            staff = new Staff();
            staff.setHireDate(LocalDate.now());
            staff.setDepartment("none");
            staff.setUser(user);

            userService.saveUser(user);

            userService.saveStaff(staff);

            redirectAttributes.addFlashAttribute("message", "User promoted to staff.");
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found.");
        }
        return "redirect:/a/home";
    }

    @PostMapping("/submit")
    public String submitReport(@RequestParam("staffId") Long staffId,
                               @RequestParam("title") String title,
                               @RequestParam("content") String content,
                               RedirectAttributes redirectAttributes) {
        Staff staff = userService.findStaffById(staffId);
        if (staff == null) {
            redirectAttributes.addFlashAttribute("error", "Staff not found.");
            return "redirect:/a/staff-home";
        }

        Report report = new Report();
        report.setTitle(title);
        report.setContent(content);
        report.setReportDate(LocalDate.now());
        report.setStaff(staff);

        reportRepository.save(report);

        redirectAttributes.addFlashAttribute("message", "Report submitted successfully.");
        return "redirect:/a/staff-home";
    }


}
