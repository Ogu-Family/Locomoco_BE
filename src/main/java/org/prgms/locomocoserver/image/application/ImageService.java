package org.prgms.locomocoserver.image.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.prgms.locomocoserver.image.domain.Image;
import org.prgms.locomocoserver.image.domain.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final AmazonS3 amazonS3;
    private final String dirName = "upload/";
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Image upload(MultipartFile multipartFile) throws IOException {
        File file = convertMultipartFileToFile(multipartFile);

        return upload(file, dirName + LocalDate.now().getYear());
    }

    private Image upload(File file, String dirName) {
        String key = randomFileName(file, dirName);
        String path = putS3(file, key);
        removeFile(file);

        return imageRepository.save(Image
                .builder()
                .key(key)
                .path(path)
                .build());
    }

    private String randomFileName(File file, String dirName) {
        return dirName + "/" + file.getName();
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return getS3(bucket, fileName);
    }

    private String getS3(String bucket, String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeFile(File file) {
        file.delete();
    }

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        // 확장자 추출
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex != -1) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // 임시 파일 생성
        String randomFilename = UUID.randomUUID().toString(); // 확장자를 포함하지 않은 임시 파일 이름
        File file = File.createTempFile(randomFilename, extension);

        // MultipartFile의 데이터를 생성한 임시 파일에 복사
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        }

        // 임시 파일 반환
        return file;
    }

    public void remove(Image image) {
        if (!amazonS3.doesObjectExist(bucket, image.getKey())) {
            throw new AmazonS3Exception("Object " + image.getKey() + " does not exist!");
        }
        amazonS3.deleteObject(bucket, image.getKey());
    }
}
