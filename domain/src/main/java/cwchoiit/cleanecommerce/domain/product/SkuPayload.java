package cwchoiit.cleanecommerce.domain.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.Map;

public record SkuPayload(
        @NotNull String skuCode,
        Map<String, Object> options,
        @PositiveOrZero int price,
        @PositiveOrZero int stockQuantity) {}
