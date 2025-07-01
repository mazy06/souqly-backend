package io.mazy.souqly_backend.controller;

import io.mazy.souqly_backend.entity.ProductImage;
import io.mazy.souqly_backend.repository.ProductImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/products")
public class ProductImageController {
    @Autowired
    private ProductImageRepository productImageRepository;

    @PostMapping("/upload-image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        ProductImage image = new ProductImage();
        image.setImageData(file.getBytes());
        image.setContentType(file.getContentType());
        image.setFileName(file.getOriginalFilename());
        productImageRepository.save(image);
        return ResponseEntity.ok(image.getId());
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        ProductImage image = productImageRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, image.getContentType())
            .body(image.getImageData());
    }

    @DeleteMapping("/image/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        productImageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
} 