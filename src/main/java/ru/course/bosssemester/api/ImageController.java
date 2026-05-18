package ru.course.bosssemester.api;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.course.bosssemester.service.ImageFileService;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    private final ImageFileService images;

    public ImageController(ImageFileService images) {
        this.images = images;
    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> get(@PathVariable String fileName) throws Exception {
        return images.get(fileName);
    }
}