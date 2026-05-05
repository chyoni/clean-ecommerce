package cwchoiit.cleanecommerce.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cwchoiit.cleanecommerce.domain.ProductFixture;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProductTest {

    @Test
    @DisplayName("상품을 정상 등록한다")
    void register() {
        ProductRegisterPayload payload = ProductFixture.getProductRegisterPayload();

        Product product = Product.register(payload);

        assertThat(product.getProductName()).isEqualTo(payload.productName());
    }

    @Test
    @DisplayName("상품을 등록할 때 상품 상태를 받지 않으면 기본으로 AVAILABLE 상태로 설정한다")
    void registerDefaultStatus() {
        ProductRegisterPayload payload = ProductFixture.builder().status(null).build();

        Product product = Product.register(payload);

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.AVAILABLE);
    }

    @Test
    @DisplayName("상품을 등록할 때 판매시작일을 등록하지 않으면 기본으로 현재날짜로 등록한다")
    void registerDefaultSalesStartDate() {
        ProductRegisterPayload payload = ProductFixture.builder().salesStartDate(null).build();

        Product product = Product.register(payload);

        assertThat(product.getSalesStartDate()).isNotNull();
    }

    @Test
    @DisplayName("판매 시작일을 수정한다")
    void changeSalesStartDate() {
        Product product = ProductFixture.register();

        LocalDateTime prevStartDate = product.getSalesStartDate();

        product.changeSalesStartDate(LocalDateTime.now());

        assertThat(product.getSalesStartDate().isAfter(prevStartDate)).isTrue();
    }

    @Test
    @DisplayName("판매 시작일이 판매 종료일보다 이후인 경우 오류가 발생한다")
    void changeSalesStartDateFail() {
        ProductRegisterPayload payload =
                ProductFixture.builder()
                        .salesStartDate(LocalDateTime.now())
                        .salesEndDate(LocalDateTime.now().plusDays(1))
                        .build();

        Product product = Product.register(payload);

        assertThatThrownBy(() -> product.changeSalesStartDate(LocalDateTime.now().plusDays(2)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("판매 종료일을 수정한다")
    void changeSalesEndDate() {
        Product product = ProductFixture.register();

        LocalDateTime prevEndDate = product.getSalesEndDate();

        product.changeSalesEndDate(LocalDateTime.now());

        assertThat(prevEndDate).isNotEqualTo(product.getSalesEndDate());
    }

    @Test
    @DisplayName("판매 종료일이 판매 시작일보다 이전인 경우 오류가 발생한다")
    void changeSalesEndDateFail() {
        ProductRegisterPayload payload =
                ProductFixture.builder()
                        .salesStartDate(LocalDateTime.now())
                        .salesEndDate(LocalDateTime.now().plusDays(1))
                        .build();

        Product product = Product.register(payload);

        assertThatThrownBy(() -> product.changeSalesEndDate(LocalDateTime.now().minusDays(2)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("가격을 수정한다")
    void changePrice() {
        Product product = ProductFixture.register();

        Integer prevPrice = product.getPrice();

        product.changePrice(2000);

        assertThat(prevPrice).isNotEqualTo(product.getPrice());
        assertThat(product.getPrice()).isEqualTo(2000);
    }

    @Test
    @DisplayName("가격 수정 시 음수를 입력하면 오류가 발생한다")
    void changePriceFail() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.changePrice(-100))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("가격 수정 시 NULL 값은 허용하지 않는다")
    void changePriceFailNull() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.changePrice(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("재고 수량을 수정한다")
    void changeStockQuantity() {
        Product product = ProductFixture.register();

        product.changeStockQuantity(20);

        assertThat(product.getStockQuantity()).isEqualTo(20);
    }

    @Test
    @DisplayName("재고 수량 수정 시 음수를 입력하면 오류가 발생한다")
    void changeStockQuantityFail() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.changeStockQuantity(-20))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("재고 수량 수정 시 NULL 값은 허용하지 않는다")
    void changeStockQuantityFailNull() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.changeStockQuantity(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("상품 상태를 수정한다")
    void changeStatus() {
        Product product = ProductFixture.register();

        product.changeProductStatus(ProductStatus.DISCONTINUED);

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.DISCONTINUED);
    }

    @Test
    @DisplayName("상품 상태를 수정 시 NULL을 허용하지 않는다")
    void changeStatusFail() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.changeProductStatus(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("상품 이름을 수정한다")
    void changeProductName() {
        Product product = ProductFixture.register();

        String newProductName = "새로운 상품";
        product.changeProductName(newProductName);

        assertThat(product.getProductName()).isEqualTo(newProductName);
    }

    @Test
    @DisplayName("상품 이름 수정 시 NULL을 허용하지 않는다")
    void changeProductNameFail() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.changeProductName(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest(name = "{1} 이 NULL이면 등록에 실패한다")
    @MethodSource("nullRequiredFieldPayloads")
    @DisplayName("필수 필드가 NULL이면 등록에 실패한다")
    void registerValidation(ProductRegisterPayload payload, String field) {
        assertThatThrownBy(() -> Product.register(payload))
                .isInstanceOf(NullPointerException.class);
    }

    private static Stream<Arguments> nullRequiredFieldPayloads() {
        return Stream.of(
                Arguments.of(ProductFixture.builder().sellerId(null).build(), "sellerId"),
                Arguments.of(ProductFixture.builder().productName(null).build(), "productName"),
                Arguments.of(ProductFixture.builder().brand(null).build(), "brand"),
                Arguments.of(ProductFixture.builder().manufacturer(null).build(), "manufacturer"),
                Arguments.of(ProductFixture.builder().price(null).build(), "price"),
                Arguments.of(
                        ProductFixture.builder().stockQuantity(null).build(), "stockQuantity"));
    }
}
