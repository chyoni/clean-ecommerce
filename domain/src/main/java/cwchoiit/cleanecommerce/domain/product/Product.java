package cwchoiit.cleanecommerce.domain.product;

import cwchoiit.cleanecommerce.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import static cwchoiit.cleanecommerce.domain.product.ProductStatus.AVAILABLE;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private Long sellerId;
    private String category;
    private String productName;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private String brand;
    private String manufacturer;
    private LocalDateTime salesStartDate;
    private LocalDateTime salesEndDate;
    private Integer price;
    private Integer stockQuantity;

    public static Product register(ProductRegisterPayload payload) {
        Product product = new Product();

        product.sellerId = requireNonNull(payload.sellerId());
        product.category = payload.category();
        product.productName = requireNonNull(payload.productName());
        product.productStatus = requireNonNullElse(payload.status(), AVAILABLE);
        product.brand = requireNonNull(payload.brand());
        product.manufacturer = requireNonNull(payload.manufacturer());
        product.salesStartDate = requireNonNullElse(payload.salesStartDate(), LocalDateTime.now());
        product.salesEndDate = payload.salesEndDate();
        product.price = requireNonNull(payload.price());
        product.stockQuantity = requireNonNull(payload.stockQuantity());

        return product;
    }
}
