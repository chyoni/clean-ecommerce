package cwchoiit.cleanecommerce.application.port.in;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cwchoiit.cleanecommerce.application.catalog.category.CategoryQueryService;
import cwchoiit.cleanecommerce.application.catalog.product.ProductRegisterService;
import cwchoiit.cleanecommerce.application.port.out.CategoryRepository;
import cwchoiit.cleanecommerce.application.port.out.MemberRepository;
import cwchoiit.cleanecommerce.application.port.out.ProductAttributeSchemaRepository;
import cwchoiit.cleanecommerce.application.port.out.ProductRepository;
import cwchoiit.cleanecommerce.domain.CategoryFixture;
import cwchoiit.cleanecommerce.domain.MemberFixture;
import cwchoiit.cleanecommerce.domain.ProductAttributeSchemaFixture;
import cwchoiit.cleanecommerce.domain.ProductFixture;
import cwchoiit.cleanecommerce.domain.catalog.product.Product;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductRegisterPayload;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductSku;
import cwchoiit.cleanecommerce.domain.catalog.product.SkuPayload;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductRegisterUseCaseTest {

    @Mock ProductRepository productRepository;
    @Mock MemberRepository memberRepository;
    @Mock CategoryRepository categoryRepository;
    @Mock ProductAttributeSchemaRepository productAttributeSchemaRepository;

    ProductRegisterUseCase productRegisterUseCase;
    CategoryQueryUseCase categoryQueryUseCase;

    @BeforeEach
    void setUp() {
        categoryQueryUseCase = new CategoryQueryService(categoryRepository);
        productRegisterUseCase =
                new ProductRegisterService(
                        productRepository,
                        memberRepository,
                        categoryQueryUseCase,
                        productAttributeSchemaRepository);
        lenient()
                .when(memberRepository.findByMemberId(any()))
                .thenReturn(
                        Optional.of(
                                MemberFixture.register(MemberFixture.getMemberRegisterPayload())));
        lenient()
                .when(categoryRepository.findByCategoryId(any()))
                .thenReturn(Optional.of(CategoryFixture.registerWithId(1L)));
        lenient()
                .when(productAttributeSchemaRepository.findByCategoryId(any()))
                .thenReturn(Optional.empty());
    }

    @Test
    @DisplayName("상품을 등록한다")
    void register() {
        ProductRegisterPayload payload = ProductFixture.getProductRegisterPayload();

        productRegisterUseCase.register(payload);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("존재하지 않는 판매자로 등록하면 예외가 발생한다")
    void registerFailSellerNotFound() {
        when(memberRepository.findByMemberId(any())).thenReturn(Optional.empty());
        ProductRegisterPayload payload = ProductFixture.getProductRegisterPayload();

        assertThatThrownBy(() -> productRegisterUseCase.register(payload))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 등록하면 예외가 발생한다")
    void registerFailCategoryNotFound() {
        when(categoryRepository.findByCategoryId(any())).thenReturn(Optional.empty());
        ProductRegisterPayload payload = ProductFixture.getProductRegisterPayload();

        assertThatThrownBy(() -> productRegisterUseCase.register(payload))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("schema가 정의된 카테고리에 맞지 않는 attributes로 등록하면 예외가 발생한다")
    void registerFailSchemaValidation() {
        ProductAttributeSchema schema = ProductAttributeSchemaFixture.create(1L);
        when(productAttributeSchemaRepository.findByCategoryId(any()))
                .thenReturn(Optional.of(schema));

        ProductRegisterPayload payload =
                ProductFixture.builder().attributes(Map.of("unknown_key", "value")).build();

        assertThatThrownBy(() -> productRegisterUseCase.register(payload))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("schema 미정의 카테고리는 attributes 검증 없이 등록된다")
    void registerNoSchemaSkipsValidation() {
        when(productAttributeSchemaRepository.findByCategoryId(any())).thenReturn(Optional.empty());
        ProductRegisterPayload payload =
                ProductFixture.builder().attributes(Map.of("any_key", "any_value")).build();

        productRegisterUseCase.register(payload);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("상품에 SKU를 추가한다")
    void addSkus() {
        Product product = ProductFixture.register();
        long productId = 1L;

        when(productRepository.findByProductIdWithSkus(eq(productId)))
                .thenReturn(Optional.of(product));

        List<SkuPayload> skus =
                List.of(
                        new SkuPayload("SKU-001", null, 10_000, 100),
                        new SkuPayload("SKU-002", null, 10_000, 100));

        productRegisterUseCase.addSkus(productId, skus);

        assertThat(product.getSkus().size()).isEqualTo(3);

        verify(productRepository, times(1)).save(eq(product));
    }

    @Test
    @DisplayName("상품에 SKU를 추가하려고 할 때, 상품을 찾지 못하면 오류가 발생한다")
    void addSkusFailNotFound() {
        long productId = 1L;
        when(productRepository.findByProductIdWithSkus(eq(productId))).thenReturn(Optional.empty());

        List<SkuPayload> skus =
                List.of(
                        new SkuPayload("SKU-001", null, 10_000, 100),
                        new SkuPayload("SKU-002", null, 10_000, 100));

        assertThatThrownBy(() -> productRegisterUseCase.addSkus(productId, skus))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("상품에 SKU를 추가하려고 할 때, 같은 SKU Code를 여러번 추가하려고 하면 중복 오류가 발생한다")
    void addSkuFailDuplicateSkuCode() {
        Product product = ProductFixture.register();
        long productId = 1L;

        when(productRepository.findByProductIdWithSkus(eq(productId)))
                .thenReturn(Optional.of(product));

        List<SkuPayload> skus =
                List.of(
                        new SkuPayload("SKU-001", null, 10_000, 100),
                        new SkuPayload("SKU-001", null, 10_000, 100));

        assertThatThrownBy(() -> productRegisterUseCase.addSkus(productId, skus))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("상품에 SKU를 삭제한다")
    void deactivateSku() {
        Product product = ProductFixture.register();
        assertThat(product.getSkus()).hasSize(1);
        assertThat(product.getSkus().getFirst().isActive()).isTrue();

        long productId = 1L;

        when(productRepository.findByProductIdWithSkus(eq(productId)))
                .thenReturn(Optional.of(product));

        List<ProductSku> productSkus =
                productRegisterUseCase.deactivateSku(productId, "DEFAULT-SKU");

        assertThat(productSkus.size()).isEqualTo(1);
        assertThat(productSkus.getFirst().isActive()).isFalse();
    }

    @Test
    @DisplayName("상품에 SKU를 삭제하려할때 없는 상품으로 시도하는 경우 오류가 발생한다")
    void deactivateSkuFailEmptyProduct() {
        long productId = 1L;

        when(productRepository.findByProductIdWithSkus(eq(productId))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productRegisterUseCase.deactivateSku(productId, "DEFAULT-SKU"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("상품에 SKU를 삭제할 때 없는 SkuCode로 삭제하려고 하면 아무것도 삭제하지 않는다")
    void deactivateSkuFailDoesNotExist() {
        Product product = ProductFixture.register();
        assertThat(product.getSkus()).hasSize(1);

        long productId = 1L;

        when(productRepository.findByProductIdWithSkus(eq(productId)))
                .thenReturn(Optional.of(product));

        List<ProductSku> productSkus =
                productRegisterUseCase.deactivateSku(productId, "UNKNOWN-SKU");

        assertThat(productSkus.size()).isEqualTo(1);
    }
}
