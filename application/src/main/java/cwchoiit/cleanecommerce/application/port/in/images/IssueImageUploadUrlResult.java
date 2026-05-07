package cwchoiit.cleanecommerce.application.port.in.images;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

public record IssueImageUploadUrlResult(
        URI uploadUrl,
        String storageKey,
        String publicUrl,
        Instant expiresAt,
        Map<String, String> requiredHeaders) {}
