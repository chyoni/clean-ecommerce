package cwchoiit.cleanecommerce.domain.product;

import jakarta.validation.constraints.NotNull;

public record ImagePayload(
        @NotNull ProductImageType imageType,
        @NotNull String imagePath,
        int displayOrder) {}
