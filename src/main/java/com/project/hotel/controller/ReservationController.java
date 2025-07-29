package com.project.hotel.controller;

import com.project.hotel.entity.*;
import com.project.hotel.repository.CustomerRepository;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.service.PaymentService;
import com.project.hotel.service.ReservationService;
import com.project.hotel.service.RoomService;
import com.project.hotel.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/reservation")
public class ReservationController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/form")
    public String showReservationForm(@RequestParam long roomId,
                                      @RequestParam String roomNumber,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkinDate,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkoutDate,
                                      Model model,
                                      HttpSession session) {
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            model.addAttribute("error", "Selected room does not exist.");
            return "index";
        }

        String username = (String) session.getAttribute("username199");
        if (username == null) {
            return "redirect:/login";
        }

        User user = userService.findUserByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }

        Customer customer = userService.findCustomerByUserId(user.getId());
        List<PaymentCard> paymentCards = paymentService.getCardsByUserId(user.getId());
        if(paymentCards == null){
            model.addAttribute("message","You must have a payment card to proceed reservation");
            return "redirect:/account?section=payment";
        }
        if (customer == null) {
            customer = new Customer(); // create empty customer
        }

        model.addAttribute("customer", customer);
        model.addAttribute("nrc", customer.getNrc() != null ? customer.getNrc() : "");
        model.addAttribute("address", customer.getAddress() != null ? customer.getAddress() : "");

        model.addAttribute("room", room);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);
        model.addAttribute("user",user);
        model.addAttribute("paymentCards",paymentCards);

