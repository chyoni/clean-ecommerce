package cwchoiit.cleanecommerce.domain.product;

import java.time.LocalDateTime;

public record ProductRegisterPayload(
        Long sellerId,
        String category,
        String productName,
        ProductStatus status,
        String brand,
        String manufacturer,
        LocalDateTime salesStartDate,
        LocalDateTime salesEndDate,
        Integer price,
        Integer stockQuantity) {}
