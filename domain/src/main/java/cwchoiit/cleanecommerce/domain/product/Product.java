package cwchoiit.cleanecommerce.domain.product;

import static cwchoiit.cleanecommerce.domain.product.ProductStatus.AVAILABLE;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.util.Assert.*;

import cwchoiit.cleanecommerce.domain.BaseEntity;
import cwchoiit.cleanecommerce.domain.member.Member;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @JoinColumn(name = "seller_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member seller;

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

        product.seller = requireNonNull(payload.seller());
        product.category = payload.category();
        product.productName = requireNonNull(payload.productName());
        product.productStatus = requireNonNullElse(payload.status(), AVAILABLE);
        product.brand = requireNonNull(payload.brand());
        product.manufacturer = requireNonNull(payload.manufacturer());

        validateSalesDate(payload.salesStartDate(), payload.salesEndDate());

        product.salesStartDate = requireNonNullElse(payload.salesStartDate(), LocalDateTime.now());
        product.salesEndDate = payload.salesEndDate();
        product.price = requireNonNull(payload.price());
        product.stockQuantity = requireNonNull(payload.stockQuantity());

        return product;
    }

    public void changeProductName(String productName) {
        this.productName = requireNonNull(productName);
    }

    public void changeProductStatus(ProductStatus status) {
        this.productStatus = requireNonNull(status);
    }

    public void changeSalesStartDate(LocalDateTime salesStartDate) {
        validateSalesDate(salesStartDate, this.salesEndDate);

        this.salesStartDate = requireNonNull(salesStartDate);
    }

    public void changeSalesEndDate(LocalDateTime salesEndDate) {
        validateSalesDate(this.salesStartDate, salesEndDate);

        this.salesEndDate = salesEndDate;
    }

    public void changePrice(Integer price) {
        state(price >= 0, "가격은 0원 또는 0원보다 커야 합니다");

        this.price = requireNonNull(price);
    }

    public void changeStockQuantity(Integer stockQuantity) {
        state(stockQuantity >= 0, "재고 수량은 0 또는 양수여야 합니다");

        this.stockQuantity = requireNonNull(stockQuantity);
    }

    private static void validateSalesDate(
            LocalDateTime salesStartDate, LocalDateTime salesEndDate) {
        state(
                salesEndDate == null || salesEndDate.isAfter(salesStartDate),
                "판매 종료일은 판매 시작일보다 이전일 수 없습니다");
    }
}
