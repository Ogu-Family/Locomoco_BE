package org.prgms.locomocoserver.image.dto;

import org.prgms.locomocoserver.image.domain.Image;

public record ImageDto(
        Long imageId,
        String path
) {
    public static ImageDto of(Image image) {
        if (image == null) return null;
        return new ImageDto(image.getId(), image.getPath());
    }
}
