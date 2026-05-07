package cwchoiit.cleanecommerce.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cwchoiit.cleanecommerce.domain.CategoryFixture;
import cwchoiit.cleanecommerce.domain.MemberFixture;
import cwchoiit.cleanecommerce.domain.ProductFixture;
import cwchoiit.cleanecommerce.domain.member.Member;
import cwchoiit.cleanecommerce.domain.member.MemberRegisterPayload;
import cwchoiit.cleanecommerce.domain.member.MemberRole;
import cwchoiit.cleanecommerce.domain.product.category.Category;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProductTest {

    Member defaultSeller;
    Category defaultCategory;

    @BeforeEach
    void setUp() {
        defaultSeller = MemberFixture.register(MemberFixture.getMemberRegisterPayload());
        defaultCategory = CategoryFixture.registerWithId(1L);
    }

    @Test
    @DisplayName("상품을 정상 등록한다")
    void register() {
        ProductRegisterPayload payload = ProductFixture.getProductRegisterPayload();

        Product product = Product.register(payload, defaultSeller, defaultCategory);

        assertThat(product.getProductName()).isEqualTo(payload.productName());
    }

    @Test
    @DisplayName("상품 등록 시 판매자 유형의 회원이 아닌 경우 오류가 발생한다")
    void registerFailNotSeller() {
        MemberRegisterPayload memberRegisterPayload =
                MemberFixture.builder().role(MemberRole.NORMAL).build();
        Member normalMember = MemberFixture.register(memberRegisterPayload);

        ProductRegisterPayload payload = ProductFixture.getProductRegisterPayload();

        assertThatThrownBy(() -> Product.register(payload, normalMember, defaultCategory))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("seller가 NULL이면 등록에 실패한다")
    void registerFailNullSeller() {
        ProductRegisterPayload payload = ProductFixture.getProductRegisterPayload();

        assertThatThrownBy(() -> Product.register(payload, null, defaultCategory))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("상품을 등록할 때 상품 상태를 받지 않으면 기본으로 DRAFT 상태로 설정한다")
    void registerDefaultStatus() {
        ProductRegisterPayload payload = ProductFixture.builder().status(null).build();

        Product product = Product.register(payload, defaultSeller, defaultCategory);

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.DRAFT);
    }

    @Test
    @DisplayName("상품을 등록할 때 판매시작일을 등록하지 않으면 기본으로 현재날짜로 등록한다")
    void registerDefaultSalesStartDate() {
        ProductRegisterPayload payload = ProductFixture.builder().salesStartDate(null).build();

        Product product = Product.register(payload, defaultSeller, defaultCategory);

        assertThat(product.getSalesStartDate()).isNotNull();
    }

    @Test
    @DisplayName("판매 시작일을 수정한다")
    void changeSalesStartDate() {
        Product product = ProductFixture.register();

        LocalDateTime prevStartDate = product.getSalesStartDate();

        product.changeSalesStartDate(LocalDateTime.now());

        assertThat(product.getSalesStartDate().isAfter(prevStartDate)).isTrue();
    }

    @Test
    @DisplayName("판매 시작일이 판매 종료일보다 이후인 경우 오류가 발생한다")
    void changeSalesStartDateFail() {
        ProductRegisterPayload payload =
                ProductFixture.builder()
                        .salesStartDate(LocalDateTime.now())
                        .salesEndDate(LocalDateTime.now().plusDays(1))
                        .build();

        Product product = Product.register(payload, defaultSeller, defaultCategory);

        assertThatThrownBy(() -> product.changeSalesStartDate(LocalDateTime.now().plusDays(2)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("판매 종료일을 수정한다")
    void changeSalesEndDate() {
        Product product = ProductFixture.register();

        LocalDateTime prevEndDate = product.getSalesEndDate();

        product.changeSalesEndDate(LocalDateTime.now());

        assertThat(prevEndDate).isNotEqualTo(product.getSalesEndDate());
    }

    @Test
    @DisplayName("판매 종료일이 판매 시작일보다 이전인 경우 오류가 발생한다")
    void changeSalesEndDateFail() {
        ProductRegisterPayload payload =
                ProductFixture.builder()
                        .salesStartDate(LocalDateTime.now())
                        .salesEndDate(LocalDateTime.now().plusDays(1))
                        .build();

        Product product = Product.register(payload, defaultSeller, defaultCategory);

        assertThatThrownBy(() -> product.changeSalesEndDate(LocalDateTime.now().minusDays(2)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("상품 상태를 허용된 전이로 수정한다")
    void changeStatus() {
        Product product = ProductFixture.register();
        // Fixture default: AVAILABLE → OUT_OF_STOCK 은 허용된 전이
        product.changeProductStatus(ProductStatus.OUT_OF_STOCK);

        assertThat(product.getProductStatus()).isEqualTo(ProductStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("상품 상태 수정 시 NULL을 허용하지 않는다")
    void changeStatusFailNull() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.changeProductStatus(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("허용되지 않는 상태 전이 시 예외가 발생한다")
    void changeStatusFailInvalidTransition() {
        ProductRegisterPayload payload =
                ProductFixture.builder().status(ProductStatus.DRAFT).build();
        Product draftProduct = Product.register(payload, defaultSeller, defaultCategory);

        // DRAFT → AVAILABLE 은 허용되지 않음
        assertThatThrownBy(() -> draftProduct.changeProductStatus(ProductStatus.AVAILABLE))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("상품 이름을 수정한다")
    void changeProductName() {
        Product product = ProductFixture.register();

        String newProductName = "새로운 상품";
        product.changeProductName(newProductName);

        assertThat(product.getProductName()).isEqualTo(newProductName);
    }

    @Test
    @DisplayName("상품 이름 수정 시 NULL을 허용하지 않는다")
    void changeProductNameFail() {
        Product product = ProductFixture.register();

        assertThatThrownBy(() -> product.changeProductName(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest(name = "{1} 이 NULL이면 등록에 실패한다")
    @MethodSource("nullRequiredFieldPayloads")
    @DisplayName("필수 필드가 NULL이면 등록에 실패한다")
    void registerValidation(ProductRegisterPayload payload, String field) {
        assertThatThrownBy(() -> Product.register(payload, defaultSeller, defaultCategory))
                .isInstanceOf(NullPointerException.class);
    }

    private static Stream<Arguments> nullRequiredFieldPayloads() {
        return Stream.of(
                Arguments.of(ProductFixture.builder().productName(null).build(), "productName"),
                Arguments.of(ProductFixture.builder().brand(null).build(), "brand"),
                Arguments.of(ProductFixture.builder().manufacturer(null).build(), "manufacturer"));
    }

    @Test
    @DisplayName("상품 등록 시 payload 의 SKU 가 함께 저장된다")
    void registerWithSkusFromPayload() {
        ProductRegisterPayload payload =
                ProductFixture.builder()
                        .skus(
                                List.of(
                                        new SkuPayload("SKU-A", null, 1_000_000, 10),
                                        new SkuPayload(
                                                "SKU-B", Map.of("color", "RED"), 1_200_000, 5)))
                        .build();

        Product product = Product.register(payload, defaultSeller, defaultCategory);

        assertThat(product.getSkus()).hasSize(2);
    }

    @Test
    @DisplayName("상품 등록 시 payload 의 이미지가 함께 저장된다")
    void registerWithImagesFromPayload() {
        ProductRegisterPayload payload =
                ProductFixture.builder()
                        .images(
                                List.of(
                                        new ImagePayload(
                                                ProductImageType.THUMBNAIL,
                                                "https://cdn.example.com/thumb.jpg",
                                                "products/thumbnail/2026/05/uuid.jpg",
                                                "image/jpeg",
                                                102400L,
                                                0)))
                        .build();

        Product product = Product.register(payload, defaultSeller, defaultCategory);

        assertThat(product.getImages()).hasSize(1);
        assertThat(product.getImages().getFirst().getImageType())
                .isEqualTo(ProductImageType.THUMBNAIL);
        assertThat(product.getImages().getFirst().getStorageKey())
                .isEqualTo("products/thumbnail/2026/05/uuid.jpg");
    }

    @Test
    @DisplayName("상품 등록 시 SKU 가 비어있으면 예외가 발생한다")
    void registerFailEmptySkus() {
        ProductRegisterPayload payload = ProductFixture.builder().skus(List.of()).build();

        assertThatThrownBy(() -> Product.register(payload, defaultSeller, defaultCategory))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("SKU를 등록한다")
    void registerSku() {
        Product product = ProductFixture.register();

        ProductSku sku = product.registerSku("SKU-NEW", null, 1_500_000, 100);

        assertThat(product.getSkus()).hasSize(2); // fixture default SKU 1개 + 추가 1개
        assertThat(sku.getSkuCode()).isEqualTo("SKU-NEW");
    }

    @Test
    @DisplayName("SKU를 제거한다")
    void removeSku() {
        Product product = ProductFixture.register();
        ProductSku sku = product.registerSku("SKU-NEW", null, 1_500_000, 100);

        product.removeSku(sku);

        assertThat(product.getSkus()).hasSize(1); // fixture default SKU만 남음
    }

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

        assertThat(product.getImages()).hasSize(1);
        assertThat(image.getImageType()).isEqualTo(ProductImageType.THUMBNAIL);
        assertThat(image.getStorageKey()).isEqualTo("products/thumbnail/2026/05/uuid.jpg");
    }

    @Test
    @DisplayName("이미지를 제거한다")
    void removeImage() {
        Product product = ProductFixture.register();
        ProductImage image =
                product.addImage(
                        ProductImageType.THUMBNAIL,
                        "https://cdn.example.com/thumb.jpg",
                        "products/thumbnail/2026/05/uuid.jpg",
                        "image/jpeg",
                        102400L,
                        0);

        product.removeImage(image);

        assertThat(product.getImages()).isEmpty();
    }

    @Test
    @DisplayName("상품 상세 HTML을 수정한다")
    void changeDescriptionHtml() {
        Product product = ProductFixture.register();
        String html = "<p>상품 상세 설명</p>";

        product.changeDescriptionHtml(html);

        assertThat(product.getDescriptionHtml()).isEqualTo(html);
    }
}
