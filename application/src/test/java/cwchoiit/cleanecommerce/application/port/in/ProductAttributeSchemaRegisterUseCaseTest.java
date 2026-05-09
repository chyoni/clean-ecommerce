package cwchoiit.cleanecommerce.application.port.in;

import static cwchoiit.cleanecommerce.domain.catalog.schema.AttributeType.*;
import static cwchoiit.cleanecommerce.domain.catalog.schema.AttributeType.STRING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import cwchoiit.cleanecommerce.application.catalog.category.CategoryQueryService;
import cwchoiit.cleanecommerce.application.catalog.schema.ProductAttributeSchemaRegisterService;
import cwchoiit.cleanecommerce.application.port.out.CategoryRepository;
import cwchoiit.cleanecommerce.application.port.out.ProductAttributeSchemaRepository;
import cwchoiit.cleanecommerce.domain.CategoryFixture;
import cwchoiit.cleanecommerce.domain.ProductAttributeSchemaFixture;
import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeDefinitionPayload;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductAttributeSchemaRegisterUseCaseTest {

    ProductAttributeSchemaRegisterUseCase productAttributeSchemaRegisterUseCase;
    CategoryQueryUseCase categoryQueryUseCase;

    @Mock CategoryRepository categoryRepository;
    @Mock ProductAttributeSchemaRepository productAttributeSchemaRepository;

    @BeforeEach
    void setUp() {
        categoryQueryUseCase = new CategoryQueryService(categoryRepository);
        productAttributeSchemaRegisterUseCase =
                new ProductAttributeSchemaRegisterService(
                        productAttributeSchemaRepository, categoryQueryUseCase);
    }

    @Test
    @DisplayName("상품 속성 스키마를 속성 정의 데이터 없이 생성한다")
    void register() {
        long categoryId = 1L;
        Category category = CategoryFixture.register();

        when(categoryRepository.findByCategoryId(eq(categoryId))).thenReturn(Optional.of(category));

        when(productAttributeSchemaRepository.findByCategoryId(any())).thenReturn(Optional.empty());

        productAttributeSchemaRegisterUseCase.register(categoryId, null);

        verify(productAttributeSchemaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("없는 카테고리의 스키마를 생성하는 경우 오류가 발생한다")
    void registerFailDoesNotExistsCategory() {
        long categoryId = 1L;

        when(categoryRepository.findByCategoryId(eq(categoryId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productAttributeSchemaRegisterUseCase.register(categoryId, null))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("카테고리의 스키마가 이미 존재하는 경우에 스키마를 또 생성하려고 하면 오류가 발생한다")
    void registerFailDuplicateSchema() {
        long categoryId = 1L;
        Category category = CategoryFixture.register();
        when(categoryRepository.findByCategoryId(eq(categoryId))).thenReturn(Optional.of(category));

        ProductAttributeSchema schema = ProductAttributeSchemaFixture.create(categoryId);
        when(productAttributeSchemaRepository.findByCategoryId(any()))
                .thenReturn(Optional.of(schema));

        assertThatThrownBy(() -> productAttributeSchemaRegisterUseCase.register(categoryId, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("스키마 생성을 속성 정의와 같이한다")
    void registerWithDefs() {
        long categoryId = 1L;
        Category category = CategoryFixture.register();

        when(categoryRepository.findByCategoryId(eq(categoryId))).thenReturn(Optional.of(category));
        when(productAttributeSchemaRepository.findByCategoryId(any())).thenReturn(Optional.empty());

        when(productAttributeSchemaRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        ProductAttributeSchema schema =
                productAttributeSchemaRegisterUseCase.register(
                        categoryId, getAttributeDefinitionPayloads());

        verify(productAttributeSchemaRepository, times(1)).save(any());

        assertThat(schema.getCategoryId()).isEqualTo(categoryId);
        assertThat(schema.getDefinitions().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("스키마와 속성 정의를 같이 생성할때 필수 속성값을 전달하지 않으면 오류가 발생한다")
    void registerFailValidation() {
        long categoryId = 1L;
        Category category = CategoryFixture.register();

        when(categoryRepository.findByCategoryId(eq(categoryId))).thenReturn(Optional.of(category));
        when(productAttributeSchemaRepository.findByCategoryId(any())).thenReturn(Optional.empty());

        // attributeType은 필수값
        List<AttributeDefinitionPayload> definitions =
                List.of(new AttributeDefinitionPayload("screen_size", null, true, null));

        assertThatThrownBy(
                        () ->
                                productAttributeSchemaRegisterUseCase.register(
                                        categoryId, definitions))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("스키마에 속성을 추가한다")
    void addDefinitions() {
        long schemaId = 1L;
        ProductAttributeSchema schema = ProductAttributeSchemaFixture.create(schemaId);
        when(productAttributeSchemaRepository.findBySchemaId(eq(schemaId)))
                .thenReturn(Optional.of(schema));

        assertThat(schema.getDefinitions().size()).isEqualTo(2);

        when(productAttributeSchemaRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        ProductAttributeSchema updatedSchema =
                productAttributeSchemaRegisterUseCase.addDefinition(
                        schemaId, getAttributeDefinitionPayloads());

        assertThat(updatedSchema.getDefinitions().size()).isEqualTo(4);
        assertThat(updatedSchema).isEqualTo(schema);
    }

    @Test
    @DisplayName("없는 스키마에 속성을 추가하려고하면 오류가 발생한다")
    void addDefinitionsFail() {
        long schemaId = 1L;
        when(productAttributeSchemaRepository.findBySchemaId(eq(schemaId)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(
                        () ->
                                productAttributeSchemaRegisterUseCase.addDefinition(
                                        schemaId, getAttributeDefinitionPayloads()))
                .isInstanceOf(NoSuchElementException.class);
    }

    private @NonNull List<AttributeDefinitionPayload> getAttributeDefinitionPayloads() {
        return List.of(
                new AttributeDefinitionPayload("weight_kg", NUMBER, true, null),
                new AttributeDefinitionPayload(
                        "color", STRING, true, List.of("RED", "BLUE", "GREEN")));
    }
}
