package com.project.hotel.controller;

import com.project.hotel.entity.*;
import com.project.hotel.security.EncryptionUtil;
import com.project.hotel.service.PaymentService;
import com.project.hotel.service.ReservationService;
import com.project.hotel.service.RoomService;
import com.project.hotel.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    ReservationService reservationService;
    @Autowired
    RoomService roomService;

    @PostMapping("/add")
    public  String addPaymentCard(@RequestParam String cardHolderName,
                                  @RequestParam String cardNumber,
                                  @RequestParam String expiryDate,
                                  @RequestParam String type,
                                  HttpSession session) {

        User user = userService.findUserByUsername((String) session.getAttribute("username199"));
        if (user == null) return "redirect:/login";


        PaymentCard card = new PaymentCard();
        card.setCardHolderName(cardHolderName);
        card.setCardNumber(cardNumber);
        card.setMaskedCardNumber(cardNumber);
        card.setExpiryDate(expiryDate);
        card.setType(type);
        card.setUser(user);

        paymentService.savePaymentCard(card);

        return "redirect:/account?section=payment";

    }
    @GetMapping("/process")
    public String processPayment(@RequestParam Long cardId,
                                 @RequestParam Long roomId,
                                 @RequestParam @DateTimeFormat LocalDate checkinDate,
                                 @RequestParam @DateTimeFormat LocalDate checkoutDate,
                                 HttpSession session, Model model, RedirectAttributes redirectAttributes){
        Room room = roomService.getRoomById(roomId);
        User user  = userService.findUserByUsername((String)session.getAttribute("username199"));
        Customer customer = userService.findCustomerByUserId(user.getId());

        PaymentCard paymentCard = paymentService.getCardByIdAndUserId(cardId,user.getId());
        if(paymentCard == null){
            redirectAttributes.addFlashAttribute("error", "Invalid payment method.");
            return "/account?section=payment";
        }

        Double totalAmount = reservationService.calculateTotalAmount(room,checkinDate,checkoutDate);

        Payment payment = paymentService.processPayment(user,paymentCard,totalAmount);

        model.addAttribute("payment", payment);
        model.addAttribute("room", room);
        model.addAttribute("user",user);
        model.addAttribute("customer",customer);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);

        return "reservation-form";
    }
}
