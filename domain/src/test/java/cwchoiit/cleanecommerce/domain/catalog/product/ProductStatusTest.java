package cwchoiit.cleanecommerce.domain.catalog.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ProductStatusTest {

    @ParameterizedTest(name = "{0} -> {1} 전이는 허용된다")
    @CsvSource({
        "DRAFT, PENDING_REVIEW",
        "PENDING_REVIEW, AVAILABLE",
        "PENDING_REVIEW, DRAFT",
        "AVAILABLE, OUT_OF_STOCK",
        "AVAILABLE, DISCONTINUED",
        "OUT_OF_STOCK, AVAILABLE",
        "OUT_OF_STOCK, DISCONTINUED"
    })
    @DisplayName("허용된 상태 전이")
    void allowedTransitions(ProductStatus from, ProductStatus to) {
        assertThat(from.canTransitionTo(to)).isTrue();
    }

    @ParameterizedTest(name = "{0} -> {1} 전이는 허용되지 않는다")
    @CsvSource({
        "DRAFT, AVAILABLE",
        "DRAFT, OUT_OF_STOCK",
        "DRAFT, DISCONTINUED",
        "PENDING_REVIEW, OUT_OF_STOCK",
        "PENDING_REVIEW, DISCONTINUED",
        "AVAILABLE, DRAFT",
        "AVAILABLE, PENDING_REVIEW",
        "OUT_OF_STOCK, DRAFT",
        "OUT_OF_STOCK, PENDING_REVIEW",
        "DISCONTINUED, DRAFT",
        "DISCONTINUED, PENDING_REVIEW",
        "DISCONTINUED, AVAILABLE",
        "DISCONTINUED, OUT_OF_STOCK"
    })
    @DisplayName("허용되지 않는 상태 전이")
    void disallowedTransitions(ProductStatus from, ProductStatus to) {
        assertThat(from.canTransitionTo(to)).isFalse();
    }

    @ParameterizedTest(name = "{0} 는 자기 자신으로 전이할 수 없다")
    @CsvSource({"DRAFT", "PENDING_REVIEW", "AVAILABLE", "OUT_OF_STOCK", "DISCONTINUED"})
    @DisplayName("자기 자신으로의 전이는 불허")
    void selfTransitionDisallowed(ProductStatus status) {
        assertThat(status.canTransitionTo(status)).isFalse();
    }

    @ParameterizedTest(name = "{0}.canTransitionTo(null) 은 NPE")
    @CsvSource({"DRAFT", "AVAILABLE"})
    @DisplayName("null 전이 시 NPE")
    void nullTransitionThrows(ProductStatus status) {
        assertThatThrownBy(() -> status.canTransitionTo(null))
                .isInstanceOf(NullPointerException.class);
    }
}
