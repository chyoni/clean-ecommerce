package cwchoiit.cleanecommerce.application.port.in.images;

import cwchoiit.cleanecommerce.domain.product.ProductImageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IssueImageUploadUrlCommand(
        @NotNull ProductImageType imageType,
        @NotBlank String originalFileName,
        @NotBlank String contentType,
        @Positive long contentLength) {}
