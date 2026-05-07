package cwchoiit.cleanecommerce.application.port.out.images;

import java.time.Duration;

public record ImageUploadRequest(
        String storageKey, String contentType, long contentLength, Duration expiresIn) {}
