package cwchoiit.cleanecommerce.application.product.images;

import java.time.Duration;
import java.util.List;

public record ImageUploadPolicy(
        long maxUploadBytes, List<String> allowedContentTypes, Duration presignExpiry) {}
