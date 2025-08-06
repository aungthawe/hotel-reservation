package com.project.hotel.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class UserImageService {

    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads", "users");

    public String saveImage(MultipartFile imageFile, String username) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) return null;

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image file");
        }

        String extension = StringUtils.getFilenameExtension(imageFile.getOriginalFilename());
        if (extension == null) extension = "jpg";
        String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "user_" + username + "_" + dateStr + "." + extension;

        Path target = uploadDir.resolve(fileName);
        try (InputStream in = imageFile.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileName;
    }

    public void deleteImage(String fileName) throws IOException {
        if (fileName == null) return;
        Path filePath = uploadDir.resolve(fileName);
        Files.deleteIfExists(filePath);
    }
}
