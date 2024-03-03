package org.prgms.locomocoserver.image.application;

import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.image.domain.ImageRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

}
