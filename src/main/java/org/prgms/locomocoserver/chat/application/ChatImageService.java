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

    private final ImageService imageService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

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
            Image image = imageService.upload(tempFile, "/upload/" + roomId);

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

    private String getImageExtension(byte[] imageBytes) {
        String[] formats = {"jpeg", "png", "jpg"};
        for (String format : formats) {
            if (startsWith(imageBytes, format)) {
                return format;
            }
        }
        return "jpg"; // 기본값으로 jpg 설정
    }

    private boolean startsWith(byte[] bytes, String prefix) {
        byte[] prefixBytes = prefix.getBytes();
        if (prefixBytes.length > bytes.length) {
            return false;
        }
        for (int i = 0; i < prefixBytes.length; i++) {
            if (bytes[i] != prefixBytes[i]) {
                return false;
            }
        }
        return true;
    }
}
