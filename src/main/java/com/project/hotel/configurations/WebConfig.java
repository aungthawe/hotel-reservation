package com.project.hotel.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){

        String roomUploadPath = System.getProperty("user.dir") + "/uploads/rooms/";
        registry.addResourceHandler("/images/rooms/**")
                .addResourceLocations("file:" + roomUploadPath)
                .setCachePeriod(0);

        String userUploadPath = System.getProperty("user.dir") + "/uploads/users";
        registry.addResourceHandler("/images/users/**")
                .addResourceLocations("file:" + userUploadPath)
                .setCachePeriod(0);
    }
}
