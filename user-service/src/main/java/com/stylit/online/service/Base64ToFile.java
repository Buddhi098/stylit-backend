package com.stylit.online.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class Base64ToFile {
    public void saveImage(String base64Image, String filePath) throws IOException {
        // Split the base64 image string by comma
        String[] stringArr = base64Image.split(",");
        String filterBase64String;
        String fileExtension = "";

        // Check if the data URL scheme prefix is present
        if (stringArr.length >= 2) {
            filterBase64String = stringArr[1];
            // Extract the extension from the prefix (e.g., "data:image/png;base64,")
            String prefix = stringArr[0];
            if (prefix.contains("image/")) {
                int start = prefix.indexOf("image/") + 6;
                int end = prefix.indexOf(";", start);
                if (end > start) {
                    fileExtension = prefix.substring(start, end);
                }
            }else if(prefix.contains("application/")){
                int start = prefix.indexOf("application/") + 12;
                int end = prefix.indexOf(";", start);
                if (end > start) {
                    fileExtension = prefix.substring(start, end);
                }
            }
        } else {
            filterBase64String = base64Image;
        }

        // Decode the base64 string to byte array
        byte[] imageBytes = Base64.getDecoder().decode(filterBase64String);

        // Define the file path with the correct extension
        if (!fileExtension.isEmpty()) {
            filePath = filePath + "." + fileExtension;
        }

        Path path = Paths.get(filePath);

        // Ensure the parent directory exists
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // Write the byte array to the file
        Files.write(path, imageBytes);
    }
}
