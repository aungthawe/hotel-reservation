package com.project.hotel.controller;

import com.project.hotel.entity.Customer;
import com.project.hotel.entity.User;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            RedirectAttributes redirectAttributes,
                            Model model, HttpServletResponse rp, HttpSession session) {
        try {
            User user = userRepository.findUserByUsername(username);
            if (user == null) {
                model.addAttribute("error", "User not found!");
                return "login";
            }

            if (user.getPassword().equals(password)) {
                // Store user details in session
                session.setAttribute("username198", username);
                session.setAttribute("123phone", user.getPhone());

                // Check if customer data exists
                Customer customer = userService.findCustomerByUserId(user.getId());
                if (customer != null) {
                    session.setAttribute("123nrc", customer.getNrc());
                    session.setAttribute("123address", customer.getAddress());
                }

                Cookie userCookieName = new Cookie("name", URLEncoder.encode(user.getName(), StandardCharsets.UTF_8));
                Cookie userCookieRole = new Cookie("role", URLEncoder.encode(user.getRole(), StandardCharsets.UTF_8));
                Cookie userCookieUserName = new Cookie("username",URLEncoder.encode(user.getUsername(),StandardCharsets.UTF_8));

                userCookieName.setPath("/");
                userCookieName.setMaxAge(60 * 60 * 3);
                userCookieRole.setPath("/");
                userCookieRole.setMaxAge(60 * 60 * 3);
                userCookieUserName.setPath("/");
                userCookieUserName.setMaxAge(60*60*3);

                rp.addCookie(userCookieUserName);
                rp.addCookie(userCookieName);
                rp.addCookie(userCookieRole);

                return "redirect:/";
            } else {
                model.addAttribute("error", "Incorrect password!");
                return "login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error","Error Before Checking User.");
            return "redirect:/";
        }
    }


    @PostMapping("/register")
    public String registerUserWithCustomer(@RequestParam String name,
                                           @RequestParam String username,
                                           @RequestParam String email,
                                           @RequestParam String phone,
                                           @RequestParam String password,
                                           @RequestParam Integer age,
                                           @RequestParam String gender,
                                           @RequestParam(required = false) String nrc,
                                           @RequestParam(required = false) String address,Model model)
    {
        String role = "customer";
        try {
            userService.saveUserWithCustomer(name, username, email, phone, password, age, gender, role, nrc, address);
            model.addAttribute("message","Customer account registeration is complete!!");
            return "index";
        } catch (Exception e) {
            model.addAttribute("error",e.getMessage());
            return "index";
        }
    }
    @GetMapping("/check-username")
    @ResponseBody
    public String checkUsername(@RequestParam String username){
            boolean exists = userRepository.existsByUsername(username);
            return exists?"exists":"available";
    }

    @GetMapping("/check-email")
    @ResponseBody
    public String checkEmail(@RequestParam String email){
        boolean exists = userRepository.existsByEmail(email);
        return exists?"exists":"available";
    }
    @GetMapping("/{username}/home")
    public String getHomepage(Model model){
        model.addAttribute("message","Hello my fucking user");
        return "index";
    }
}
