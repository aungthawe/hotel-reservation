package com.project.hotel.controller;

import com.project.hotel.constants.RoleConstants;
import com.project.hotel.entity.Customer;
import com.project.hotel.entity.Review;
import com.project.hotel.entity.User;
import com.project.hotel.repository.ReviewRepository;
import com.project.hotel.repository.RoomRepository;
import com.project.hotel.repository.RoomTypeRepository;
import com.project.hotel.service.RoomService;
import com.project.hotel.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    private String getCookieValue(HttpServletRequest request, String key) {
        if (request.getCookies() == null) return "";
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }
        return "";
    }

    @GetMapping("")
    public String getIndex(HttpSession session, HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String name = getCookieValue(request, "name");
        String role = getCookieValue(request, "role");
        String username = getCookieValue(request,"username");

        session.setAttribute("discountedRooms", roomService.getDiscountedRooms());
        session.setAttribute("roomTypes", roomTypeRepository.findAll());
        session.setAttribute("reviews", reviewRepository.findAll());

        //is manager??
        if (!name.isEmpty() && !role.isEmpty()) {

            User user = userService.findUserByUsername(username);

            // Check if customer data exists
            Customer customer = userService.findCustomerByUserId(user.getId());
            if (customer != null) {
                session.setAttribute("123nrc", customer.getNrc());
                session.setAttribute("123address", customer.getAddress());
            }
            // Store user details in session
            session.setAttribute("123phone", user.getPhone());
            session.setAttribute("username199", username);
            session.setAttribute("nameofuser",user.getName());
            session.setAttribute("usercookierole", role);
            model.addAttribute("scrollTo",redirectAttributes.getAttribute("scrollTo"));

            if (RoleConstants.MANAGER.equals(role)) {

                return "redirect:/a/home";
            }

            if (RoleConstants.STAFF.equals(role)){
                return "redirect:/a/staff-home";
            }
        }

        session.removeAttribute("searchResults");
        return "index";
    }

    @GetMapping("/facilities")
    public String getFacilitiesPage() {
        return "facilities";
    }

    @GetMapping("/room")
    public String getRoomPage() {
        return "room";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        HttpSession session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        return "redirect:/?logout=success";
    }

    @GetMapping("/contact")
    public String getContactPage() {
        return "contact";
    }

    @GetMapping("/terms")
    public String getTermsPage(){
        return "terms";
    }
    @DeleteMapping("/reviews/delete/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable Long id) {
        try {
            reviewRepository.deleteById(id);
            return ResponseEntity.ok("Review deleted successfully!!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting review");
        }
    }

    @PostMapping("/review/submit")
    public String submitReview(@RequestParam("stars") Integer stars,
                               @RequestParam("content") String content,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        User user = userService.findUserByUsername((String)session.getAttribute("username199"));
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to submit a review.");
            return "redirect:/login";
        }

        Review review = new Review();
        review.setUser(user);
        review.setStars(stars);
        review.setContent(content);
        review.setReviewDate(LocalDate.now());

        reviewRepository.save(review);

        redirectAttributes.addFlashAttribute("message", "Review submitted successfully!");
        return "redirect:/";
    }
}
