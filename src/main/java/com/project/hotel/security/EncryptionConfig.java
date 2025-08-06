package com.project.hotel.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionConfig {

    @Value("${encryption.secret-key-128}")
    private String secretKey128;

    public String getSecretKey() {
        return secretKey128;
    }
}
