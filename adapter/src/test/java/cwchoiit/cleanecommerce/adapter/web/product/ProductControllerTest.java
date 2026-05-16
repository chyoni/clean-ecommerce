package cwchoiit.cleanecommerce.adapter.web.product;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cwchoiit.cleanecommerce.adapter.web.product.request.ProductAddSkuRequest;
import cwchoiit.cleanecommerce.application.port.in.ProductRegisterUseCase;
import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlCommand;
import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlResult;
import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlUseCase;
import cwchoiit.cleanecommerce.domain.catalog.product.*;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock IssueImageUploadUrlUseCase issueImageUploadUrlUseCase;
    @Mock ProductRegisterUseCase productRegisterUseCase;

    MockMvc mockMvc;
    ObjectMapper MAPPER;

    @BeforeEach
    void setUp() {
        MAPPER = new ObjectMapper();

        ProductController controller =
                new ProductController(issueImageUploadUrlUseCase, productRegisterUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("이미지 업로드 URL 발급 요청이 성공하면 201과 presigned URL을 반환한다")
    void issueImageUploadUrl() throws Exception {
        URI uploadUrl = URI.create("http://localhost:9000/presigned-put");
        Instant expiresAt = Instant.parse("2026-05-07T10:00:00Z");
        IssueImageUploadUrlResult result =
                new IssueImageUploadUrlResult(
                        uploadUrl,
                        "products/thumbnail/2026/05/uuid.jpg",
                        "http://localhost:9000/product-images/products/thumbnail/2026/05/uuid.jpg",
                        expiresAt,
                        Map.of("Content-Type", "image/jpeg"));
        when(issueImageUploadUrlUseCase.issue(any(IssueImageUploadUrlCommand.class)))
                .thenReturn(result);

        IssueImageUploadUrlCommand request =
                new IssueImageUploadUrlCommand(
                        ProductImageType.THUMBNAIL, "photo.jpg", "image/jpeg", 1024L);

        mockMvc.perform(
                        post("/api/v1/products/images/upload-url")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uploadUrl").value(uploadUrl.toString()))
                .andExpect(jsonPath("$.storageKey").value("products/thumbnail/2026/05/uuid.jpg"))
                .andExpect(jsonPath("$.publicUrl").isNotEmpty())
                .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                .andExpect(jsonPath("$.requiredHeaders.Content-Type").value("image/jpeg"));
    }

    @Test
    @DisplayName("imageType이 null이면 400을 반환한다")
    void issueImageUploadUrl_nullImageType() throws Exception {
        String requestJson =
                """
                {
                    "originalFileName": "photo.jpg",
                    "contentType": "image/jpeg",
                    "contentLength": 1024
                }
                """;

        mockMvc.perform(
                        post("/api/v1/products/images/upload-url")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("contentLength가 0이하이면 400을 반환한다")
    void issueImageUploadUrl_nonPositiveContentLength() throws Exception {
        String requestJson =
                """
                {
                    "imageType": "THUMBNAIL",
                    "originalFileName": "photo.jpg",
                    "contentType": "image/jpeg",
                    "contentLength": 0
                }
                """;

        mockMvc.perform(
                        post("/api/v1/products/images/upload-url")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("상품 등록 요청이 성공하면 201과 productId를 반환한다")
    void registerProduct() throws Exception {
        Product product = mock(Product.class);
        when(product.getProductId()).thenReturn(42L);
        when(productRegisterUseCase.register(any(ProductRegisterPayload.class)))
                .thenReturn(product);

        ProductRegisterPayload payload =
                new ProductRegisterPayload(
                        1L,
                        2L,
                        "테스트 상품",
                        null,
                        null,
                        "TestBrand",
                        "TestMfr",
                        null,
                        null,
                        null,
                        List.of(new SkuPayload("SKU-001", null, 10000, 100)),
                        null);

        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(42L));
    }

    @Test
    @DisplayName("sellerId가 null이면 400을 반환한다")
    void registerProduct_nullSellerId() throws Exception {
        String requestJson =
                """
                {
                    "categoryId": 2,
                    "productName": "테스트 상품",
                    "brand": "TestBrand",
                    "manufacturer": "TestMfr",
                    "skus": [
                        { "skuCode": "SKU-001", "price": 10000, "stockQuantity": 100 }
                    ]
                }
                """;

        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("skus가 비어있으면 400을 반환한다")
    void registerProduct_emptySkus() throws Exception {
        String requestJson =
                """
                {
                    "sellerId": 1,
                    "categoryId": 2,
                    "productName": "테스트 상품",
                    "brand": "TestBrand",
                    "manufacturer": "TestMfr",
                    "skus": []
                }
                """;

        mockMvc.perform(
                        post("/api/v1/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("SKU를 추가한다")
    void addSkus() throws Exception {
        ProductAddSkuRequest skus =
                new ProductAddSkuRequest(
                        List.of(
                                new SkuPayload("SKU-001", null, 10000, 100),
                                new SkuPayload("SKU-002", null, 10000, 100)));

        long productId = 1L;

        ProductSku productSku = mock(ProductSku.class);
        when(productSku.getSkuCode()).thenReturn("SKU-001");

        ProductSku productSku2 = mock(ProductSku.class);
        when(productSku2.getSkuCode()).thenReturn("SKU-002");

        when(productRegisterUseCase.addSkus(eq(productId), eq(skus.skus())))
                .thenReturn(List.of(productSku, productSku2));

        mockMvc.perform(
                        post("/api/v1/products/{productId}/skus", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(skus)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.skus[0].skuCode").value("SKU-001"))
                .andExpect(jsonPath("$.skus[1].skuCode").value("SKU-002"));
    }
}
