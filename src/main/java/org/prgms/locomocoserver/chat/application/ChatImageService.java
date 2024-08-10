package org.prgms.locomocoserver.chat.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.prgms.locomocoserver.chat.dto.request.ChatMessageRequestDto;
import org.prgms.locomocoserver.image.application.ImageService;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.image.exception.ImageErrorType;
import org.prgms.locomocoserver.image.exception.ImageException;
import org.prgms.locomocoserver.user.application.UserService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatImageService {

    private static final byte[] JPEG_HEADER = new byte[]{(byte) 0xFF, (byte) 0xD8};
    private static final byte[] PNG_HEADER = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47};
    private static final byte[] HEIC_HEADER = new byte[]{'f', 't', 'y', 'p'};

    private final ImageService imageService;

    public List<String> create(ChatMessageRequestDto requestDto) {
        List<String> imageByteCodeList = requestDto.imageByteCode();

        List<String> imageUrls = new ArrayList<>();
        for (String imageByteCode : imageByteCodeList) {
            Image image = save(requestDto.chatRoomId(), imageByteCode);
            imageUrls.add(image.getPath());
        }

        return imageUrls;
    }

    private Image save(Long roomId, String imageCode) {
        try {
            // Base64 인코딩된 이미지 데이터에서 바이트 배열로 디코딩
            byte[] imageBytes = decodeBase64StringToByteArray(imageCode);

            // 임시 파일 생성
            File tempFile = createTempImageFile(imageBytes);

            // 이미지 서비스를 통해 업로드
            Image image = imageService.upload(tempFile, "upload/" + roomId);

            // 임시 파일 삭제
            tempFile.delete();

            return image;
        } catch (IOException ex) {
            log.error("IOException Error Message : {}", ex.getMessage());
            ex.printStackTrace();
            throw new ImageException(ImageErrorType.FILE_WRITE_ERROR);
        }
    }

    private byte[] decodeBase64StringToByteArray(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    private File createTempImageFile(byte[] imageBytes) throws IOException {
        String extension = getImageExtension(imageBytes);
        File tempFile = File.createTempFile("image", "." + extension);
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(imageBytes);
        }
        return tempFile;
    }

    public String getImageExtension(byte[] imageBytes) {
        if (imageBytes.length > 12) {
            if (startsWithHeader(imageBytes, JPEG_HEADER)) {
                return "jpeg";
            } else if (startsWithHeader(imageBytes, PNG_HEADER)) {
                return "png";
            } else if (isHeicFormat(imageBytes)) {
                return "heic";
            }
        }
        throw new ImageException(ImageErrorType.IMAGE_FORMAT_NOTFOUND);
    }

    private boolean startsWithHeader(byte[] data, byte[] header) {
        if (data.length < header.length) {
            return false;
        }

        for (int i = 0; i < header.length; i++) {
            if (data[i] != header[i]) {
                return false;
            }
        }

        return true;
    }

    private boolean isHeicFormat(byte[] data) {
        // HEIC 파일의 헤더에서 4-7번째 바이트에 'ftyp' 문자열이 있어야 함
        return startsWithHeader(data, HEIC_HEADER) &&
                (data[8] == 'h' && data[9] == 'e' && data[10] == 'i' && (data[11] == 'c' || data[11] == 'x'));
    }
}
