package com.project.hotel.controller;

import com.project.hotel.entity.Customer;
import com.project.hotel.entity.PaymentCard;
import com.project.hotel.entity.Reservation;
import com.project.hotel.entity.User;
import com.project.hotel.repository.CustomerRepository;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.service.PaymentService;
import com.project.hotel.service.ReservationService;
import com.project.hotel.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/account")
public class AccountController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private PaymentService paymentService;

    @GetMapping("")
    public String showAccountLayout(@RequestParam(defaultValue = "dashboard")String section, Model model, HttpSession session){
        String username = (String) session.getAttribute("username199");
        if(username == null){
            return "redirect:/login";
        }
        User user = userService.findUserByUsername(username);
        Customer customer = userService.findCustomerByUserId(user.getId());

        List<Reservation> reservations = reservationService.getReservationByCustomer(customer);
        Map<Long, Boolean> editableMap = new HashMap<>();
        for (Reservation res : reservations) {
            boolean editable = reservationService.canEditReservation(res);
            editableMap.put(res.getId(), editable);
        }

        model.addAttribute("user",user);
        model.addAttribute("customer",customer);
        model.addAttribute("reservations",reservations);
        model.addAttribute("editableMap",editableMap);
        model.addAttribute("section",section);
        model.addAttribute("paymentCards", paymentService.getCardsByUserId(user.getId()));
        model.addAttribute("payments",paymentService.getPaymentsByUserId(user.getId()));

        return "account-layout";
    }

    @GetMapping("/edit")
    public String showEditForm(HttpSession session,Model model){
        String username =(String) session.getAttribute("username199");
        User user = userService.findUserByUsername(username);
        if(user == null) return "redirect:/login";

        Customer customer = userService.findCustomerByUserId(user.getId());

        model.addAttribute("user",user);
        model.addAttribute("customer",customer);
        return "redirect:/account?section=edit";
    }

    @PostMapping("/edit")
    public String updateAccount(
            @ModelAttribute("user") User updatedUser,
            @ModelAttribute("customer") Customer updatedCustomer,
            HttpSession session,
            RedirectAttributes redirectAttributes,Model  model
    ){
        User user = userService.findUserByUsername((String) session.getAttribute("username199"));
        Customer customer = userService.findCustomerByUserId(user.getId());

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());

        customer.setNrc(updatedCustomer.getNrc());
        customer.setAddress(updatedCustomer.getAddress());


        userRepository.save(user);
        customerRepository.save(customer);

        // Refresh session user object
        session.setAttribute("loggedInUser", user);

        redirectAttributes.addFlashAttribute("message", "Account updated successfully.");
        return "redirect:/account?section=personal";
    }
}
