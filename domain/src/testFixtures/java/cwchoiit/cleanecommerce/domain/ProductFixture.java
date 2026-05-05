package cwchoiit.cleanecommerce.domain;

import cwchoiit.cleanecommerce.domain.member.Member;
import cwchoiit.cleanecommerce.domain.product.Product;
import cwchoiit.cleanecommerce.domain.product.ProductRegisterPayload;
import cwchoiit.cleanecommerce.domain.product.ProductStatus;
import java.time.LocalDateTime;

public class ProductFixture {

    public static Product register() {
        return Product.register(getProductRegisterPayload());
    }

    public static ProductRegisterPayload getProductRegisterPayload() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Member seller = MemberFixture.register(MemberFixture.getMemberRegisterPayload());
        private String category = "전자기기";
        private String productName = "아이폰17";
        private ProductStatus status = ProductStatus.AVAILABLE;
        private String brand = "apple";
        private String manufacturer = "apple";
        private LocalDateTime salesStartDate = LocalDateTime.now();
        private LocalDateTime salesEndDate = null;
        private Integer price = 1_500_000;
        private Integer stockQuantity = 20_000_000;

        public Builder seller(Member v) {
            this.seller = v;
            return this;
        }

        public Builder category(String v) {
            this.category = v;
            return this;
        }

        public Builder productName(String v) {
            this.productName = v;
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

        public Builder price(Integer v) {
            this.price = v;
            return this;
        }

        public Builder stockQuantity(Integer v) {
            this.stockQuantity = v;
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

        public ProductRegisterPayload build() {
            return new ProductRegisterPayload(
                    seller,
                    category,
                    productName,
                    status,
                    brand,
                    manufacturer,
                    salesStartDate,
                    salesEndDate,
                    price,
                    stockQuantity);
        }
    }
}
