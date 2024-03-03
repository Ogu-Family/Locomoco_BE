package org.prgms.locomocoserver.image.presentaion;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.image.application.ImageService;
import org.prgms.locomocoserver.image.domain.Image;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/images")
    public Image upload(@RequestPart("file") MultipartFile multipartFile) throws IOException {
        return imageService.upload(multipartFile);
    }

    @DeleteMapping("/images")
    public void remove(Image image) {
        imageService.remove(image);
    }
}
