package cwchoiit.cleanecommerce.application.port.in;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import cwchoiit.cleanecommerce.application.catalog.category.CategoryCreateService;
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
class CategoryCreateUseCaseTest {

    @Mock CategoryRepository categoryRepository;

    CategoryQueryUseCase categoryQueryUseCase;
    CategoryCreateUseCase categoryCreateUseCase;

    @BeforeEach
    void setUp() {
        categoryQueryUseCase = new CategoryQueryService(categoryRepository);
        categoryCreateUseCase = new CategoryCreateService(categoryRepository, categoryQueryUseCase);
    }

    @Test
    @DisplayName("카테고리를 생성한다")
    void create() {
        String categoryName = "Category-1";

        when(categoryRepository.findByName(eq(categoryName))).thenReturn(Optional.empty());

        categoryCreateUseCase.create(categoryName, null);

        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("동일한 이름의 카테고리가 이미 존재하면 오류가 발생한다")
    void createFail() {
        String categoryName = "Category-1";

        Category existingCategory = CategoryFixture.register();

        when(categoryRepository.findByName(eq(categoryName)))
                .thenReturn(Optional.of(existingCategory));

        assertThatThrownBy(() -> categoryCreateUseCase.create(categoryName, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상위 카테고리가 존재하는 카테고리를 생성한다")
    void createWithParentCategory() {
        String categoryName = "Category-1";

        when(categoryRepository.findByName(eq(categoryName))).thenReturn(Optional.empty());

        Category parentCategory = CategoryFixture.registerWithId(1L);

        when(categoryRepository.findByCategoryId(eq(parentCategory.getCategoryId())))
                .thenReturn(Optional.of(parentCategory));

        categoryCreateUseCase.create(categoryName, parentCategory.getCategoryId());

        verify(categoryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("존재하지 않는 상위 카테고리로 카테고리를 생성하면 오류가 발생한다")
    void createFailWithParentCategory() {
        String categoryName = "Category-1";

        when(categoryRepository.findByName(eq(categoryName))).thenReturn(Optional.empty());

        long emptyCategoryId = 2L;

        when(categoryRepository.findByCategoryId(eq(emptyCategoryId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryCreateUseCase.create(categoryName, emptyCategoryId))
                .isInstanceOf(NoSuchElementException.class);
    }
}
