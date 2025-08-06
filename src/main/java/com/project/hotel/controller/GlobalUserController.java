package com.project.hotel.controller;


import com.project.hotel.entity.User;
import com.project.hotel.entity.UserProfileDto;
import com.project.hotel.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalUserController {
    @Autowired
    private UserService userService;

    @ModelAttribute("currentUser")
    public UserProfileDto currentUserUpdate(HttpServletRequest request,HttpSession session){
        String username = MainController.getCookieValue(request,"username");
        if (username != null){
            User user = userService.findUserByUsername(username);
            if(user != null){
                return new UserProfileDto(user.getName(),user.getImagePath());
            }else return null;

        }else return null;
    }
}
