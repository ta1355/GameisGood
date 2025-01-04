package game.gamegoodgood.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebImgConfig implements WebMvcConfigurer {

    @Value("${upload.image.path}")
    private String uploadImagePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // "/img/**" URL 경로로 요청이 들어오면, 실제 경로 D:/savedb/image 에서 파일을 제공
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:///" + uploadImagePath + "/");
    }
}
