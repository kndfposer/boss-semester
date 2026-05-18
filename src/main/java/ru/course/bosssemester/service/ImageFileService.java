package ru.course.bosssemester.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.nio.file.*;
@Service
public class ImageFileService {
    @Value("${app.images-dir:generated-images}") private String imagesDir;
    public ResponseEntity<Resource> get(String fileName) throws Exception {
        Path p = Paths.get(imagesDir).toAbsolutePath().resolve(fileName).normalize();
        Resource r = new UrlResource(p.toUri());
        String type = fileName.endsWith(".svg") ? "image/svg+xml" : "image/jpeg";
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(type)).body(r);
    }
}
