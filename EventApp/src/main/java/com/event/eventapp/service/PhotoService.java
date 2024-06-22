package com.event.eventapp.service;

import com.event.eventapp.model.Photo;
import com.event.eventapp.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class PhotoService {
    @Value("${file.upload-dir}")
    String uploadDir;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        try {
            fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(fileStorageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Nu se poate iniţializa folderul pentru încărcarea elementelor.", e);
        }
    }

    @Transactional
    public Set<Photo> storeFiles(MultipartFile[] files, Product product) {
        Set<Photo> storedFiles = new HashSet<>();
        for (MultipartFile file : files) {
            Photo photo = storeFile(file);
            photo.setProduct(product);
            storedFiles.add(photo);
        }
        return storedFiles;
    }

    private Photo storeFile(MultipartFile file) {
        String fileName = null;
        try {
            fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String fileUploaded = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return new Photo(fileName, fileUploaded);
        } catch (IOException e) {
            throw new RuntimeException("Eroare la stocarea fişierului " + fileName, e);
        }
    }
}
