package com.project.hotel.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model, @ModelAttribute("errorMessage") String flashError) {
        WebRequest webRequest = new ServletWebRequest(request);

        // Get error details
        Map<String, Object> errors = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.BINDING_ERRORS));

        // Get error details from attributes (if set in controller)
        String errorMessage = (flashError != null && !flashError.isEmpty()) ? flashError : "Something went wrong";

        // Pass error details to the error page
        model.addAttribute("status", errors.getOrDefault("status", 500));
        model.addAttribute("error", errors.getOrDefault("error", "Unknown Error"));
        model.addAttribute("message", errorMessage);
        model.addAttribute("path", request.getRequestURI());

        return "error"; // Loads `error.html`
    }
}
