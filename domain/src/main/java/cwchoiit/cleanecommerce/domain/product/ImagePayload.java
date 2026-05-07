package cwchoiit.cleanecommerce.domain.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ImagePayload(
        @NotNull ProductImageType imageType,
        @NotBlank String imagePath,
        @NotBlank String storageKey,
        @NotBlank String mimeType,
        @Positive Long fileSize,
        int displayOrder) {}
