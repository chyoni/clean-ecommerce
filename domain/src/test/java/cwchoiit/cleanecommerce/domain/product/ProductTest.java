package cwchoiit.cleanecommerce.domain.product;

import cwchoiit.cleanecommerce.domain.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("상품을 정상 등록한다")
    void register() {
        ProductRegisterPayload payload = ProductFixture.getProductRegisterPayload();

        Product product = Product.register(payload);

        assertThat(product.getProductName()).isEqualTo(payload.productName());
    }

    @ParameterizedTest(name = "{1} 이 null이면 등록에 실패한다")
    @MethodSource("nullRequiredFieldPayloads")
    @DisplayName("필수 필드가 null이면 등록에 실패한다")
    void registerFail(ProductRegisterPayload payload, String field) {
        assertThatThrownBy(() -> Product.register(payload))
                .isInstanceOf(NullPointerException.class);
    }

    private static Stream<Arguments> nullRequiredFieldPayloads() {
        return Stream.of(
                Arguments.of(ProductFixture.builder().sellerId(null).build(),        "sellerId"),
                Arguments.of(ProductFixture.builder().productName(null).build(),     "productName"),
                Arguments.of(ProductFixture.builder().brand(null).build(),           "brand"),
                Arguments.of(ProductFixture.builder().manufacturer(null).build(),    "manufacturer"),
                Arguments.of(ProductFixture.builder().price(null).build(),           "price"),
                Arguments.of(ProductFixture.builder().stockQuantity(null).build(),   "stockQuantity")
        );
    }
}