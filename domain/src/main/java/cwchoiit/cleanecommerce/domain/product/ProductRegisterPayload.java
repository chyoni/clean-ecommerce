package cwchoiit.cleanecommerce.domain.product;

import cwchoiit.cleanecommerce.domain.member.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ProductRegisterPayload(
        @NotNull Member seller,
        @Size(max = 100) String category,
        @NotNull @Size(min = 1, max = 100) String productName,
        ProductStatus status,
        @NotNull @Size(min = 200) String brand,
        @NotNull @Size(min = 100) String manufacturer,
        LocalDateTime salesStartDate,
        LocalDateTime salesEndDate,
        @NotNull Integer price,
        @NotNull Integer stockQuantity) {}
