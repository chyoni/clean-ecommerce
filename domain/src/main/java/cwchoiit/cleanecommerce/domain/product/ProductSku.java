package cwchoiit.cleanecommerce.domain.product;

import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.state;

import cwchoiit.cleanecommerce.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString(exclude = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSku extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long skuId;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    /** 예) SHIRT-RED-S, SHIRT-RED-L, ... */
    private String skuCode;

    private String options;
    private Integer price;
    private Integer stockQuantity;
    private boolean active;

    static ProductSku create(
            Product product, String skuCode, String options, int price, int stockQuantity) {
        ProductSku sku = new ProductSku();

        state(price >= 0, "가격은 0원 또는 0원보다 커야 합니다");
        state(stockQuantity >= 0, "재고 수량은 0 또는 양수여야 합니다");

        sku.product = requireNonNull(product);
        sku.skuCode = requireNonNull(skuCode);
        sku.options = options;
        sku.price = price;
        sku.stockQuantity = stockQuantity;
        sku.active = true;

        return sku;
    }

    public void changePrice(int price) {
        state(price >= 0, "가격은 0원 또는 0원보다 커야 합니다");
        this.price = price;
    }

    public void decreaseStock(int qty) {
        state(qty > 0, "차감 수량은 양수여야 합니다");
        state(this.stockQuantity >= qty, "재고 수량이 부족합니다");
        this.stockQuantity -= qty;
    }

    public void increaseStock(int qty) {
        state(qty > 0, "추가 수량은 양수여야 합니다");
        this.stockQuantity += qty;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
