package cwchoiit.cleanecommerce.domain.product;

import cwchoiit.cleanecommerce.domain.member.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ProductRegisterPayload(
        @NotNull Member seller,
        @NotNull Category category,
        @NotNull @Size(min = 1, max = 100) String productName,
        String descriptionHtml,
        ProductStatus status,
        @NotNull @Size(max = 200) String brand,
        @NotNull @Size(max = 100) String manufacturer,
        LocalDateTime salesStartDate,
        LocalDateTime salesEndDate,
        String attributes) {}
