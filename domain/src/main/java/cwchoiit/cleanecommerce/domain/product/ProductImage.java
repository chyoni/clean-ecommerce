package cwchoiit.cleanecommerce.domain.product;

import static java.util.Objects.requireNonNull;

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
public class ProductImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Enumerated(EnumType.STRING)
    private ProductImageType imageType;

    private String imagePath;
    private Integer displayOrder;

    static ProductImage create(Product product, ProductImageType type, String path, int order) {
        ProductImage image = new ProductImage();

        image.product = requireNonNull(product);
        image.imageType = requireNonNull(type);
        image.imagePath = requireNonNull(path);
        image.displayOrder = order;

        return image;
    }

    public void changeOrder(int order) {
        this.displayOrder = order;
    }
}
