package com.project.hotel.security;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionConfig {

    @Value("${encryption.secret-key-128}")
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }
}
