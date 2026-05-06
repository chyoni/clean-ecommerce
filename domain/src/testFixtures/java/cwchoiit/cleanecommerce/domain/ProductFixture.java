package cwchoiit.cleanecommerce.domain;

import cwchoiit.cleanecommerce.domain.member.Member;
import cwchoiit.cleanecommerce.domain.product.Product;
import cwchoiit.cleanecommerce.domain.product.ProductRegisterPayload;
import cwchoiit.cleanecommerce.domain.product.ProductStatus;
import cwchoiit.cleanecommerce.domain.product.category.Category;
import java.time.LocalDateTime;
import java.util.Map;

public class ProductFixture {

    public static Product register() {
        ProductRegisterPayload payload = getProductRegisterPayload();
        Member seller = MemberFixture.register(MemberFixture.getMemberRegisterPayload());
        Category category = CategoryFixture.registerWithId(1L);
        return Product.register(payload, seller, category);
    }

    public static Product registerWithDefaultSku() {
        Product product = register();
        product.registerSku("DEFAULT-SKU", null, 1_500_000, 20_000_000);
        return product;
    }

    public static ProductRegisterPayload getProductRegisterPayload() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long sellerId = 1L;
        private Long categoryId = 1L;
        private String productName = "아이폰17";
        private String descriptionHtml = null;
        private ProductStatus status = ProductStatus.AVAILABLE;
        private String brand = "apple";
        private String manufacturer = "apple";
        private LocalDateTime salesStartDate = LocalDateTime.now();
        private LocalDateTime salesEndDate = null;
        private Map<String, Object> attributes = Map.of("screen_size", 6.2, "storage", 256);

        public Builder sellerId(Long v) {
            this.sellerId = v;
            return this;
        }

        public Builder categoryId(Long v) {
            this.categoryId = v;
            return this;
        }

        public Builder productName(String v) {
            this.productName = v;
            return this;
        }

        public Builder descriptionHtml(String v) {
            this.descriptionHtml = v;
            return this;
        }

        public Builder brand(String v) {
            this.brand = v;
            return this;
        }

        public Builder status(ProductStatus v) {
            this.status = v;
            return this;
        }

        public Builder manufacturer(String v) {
            this.manufacturer = v;
            return this;
        }

        public Builder salesStartDate(LocalDateTime v) {
            this.salesStartDate = v;
            return this;
        }

        public Builder salesEndDate(LocalDateTime v) {
            this.salesEndDate = v;
            return this;
        }

        public Builder attributes(Map<String, Object> v) {
            this.attributes = v;
            return this;
        }

        public ProductRegisterPayload build() {
            return new ProductRegisterPayload(
                    sellerId,
                    categoryId,
                    productName,
                    descriptionHtml,
                    status,
                    brand,
                    manufacturer,
                    salesStartDate,
                    salesEndDate,
                    attributes);
        }
    }
}
