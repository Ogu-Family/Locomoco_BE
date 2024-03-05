package org.prgms.locomocoserver.image.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.prgms.locomocoserver.image.domain.Image;

public record ImageDto(@Schema(description = "이미지 id", example = "1") Long imageId,
                       @Schema(description = "이미지 경로", example = "https://example.com/image.png") String path
) {
    public static ImageDto of(Image image) {
        if (image == null) return null;
        return new ImageDto(image.getId(), image.getPath());
    }
}
