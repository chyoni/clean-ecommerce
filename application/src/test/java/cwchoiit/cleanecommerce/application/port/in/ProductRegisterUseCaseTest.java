package cwchoiit.cleanecommerce.application.port.in;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cwchoiit.cleanecommerce.application.port.out.CategoryRepository;
import cwchoiit.cleanecommerce.application.port.out.MemberRepository;
import cwchoiit.cleanecommerce.application.port.out.ProductAttributeSchemaRepository;
import cwchoiit.cleanecommerce.application.port.out.ProductRepository;
import cwchoiit.cleanecommerce.application.product.ProductRegisterService;
import cwchoiit.cleanecommerce.domain.CategoryFixture;
import cwchoiit.cleanecommerce.domain.MemberFixture;
import cwchoiit.cleanecommerce.domain.ProductAttributeSchemaFixture;
import cwchoiit.cleanecommerce.domain.ProductFixture;
import cwchoiit.cleanecommerce.domain.product.Product;
import cwchoiit.cleanecommerce.domain.product.ProductRegisterPayload;
import cwchoiit.cleanecommerce.domain.product.schema.ProductAttributeSchema;
import java.util.Map;
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

    @BeforeEach
    void setUp() {
        productRegisterUseCase =
                new ProductRegisterService(
                        productRepository,
                        memberRepository,
                        categoryRepository,
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
                .isInstanceOf(IllegalArgumentException.class);
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
}
