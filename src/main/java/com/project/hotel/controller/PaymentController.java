package com.project.hotel.controller;

import com.project.hotel.entity.*;
import com.project.hotel.security.EncryptionUtil;
import com.project.hotel.service.PaymentService;
import com.project.hotel.service.ReservationService;
import com.project.hotel.service.RoomService;
import com.project.hotel.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.List;

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
    @Autowired
    EncryptionUtil encryptionUtil;


    @PostMapping("/add")
    public  String addPaymentCard(@RequestParam String cardHolderName,
                                  @RequestParam String cardNumber,
                                  @RequestParam String expiryDate,
                                  @RequestParam String type,
                                  HttpSession session,HttpServletRequest request) {

        User user = userService.findUserByUsername(MainController.getCookieValue(request,"username"));
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
    @GetMapping("/form")
    public String getPaymentForm(@RequestParam long roomId,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkinDate,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkoutDate,
                                 Model model,RedirectAttributes redirectAttributes,
                                 HttpServletRequest request){
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            model.addAttribute("error", "Selected room does not exist.");
            return "index";
        }

        String username = MainController.getCookieValue(request,"username");
        if (username == null) {
            model.addAttribute("error", "You need to have an account");
            return "login";
        }

        User user = userService.findUserByUsername(username);
        if (user == null) {
            model.addAttribute("error", "You must log in first!");
            return "login";
        }

        Customer customer = userService.findCustomerByUserId(user.getId());

        List<PaymentCard> paymentCards = paymentService.getCardsByUserId(user.getId());
        if(paymentCards == null){
            redirectAttributes.addFlashAttribute("message","You must have a payment card to proceed reservation");
            return "redirect:/account?section=payment";
        }
        if (customer == null) {
            customer = new Customer(); // create empty customer
        }

        Double totalAmount = reservationService.calculateTotalAmount(room,checkinDate,checkoutDate);

        model.addAttribute("user",user);
        model.addAttribute("customer", customer);

        model.addAttribute("room", room);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);
        model.addAttribute("paymentCards",paymentCards);
        model.addAttribute("totalAmount",totalAmount);

        return "payment-form";

    }
    @PostMapping("/process")
    public String processPayment(@RequestParam Long cardId,
                                 @RequestParam Long roomId,
                                 @RequestParam @DateTimeFormat LocalDate checkinDate,
                                 @RequestParam @DateTimeFormat LocalDate checkoutDate,
                                 HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) throws Exception {

        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            model.addAttribute("error", "Room not found.");
            model.addAttribute("room", new Room()); // or return back with roomId?
            return "reservation-form";
        }
        model.addAttribute("room", room);

        User user  = userService.findUserByUsername(MainController.getCookieValue(request,"username"));
        Customer customer = userService.findCustomerByUserId(user.getId());

        PaymentCard paymentCard = paymentService.getCardByIdAndUserId(cardId,user.getId());
        if(paymentCard == null){
            redirectAttributes.addFlashAttribute("error", "Invalid payment method.");
            return "/account?section=payment";
        }

        Double totalAmount = reservationService.calculateTotalAmount(room,checkinDate,checkoutDate);

        Payment payment = paymentService.processPayment(user,paymentCard,totalAmount);

        model.addAttribute("payment", payment);
        model.addAttribute("user",user);
        model.addAttribute("customer",customer);

        String decryptedNrc = customer.getNrc() != null
                ? encryptionUtil.decrypt(customer.getNrc())
                : "";
        model.addAttribute("nrc", decryptedNrc);
        model.addAttribute("checkinDate", checkinDate);
        model.addAttribute("checkoutDate", checkoutDate);

        model.addAttribute("message","Your Payment process went well!!");

        return "reservation-form";
    }
}
