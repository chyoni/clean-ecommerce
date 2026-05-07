package cwchoiit.cleanecommerce.adapter.storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.storage.image")
public record S3StorageProperties(
        @NotBlank String endpoint,
        @NotBlank String region,
        @NotBlank String bucket,
        @NotBlank String accessKey,
        @NotBlank String secretKey,
        boolean forcePathStyle,
        @NotNull Duration presignExpiry,
        @NotBlank String publicUrlBase,
        @Positive long maxUploadBytes,
        @NotEmpty List<String> allowedContentTypes) {}
