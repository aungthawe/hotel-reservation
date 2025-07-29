package com.project.hotel.security;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionUtil {

    private static EncryptionConfig encryptionConfig = new EncryptionConfig();
    private static final String ALGORITHM = "AES";


    public EncryptionUtil(EncryptionConfig encryptionConfig) {
        EncryptionUtil.encryptionConfig = encryptionConfig;
    }

    public static String encrypt(String rawString) throws Exception {
        String secretKey = encryptionConfig.getSecretKey();
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encrypted = cipher.doFinal(rawString.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encryptedString) throws Exception {
        String secretKey = encryptionConfig.getSecretKey();
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
        return new String(decrypted);
    }
}
