package cwchoiit.cleanecommerce.application.port.out.images;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

public record PresignedUploadDescriptor(
        URI uploadUrl, Instant expiresAt, Map<String, String> requiredHeaders) {}
