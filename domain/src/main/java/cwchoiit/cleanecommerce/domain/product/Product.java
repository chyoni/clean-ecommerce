package cwchoiit.cleanecommerce.domain.product;

import static cwchoiit.cleanecommerce.domain.product.ProductStatus.DRAFT;
import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.util.Assert.state;

import cwchoiit.cleanecommerce.domain.BaseEntity;
import cwchoiit.cleanecommerce.domain.member.Member;
import cwchoiit.cleanecommerce.domain.product.category.Category;
import cwchoiit.cleanecommerce.domain.product.schema.ProductAttributeSchema;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    private String productName;
    private String descriptionHtml;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    private String brand;
    private String manufacturer;
    private LocalDateTime salesStartDate;
    private LocalDateTime salesEndDate;

    @Convert(converter = JsonAttributeConverter.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> attributes;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSku> skus = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();

    public static Product register(
            ProductRegisterPayload payload, Member seller, Category category) {
        Product product = new Product();

        validateSku(payload.skus());
        validateSeller(seller);
        validateSalesDate(payload.salesStartDate(), payload.salesEndDate());

        product.seller = requireNonNull(seller);
        product.category = requireNonNull(category);
        product.productName = requireNonNull(payload.productName());
        product.brand = requireNonNull(payload.brand());
        product.manufacturer = requireNonNull(payload.manufacturer());

        product.descriptionHtml = payload.descriptionHtml();
        product.salesEndDate = payload.salesEndDate();
        product.attributes = payload.attributes();

        product.salesStartDate = requireNonNullElse(payload.salesStartDate(), LocalDateTime.now());
        product.productStatus = requireNonNullElse(payload.status(), DRAFT);

        payload.skus()
                .forEach(
                        s ->
                                product.registerSku(
                                        s.skuCode(), s.options(), s.price(), s.stockQuantity()));

        if (payload.images() != null) {
            payload.images()
                    .forEach(i -> product.addImage(i.imageType(), i.imagePath(), i.displayOrder()));
        }

        return product;
    }

    private static void validateSku(List<SkuPayload> skus) {
        state(skus != null && !skus.isEmpty(), "상품은 최소 1개의 SKU를 가져야 합니다");
    }

    public ProductSku registerSku(
            String skuCode, Map<String, Object> options, int price, int stockQuantity) {
        ProductSku sku = ProductSku.create(this, skuCode, options, price, stockQuantity);
        skus.add(sku);
        return sku;
    }

    public void removeSku(ProductSku sku) {
        skus.remove(requireNonNull(sku));
    }

    public ProductImage addImage(ProductImageType type, String path, int order) {
        ProductImage image = ProductImage.create(this, type, path, order);
        images.add(image);
        return image;
    }

    public void removeImage(ProductImage image) {
        images.remove(requireNonNull(image));
    }

    public void changeProductName(String productName) {
        this.productName = requireNonNull(productName);
    }

    public void changeProductStatus(ProductStatus status) {
        requireNonNull(status);
        state(
                productStatus.canTransitionTo(status),
                "잘못된 상태 전이: " + productStatus + " -> " + status);
        this.productStatus = status;
    }

    public void changeDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }

    public void changeAttributes(Map<String, Object> attributes, ProductAttributeSchema schema) {
        if (schema != null) {
            schema.validate(attributes);
        }
        this.attributes = attributes;
    }

    public void changeSalesStartDate(LocalDateTime salesStartDate) {
        validateSalesDate(salesStartDate, this.salesEndDate);
        this.salesStartDate = requireNonNull(salesStartDate);
    }

    public void changeSalesEndDate(LocalDateTime salesEndDate) {
        validateSalesDate(this.salesStartDate, salesEndDate);
        this.salesEndDate = salesEndDate;
    }

    private static void validateSeller(Member seller) {
        state(seller.isSeller(), "상품 등록은 판매자 유형의 회원만 가능합니다");
    }

    private static void validateSalesDate(
            LocalDateTime salesStartDate, LocalDateTime salesEndDate) {
        state(
                salesEndDate == null || salesEndDate.isAfter(salesStartDate),
                "판매 종료일은 판매 시작일보다 이전일 수 없습니다");
    }
}
