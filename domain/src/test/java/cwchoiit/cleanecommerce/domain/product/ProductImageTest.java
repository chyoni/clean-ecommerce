package cwchoiit.cleanecommerce.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import cwchoiit.cleanecommerce.domain.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductImageTest {

    @Test
    @DisplayName("이미지를 추가한다")
    void addImage() {
        Product product = ProductFixture.register();

        ProductImage image = product.addImage(ProductImageType.THUMBNAIL, "image/thumb.jpg", 0);

        assertThat(image.getImageType()).isEqualTo(ProductImageType.THUMBNAIL);
        assertThat(image.getImagePath()).isEqualTo("image/thumb.jpg");
        assertThat(image.getDisplayOrder()).isEqualTo(0);
        assertThat(product.getImages()).hasSize(1);
    }

    @Test
    @DisplayName("이미지 표시 순서를 변경한다")
    void changeOrder() {
        Product product = ProductFixture.register();
        ProductImage image = product.addImage(ProductImageType.DETAIL, "image/detail.jpg", 0);

        image.changeOrder(3);

        assertThat(image.getDisplayOrder()).isEqualTo(3);
    }
}
