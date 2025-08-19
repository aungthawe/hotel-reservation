package com.project.hotel.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentParameters;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService(Client client){
        this.client = client;
    }

    public String generateRoomRecommendation(String userInput){
        try{
//            GenerateContentResponse response = client.models.generateContent();
                return "nothing";
        }catch (Exception e){
            throw new RuntimeException("Error While Generating Room:"+e.getMessage());
        }
    }
}
