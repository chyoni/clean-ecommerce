package cwchoiit.cleanecommerce.domain.catalog.category;

import static org.assertj.core.api.Assertions.*;

import cwchoiit.cleanecommerce.domain.CategoryFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    @DisplayName("카테고리를 생성한다")
    void create() {
        Category category = CategoryFixture.register();

        assertThat(category.getName()).isNotNull();
    }

    @Test
    @DisplayName("카테고리 생성 시 이름이 없으면 오류가 발생한다")
    void createFail() {
        assertThatThrownBy(() -> CategoryFixture.builder().name(null).build())
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("카테고리 생성 시 부모 카테고리가 없어도 된다")
    void createWithoutParent() {
        Category category = CategoryFixture.builder().parent(null).build();

        assertThat(category.getName()).isNotNull();
        assertThat(category.getParent()).isNull();
    }
}
