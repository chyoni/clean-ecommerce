package cwchoiit.cleanecommerce.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import cwchoiit.cleanecommerce.application.catalog.category.CategoryQueryService;
import cwchoiit.cleanecommerce.application.port.out.CategoryRepository;
import cwchoiit.cleanecommerce.domain.CategoryFixture;
import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryQueryUseCaseTest {

    @Mock CategoryRepository categoryRepository;

    CategoryQueryUseCase categoryQueryUseCase;

    @BeforeEach
    void setUp() {
        categoryQueryUseCase = new CategoryQueryService(categoryRepository);
    }

    @Test
    @DisplayName("카테고리 ID를 통해 카테고리를 찾을 수 있다")
    void findById() {
        Category category = CategoryFixture.register();

        when(categoryRepository.findByCategoryId(eq(1L))).thenReturn(Optional.of(category));

        Category findCategory = categoryQueryUseCase.findById(1L);

        assertThat(findCategory.getCategoryId()).isEqualTo(category.getCategoryId());
        verify(categoryRepository, times(1)).findByCategoryId(eq(1L));
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 ID를 제공하면 오류가 발생한다")
    void findByIdFail() {
        when(categoryRepository.findByCategoryId(eq(1L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryQueryUseCase.findById(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("카테고리 이름을 통해 존재하는 카테고리인지 확인할 수 있다")
    void existsName() {
        Category category = CategoryFixture.register();

        String categoryName = "categoryName";
        when(categoryRepository.findByName(eq(categoryName))).thenReturn(Optional.of(category));

        assertThat(categoryQueryUseCase.existsByName(categoryName)).isTrue();
    }

    @Test
    @DisplayName("카테고리 이름을 통해 존재하지 않는 카테고리인지 확인할 수 있다")
    void nonExistsName() {
        String categoryName = "categoryName";

        when(categoryRepository.findByName(eq(categoryName))).thenReturn(Optional.empty());

        assertThat(categoryQueryUseCase.existsByName(categoryName)).isFalse();
    }
}