//        if (customer != null) {
//            if(customer.getNrc() != null && customer.getAddress() != null){
//                model.addAttribute("customer",customer);
//            }
//            model.addAttribute("nrc", customer.getNrc() != null ? customer.getNrc() : "");
//            model.addAttribute("address", customer.getAddress() != null ? customer.getAddress() : "");
//        } else {
//            Customer newCustomer = new Customer();
//            model.addAttribute("customer", newCustomer);
//        }

        return "reservation-form";
    }

    @PostMapping("/confirm")
    public String confirmReservation(@RequestParam long roomId,
                                     @RequestParam @DateTimeFormat LocalDate checkinDate,
                                     @RequestParam @DateTimeFormat LocalDate checkoutDate,
                                     @RequestParam String nrc,
                                     @RequestParam String address,
                                     @RequestParam long paymentId,
                                     HttpSession session,
                                     Model model) {

        String username = (String) session.getAttribute("username199");
        if (username == null) {
            model.addAttribute("error", "Please login first to make reservations.");
            return "redirect:/users/login";
        }

        User user = userService.findUserByUsername(username);
        if (user == null) {
            model.addAttribute("error", "User not found.");
            return "reservation-form";
        }

        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            model.addAttribute("error", "Room not found.");
            return "reservation-form";
        }

        try {
            Customer customer = userService.findCustomerByUserId(user.getId());

            if (customer == null) {
                // create new customer
                customer = new Customer();
                customer.setUser(user);
                customer.setNrc(nrc);
                customer.setAddress(address);
                userService.saveCustomer(customer);

                // update session attrs
                session.setAttribute("123nrc", nrc);
                session.setAttribute("123address", address);
            } else {
                // update customer if info changed
                boolean changed = false;
                if (nrc != null && !nrc.equals(customer.getNrc())) {
                    customer.setNrc(nrc);
                    changed = true;
                }
                if (address != null && !address.equals(customer.getAddress())) {
                    customer.setAddress(address);
                    changed = true;
                }
                if (changed) {
                    userService.saveCustomer(customer);
                    session.setAttribute("123nrc", nrc);
                    session.setAttribute("123address", address);
                }
            }

            Payment payment = paymentService.getPaymentById(paymentId);
            LocalDate reservationDate = LocalDate.now();

            //Final Stage: Saving Reservation in Database
            Reservation reservation = reservationService.saveReservation(user, room, checkinDate, checkoutDate, reservationDate, nrc, address,payment);

            model.addAttribute("reservation", reservation);
            return "reservation-confirmation";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "reservation-form";
        }
    }

    @GetMapping("/check-availability")
    public String checkRoomAvailability(@RequestParam Long roomId,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkinDate,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkoutDate,
                                        RedirectAttributes redirectAttributes){
        if(checkoutDate.isBefore(checkinDate) || checkinDate.isBefore(LocalDate.now())){
            redirectAttributes.addFlashAttribute("inform","Please,choose correct dates.");
            return "redirect:/reservation/date-input?roomId="+roomId;
        }
        if(!reservationService.isRoomAvailable(roomId,checkinDate,checkoutDate)){
            redirectAttributes.addFlashAttribute("inform","This room is not available for the selected dates.Choose other room.");
            return "redirect:/reservation/date-input?roomId="+roomId;
        }
        Room room  = roomService.getRoomById(roomId);

        return "redirect:/reservation/form?roomId="+roomId+
                "&roomNumber="+room.getRoomNumber()+
                "&checkinDate="+checkinDate+
                "&checkoutDate="+checkoutDate;
    }

    @GetMapping("/date-input")
    public String showDateInputForm(@RequestParam("roomId") long roomId,Model model){
        Room room  = roomService.getRoomById(roomId);
        if(room ==null){
            model.addAttribute("error","That Offered Room is not Available,Choose another room.");
            return "redirect:/";
        }
        YearMonth currentMonth = YearMonth.now();
        List<LocalDate> availableDates = reservationService.getAvailableDatesForRoomInMonth(roomId,currentMonth);
        model.addAttribute("availableDates",availableDates);
        if (!availableDates.isEmpty()){
            model.addAttribute("availableMessage","Room is available on the following dates this month:");
        }else model.addAttribute("availableMessage","Sorry, Your desired room is not available in this month,But you make reservation for next month");
        model.addAttribute("room",room);
        return "date-input";
    }

    @GetMapping("/cancel/{id}")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes,HttpSession session) {
        Reservation reservation = reservationService.findById(id);

        User user = userService.findUserByUsername((String) session.getAttribute("username199"));
        Customer customer = userService.findCustomerByUserId(user.getId());



        if (!reservation.getCustomer().getId().equals(customer.getId())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/account";
        }

        if (!reservationService.canEditReservation(reservation)) {
            redirectAttributes.addFlashAttribute("error", "Cannot cancel within 2 days of check-in.");
            return "redirect:/account";
        }

        reservationService.cancelReservation(id);
        redirectAttributes.addFlashAttribute("message", "Reservation cancelled.");
        return "redirect:/account?section=history";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id,Model model,HttpSession session,RedirectAttributes redirectAttributes){
        Reservation reservation = reservationService.findById(id);
        if(reservation == null){
            model.addAttribute("error","Reservation not found");
            return "redirect:/account";
        }

        User user = userService.findUserByUsername((String) session.getAttribute("username199"));
        Customer customer = userService.findCustomerByUserId(user.getId());

        if (!reservation.getCustomer().getId().equals(customer.getId())) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/account";
        }

        if (!reservationService.canEditReservation(reservation)) {
            model.addAttribute("error", "Cannot edit reservation within 2 days of check-in.");
            return "redirect:/account";
        }

        model.addAttribute("reservation", reservation);
        return "edit-form";

    }

    @PostMapping("/edit/{id}")
    public String editReservation(@PathVariable Long id,
                                  @RequestParam("checkinDate") LocalDate checkinDate,
                                  @RequestParam("checkoutDate") LocalDate checkoutDate,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes){
        Reservation reservation = reservationService.getReservationById(id);

        if (!reservationService.isRoomAvailableExcludingReservation(reservation.getRoom(), checkinDate, checkoutDate, reservation.getId())) {

            redirectAttributes.addFlashAttribute("error", "The selected dates are not available.");
            return "redirect:/reservation/edit/" + id;
        }

        reservationService.updateReservationDates(id, checkinDate, checkoutDate);
        redirectAttributes.addFlashAttribute("message", "Reservation updated successfully.");
        return "redirect:/account?section=history";
    }
}
