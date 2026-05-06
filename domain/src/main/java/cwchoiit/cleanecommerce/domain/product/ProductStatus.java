package cwchoiit.cleanecommerce.domain.product;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;

@Getter
public enum ProductStatus {
    DRAFT("작성중") {
        @Override
        public Set<ProductStatus> allowedNext() {
            return EnumSet.of(PENDING_REVIEW);
        }
    },
    PENDING_REVIEW("심사대기") {
        @Override
        public Set<ProductStatus> allowedNext() {
            return EnumSet.of(AVAILABLE, DRAFT);
        }
    },
    AVAILABLE("판매중") {
        @Override
        public Set<ProductStatus> allowedNext() {
            return EnumSet.of(OUT_OF_STOCK, DISCONTINUED);
        }
    },
    OUT_OF_STOCK("품절") {
        @Override
        public Set<ProductStatus> allowedNext() {
            return EnumSet.of(AVAILABLE, DISCONTINUED);
        }
    },
    DISCONTINUED("판매종료") {
        @Override
        public Set<ProductStatus> allowedNext() {
            return EnumSet.noneOf(ProductStatus.class);
        }
    };

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }

    public abstract Set<ProductStatus> allowedNext();

    public boolean canTransitionTo(ProductStatus next) {
        return allowedNext().contains(Objects.requireNonNull(next));
    }
}
