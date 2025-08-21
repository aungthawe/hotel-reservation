package com.project.hotel.controller;

import com.project.hotel.entity.Customer;
import com.project.hotel.entity.PaymentCard;
import com.project.hotel.entity.Reservation;
import com.project.hotel.entity.User;
import com.project.hotel.repository.CustomerRepository;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.security.EncryptionUtil;
import com.project.hotel.service.PaymentService;
import com.project.hotel.service.ReservationService;
import com.project.hotel.service.UserService;
import com.sun.tools.javac.Main;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.http.HttpRequest;
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
    @Autowired
    private EncryptionUtil encryptionUtil;

    @GetMapping("")
    public String showAccountLayout(@RequestParam(defaultValue = "dashboard") String section, Model model, HttpSession session, HttpServletRequest request) {
        String username = MainController.getCookieValue(request, "username");

        if (username == null) {
            model.addAttribute("error", "You haven't logged in yet, Log in to manage your account");
            return "login";
        }
        User user = userService.findUserByUsername(username);
        Customer customer = userService.findCustomerByUserId(user.getId());
        String decryptedNrc = "No NRC";

        List<Reservation> reservations = reservationService.getReservationByCustomer(customer);
        Map<Long, Boolean> editableMap = new HashMap<>();
        for (Reservation res : reservations) {
            boolean editable = reservationService.canEditReservation(res);
            editableMap.put(res.getId(), editable);
        }

        try {
            if (customer.getNrc() != null && !customer.getNrc().trim().isEmpty()) {
                decryptedNrc = encryptionUtil.decrypt(customer.getNrc());
            }else decryptedNrc = "No nrc";
        } catch (Exception e) {
            model.addAttribute("error","NRC decryption fail:"+e.getMessage());
            return "error";
        }

        model.addAttribute("user", user);
        model.addAttribute("customer", customer);
        model.addAttribute("nrc",decryptedNrc);
        model.addAttribute("reservations", reservations);
        model.addAttribute("editableMap", editableMap);
        model.addAttribute("section", section);
        model.addAttribute("paymentCards", paymentService.getCardsByUserId(user.getId()));
        model.addAttribute("payments", paymentService.getPaymentsByUserId(user.getId()));

        return "account-layout";
    }

    @GetMapping("/edit")
    public String showEditForm(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String username = MainController.getCookieValue(request,"username");
        User user = new User();

        if (username != null) {
            user = userService.findUserByUsername(username);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error","You must have an account!!");
                return "redirect:/login";
            }

            Customer customer = userService.findCustomerByUserId(user.getId());
            String decryptedNrc;

            try {
                if (customer.getNrc() != null && !customer.getNrc().trim().isEmpty()) {
                    decryptedNrc = encryptionUtil.decrypt(customer.getNrc());
                }else decryptedNrc = "No nrc";
            } catch (Exception e) {
                throw new RuntimeException("NRC decryption fail:"+e.getMessage());
            }
            redirectAttributes.addFlashAttribute("user", user);
            redirectAttributes.addFlashAttribute("customer", customer);
            redirectAttributes.addFlashAttribute("nrc",decryptedNrc);

            return "redirect:/account?section=edit";
        }else {
            redirectAttributes.addFlashAttribute("error", "You must have an account!!");
            return "redirect:/login";
        }
    }

    @PostMapping("/edit")
    public String updateAccount(
            @ModelAttribute("user") User updatedUser,
            @ModelAttribute("customer") Customer updatedCustomer,
            @RequestParam("stateCode") String stateCode,
            @RequestParam("township") String township,
            @RequestParam("citizenType") String citizenType,
            @RequestParam("nrcNumber") String nrcNumber,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes, Model model
    ) {

        String nrc = stateCode + "/" + township + "(" + citizenType + ")" + nrcNumber;

       User user = userService.findUserByUsername(MainController.getCookieValue(request,"username"));
        Customer customer = userService.findCustomerByUserId(user.getId());

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());


        try {
            if (nrc !=  null && !nrc.isEmpty()) {
                customer.setNrc(encryptionUtil.encrypt(nrc));
            }
        } catch (Exception e) {
            throw new RuntimeException("NRC encryption fail:"+e.getMessage());
        }
        customer.setAddress(updatedCustomer.getAddress());


        userRepository.save(user);
        customerRepository.save(customer);

        redirectAttributes.addFlashAttribute("message", "Account updated successfully.");
        return "redirect:/account?section=personal";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam MultipartFile imageFile, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String username = MainController.getCookieValue(request, "username");
        User user = userService.findUserByUsername(username);
        try {
            if (imageFile == null || imageFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No image selected!");
                return "redirect:/account?section=personal";
            }

            userService.updateUserProfile(user.getUsername(), imageFile);
            redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
            return "redirect:/account?section=personal";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "User profile update failed: " + e.getMessage());
            return "redirect:/error";
        }
    }


    @PostMapping("/profile/delete-image/{imagePath}")
    public String deleteProfileImage(@PathVariable String imagePath,HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String username = MainController.getCookieValue(request, "username");
        User user = userService.findUserByUsername(username);
        if (user == null){
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/account?section=personal";
        }else {
            try {
                userService.deleteUserProfile(username,imagePath);
                redirectAttributes.addFlashAttribute("message", "Profile deleted successfully!");
                return "redirect:/account?section=personal";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "User profile delete failed:" + e.getMessage());
                return "redirect:/error";
            }
        }

    }
}
