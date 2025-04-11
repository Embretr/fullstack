package stanism.marketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {
    /**
     * This is the directory where the images are stored.
     */
    private final Path uploadDir = Paths.get("uploads");

    public FileStorageConfig() {
        try {
            if (!uploadDir.toFile().exists()) {
                uploadDir.toFile().mkdirs();
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}