package cwchoiit.cleanecommerce.domain.catalog.product;

import static org.assertj.core.api.Assertions.assertThat;

import cwchoiit.cleanecommerce.domain.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductImageTest {

    @Test
    @DisplayName("이미지를 추가한다")
    void addImage() {
        Product product = ProductFixture.register();

        ProductImage image =
                product.addImage(
                        ProductImageType.THUMBNAIL,
                        "https://cdn.example.com/thumb.jpg",
                        "products/thumbnail/2026/05/uuid.jpg",
                        "image/jpeg",
                        102400L,
                        0);

        assertThat(image.getImageType()).isEqualTo(ProductImageType.THUMBNAIL);
        assertThat(image.getImagePath()).isEqualTo("https://cdn.example.com/thumb.jpg");
        assertThat(image.getStorageKey()).isEqualTo("products/thumbnail/2026/05/uuid.jpg");
        assertThat(image.getMimeType()).isEqualTo("image/jpeg");
        assertThat(image.getFileSize()).isEqualTo(102400L);
        assertThat(image.getDisplayOrder()).isEqualTo(0);
        assertThat(product.getImages()).hasSize(1);
    }

    @Test
    @DisplayName("이미지 표시 순서를 변경한다")
    void changeOrder() {
        Product product = ProductFixture.register();
        ProductImage image =
                product.addImage(
                        ProductImageType.DETAIL,
                        "https://cdn.example.com/detail.jpg",
                        "products/detail/2026/05/uuid.jpg",
                        "image/jpeg",
                        204800L,
                        0);

        image.changeOrder(3);

        assertThat(image.getDisplayOrder()).isEqualTo(3);
    }
}
