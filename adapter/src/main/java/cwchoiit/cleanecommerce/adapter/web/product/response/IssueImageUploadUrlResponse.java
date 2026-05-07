package cwchoiit.cleanecommerce.adapter.web.product.response;

import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlResult;
import java.net.URI;
import java.time.Instant;
import java.util.Map;

public record IssueImageUploadUrlResponse(
        URI uploadUrl,
        String storageKey,
        String publicUrl,
        Instant expiresAt,
        Map<String, String> requiredHeaders) {

    public static IssueImageUploadUrlResponse from(IssueImageUploadUrlResult result) {
        return new IssueImageUploadUrlResponse(
                result.uploadUrl(),
                result.storageKey(),
                result.publicUrl(),
                result.expiresAt(),
                result.requiredHeaders());
    }
}
