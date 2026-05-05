package cwchoiit.cleanecommerce.domain.product;

import cwchoiit.cleanecommerce.domain.member.Member;
import java.time.LocalDateTime;

public record ProductRegisterPayload(
        Member seller,
        String category,
        String productName,
        ProductStatus status,
        String brand,
        String manufacturer,
        LocalDateTime salesStartDate,
        LocalDateTime salesEndDate,
        Integer price,
        Integer stockQuantity) {}
