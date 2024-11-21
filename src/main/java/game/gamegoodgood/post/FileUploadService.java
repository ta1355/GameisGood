package game.gamegoodgood.post;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${upload.image.path}")
    private String uploadImagePath;

    // 이미지 저장 메서드
    public String saveImage(MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            // 원본 파일 이름을 추출
            String originalFileName = image.getOriginalFilename();

            String uuid = UUID.randomUUID().toString();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String savedFileName = uuid + extension;

            Path path = Paths.get(uploadImagePath + "/" + savedFileName);

            File directory = new File(uploadImagePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일을 지정된 경로에 저장
            Files.copy(image.getInputStream(), path);

            return "/img/" + savedFileName;
        }
        return null; // 이미지가 없는 경우 null 반환
    }
}