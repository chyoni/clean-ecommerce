package cwchoiit.cleanecommerce.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import cwchoiit.cleanecommerce.application.catalog.schema.ProductAttributeSchemaQueryService;
import cwchoiit.cleanecommerce.application.port.out.ProductAttributeSchemaRepository;
import cwchoiit.cleanecommerce.domain.ProductAttributeSchemaFixture;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductAttributeSchemaQueryUseCaseTest {

    ProductAttributeSchemaQueryUseCase productAttributeSchemaQueryUseCase;

    @Mock ProductAttributeSchemaRepository productAttributeSchemaRepository;

    @BeforeEach
    void setUp() {
        productAttributeSchemaQueryUseCase =
                new ProductAttributeSchemaQueryService(productAttributeSchemaRepository);
    }

    @Test
    @DisplayName("스키마 ID로 조회")
    void findBySchemaId() {
        long schemaId = 1L;
        long categoryId = 2L;

        ProductAttributeSchema schema = ProductAttributeSchemaFixture.create(categoryId);

        doReturn(Optional.of(schema))
                .when(productAttributeSchemaRepository)
                .findBySchemaId(eq(schemaId));

        ProductAttributeSchema findSchema =
                productAttributeSchemaQueryUseCase.findBySchemaId(schemaId);

        assertThat(findSchema.getSchemaId()).isEqualTo(schema.getSchemaId());
    }

    @Test
    @DisplayName("스키마 ID로 조회할 때 없는 ID면 오류 발생")
    void findBySchemaIdFail() {
        long schemaId = 1L;
        long categoryId = 2L;

        ProductAttributeSchemaFixture.create(categoryId);

        doReturn(Optional.empty())
                .when(productAttributeSchemaRepository)
                .findBySchemaId(eq(schemaId));

        assertThatThrownBy(() -> productAttributeSchemaQueryUseCase.findBySchemaId(schemaId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("카테고리 ID로 조회")
    void findByCategoryId() {
        long categoryId = 2L;

        ProductAttributeSchema schema = ProductAttributeSchemaFixture.create(categoryId);

        doReturn(Optional.of(schema))
                .when(productAttributeSchemaRepository)
                .findByCategoryId(eq(categoryId));

        ProductAttributeSchema findSchema =
                productAttributeSchemaQueryUseCase.findByCategoryId(categoryId);

        assertThat(findSchema.getCategoryId()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("카테고리 ID로 조회할 때 없는 ID면 오류 발생")
    void findByCategoryIdFail() {
        long categoryId = 2L;

        ProductAttributeSchemaFixture.create(categoryId);

        doReturn(Optional.empty())
                .when(productAttributeSchemaRepository)
                .findByCategoryId(eq(categoryId));

        assertThatThrownBy(() -> productAttributeSchemaQueryUseCase.findByCategoryId(categoryId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("카테고리 ID로 존재 여부 조회")
    void existsByCategoryId() {
        long categoryId = 2L;

        ProductAttributeSchema schema = ProductAttributeSchemaFixture.create(categoryId);

        doReturn(Optional.of(schema))
                .when(productAttributeSchemaRepository)
                .findByCategoryId(eq(categoryId));

        boolean exists = productAttributeSchemaQueryUseCase.existsByCategoryId(categoryId);

        assertThat(exists).isTrue();

        boolean doesNotExists = productAttributeSchemaQueryUseCase.existsByCategoryId(999L);

        assertThat(doesNotExists).isFalse();
    }
}
