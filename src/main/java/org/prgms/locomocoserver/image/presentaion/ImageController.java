package org.prgms.locomocoserver.image.presentaion;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.image.application.ImageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
}
