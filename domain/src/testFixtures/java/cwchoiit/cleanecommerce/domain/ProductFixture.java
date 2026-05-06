package cwchoiit.cleanecommerce.domain;

import cwchoiit.cleanecommerce.domain.member.Member;
import cwchoiit.cleanecommerce.domain.product.Category;
import cwchoiit.cleanecommerce.domain.product.Product;
import cwchoiit.cleanecommerce.domain.product.ProductRegisterPayload;
import cwchoiit.cleanecommerce.domain.product.ProductStatus;
import java.time.LocalDateTime;

public class ProductFixture {

    public static Product register() {
        return Product.register(getProductRegisterPayload());
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
        private Member seller = MemberFixture.register(MemberFixture.getMemberRegisterPayload());
        private Category category = CategoryFixture.register();
        private String productName = "아이폰17";
        private String descriptionHtml = null;
        private ProductStatus status = ProductStatus.AVAILABLE;
        private String brand = "apple";
        private String manufacturer = "apple";
        private LocalDateTime salesStartDate = LocalDateTime.now();
        private LocalDateTime salesEndDate = null;
        private String attributes = "{ \"screen_size\": 6.2, \"storage\": 256 }";

        public Builder seller(Member v) {
            this.seller = v;
            return this;
        }

        public Builder category(Category v) {
            this.category = v;
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

        public Builder attributes(String v) {
            this.attributes = v;
            return this;
        }

        public ProductRegisterPayload build() {
            return new ProductRegisterPayload(
                    seller,
                    category,
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
