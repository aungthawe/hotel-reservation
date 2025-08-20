package com.project.hotel.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    @Autowired
    private Client genaiClient;


    public JSONObject generateRoomRecommendation(String userInput){
        try{
            String prompt = """
            Extract the following fields from this user request and return JSON only:
            - checkinDate (YYYY-MM-DD)
            - checkoutDate (YYYY-MM-DD)
            - roomType (string)
            - capacity (integer)
            - features (array of strings)
            - text (Your short strings recommendation speak base on user input )

            User request: "%s"
            """.formatted(userInput);

            GenerateContentResponse response = genaiClient.models.generateContent("gemini-2.5-flash",prompt,null);
                return new JSONObject(response.text());
        }catch (Exception e){
            throw new RuntimeException("Error While Generating Room:"+e.getMessage());
        }
    }
}
