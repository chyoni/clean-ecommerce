package cwchoiit.cleanecommerce.domain.catalog.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record ProductRegisterPayload(
        @NotNull Long sellerId,
        @NotNull Long categoryId,
        @NotNull @Size(min = 1, max = 100) String productName,
        String descriptionHtml,
        ProductStatus status,
        @NotNull @Size(max = 200) String brand,
        @NotNull @Size(max = 100) String manufacturer,
        LocalDateTime salesStartDate,
        LocalDateTime salesEndDate,
        Map<String, Object> attributes,
        @NotNull @Size(min = 1) List<SkuPayload> skus,
        List<ImagePayload> images) {}
