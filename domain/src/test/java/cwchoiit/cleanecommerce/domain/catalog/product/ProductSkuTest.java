package cwchoiit.cleanecommerce.domain.catalog.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cwchoiit.cleanecommerce.domain.ProductFixture;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductSkuTest {

    private ProductSku defaultSku() {
        Product product = ProductFixture.register();
        return product.registerSku("SKU-001", null, 10_000, 100);
    }

    @Test
    @DisplayName("SKU를 등록한다")
    void registerSku() {
        Product product = ProductFixture.register();

        ProductSku sku = product.registerSku("SKU-001", Map.of("color", "RED"), 10_000, 100);

        assertThat(sku.getSkuCode()).isEqualTo("SKU-001");
        assertThat(sku.getPrice()).isEqualTo(10_000);
        assertThat(sku.getStockQuantity()).isEqualTo(100);
        assertThat(sku.isActive()).isTrue();
        assertThat(product.getSkus()).hasSize(2); // fixture default SKU 1개 + 추가 1개
    }

    @Test
    @DisplayName("가격이 음수이면 SKU 등록에 실패한다")
    void registerSkuFailNegativePrice() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.registerSku("SKU-001", null, -1, 100))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("재고가 음수이면 SKU 등록에 실패한다")
    void registerSkuFailNegativeStock() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.registerSku("SKU-001", null, 10_000, -1))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("가격을 변경한다")
    void changePrice() {
        ProductSku sku = defaultSku();

        sku.changePrice(20_000);

        assertThat(sku.getPrice()).isEqualTo(20_000);
    }

    @Test
    @DisplayName("가격 변경 시 음수이면 오류가 발생한다")
    void changePriceFail() {
        ProductSku sku = defaultSku();

        assertThatThrownBy(() -> sku.changePrice(-1)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("재고를 차감한다")
    void decreaseStock() {
        ProductSku sku = defaultSku();

        sku.decreaseStock(30);

        assertThat(sku.getStockQuantity()).isEqualTo(70);
    }

    @Test
    @DisplayName("재고 차감 시 재고가 부족하면 오류가 발생한다")
    void decreaseStockFail() {
        ProductSku sku = defaultSku();

        assertThatThrownBy(() -> sku.decreaseStock(200)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("재고 차감 시 0 이하이면 오류가 발생한다")
    void decreaseStockFailZero() {
        ProductSku sku = defaultSku();

        assertThatThrownBy(() -> sku.decreaseStock(0)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("재고를 추가한다")
    void increaseStock() {
        ProductSku sku = defaultSku();

        sku.increaseStock(50);

        assertThat(sku.getStockQuantity()).isEqualTo(150);
    }

    @Test
    @DisplayName("재고 추가 시 0 이하이면 오류가 발생한다")
    void increaseStockFailZero() {
        ProductSku sku = defaultSku();

        assertThatThrownBy(() -> sku.increaseStock(0)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("SKU를 비활성화한다")
    void deactivate() {
        ProductSku sku = defaultSku();

        sku.deactivate();

        assertThat(sku.isActive()).isFalse();
    }

    @Test
    @DisplayName("SKU를 활성화한다")
    void activate() {
        ProductSku sku = defaultSku();
        sku.deactivate();

        sku.activate();

        assertThat(sku.isActive()).isTrue();
    }
}
