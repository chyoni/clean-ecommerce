package cwchoiit.cleanecommerce.adapter.web.schema;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cwchoiit.cleanecommerce.adapter.web.schema.request.ProductAttributeSchemaRegisterRequest;
import cwchoiit.cleanecommerce.application.port.in.ProductAttributeSchemaRegisterUseCase;
import cwchoiit.cleanecommerce.domain.ProductAttributeSchemaFixture;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProductAttributeSchemaControllerTest {

    MockMvc mockMvc;
    ObjectMapper MAPPER;

    @Mock ProductAttributeSchemaRegisterUseCase productAttributeSchemaRegisterUseCase;

    @BeforeEach
    void setUp() {
        ProductAttributeSchemaController controller =
                new ProductAttributeSchemaController(productAttributeSchemaRegisterUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        MAPPER = new ObjectMapper();
    }

    @Test
    @DisplayName("스키마를 등록한다")
    void register() throws Exception {
        ProductAttributeSchemaRegisterRequest request =
                new ProductAttributeSchemaRegisterRequest(1L, null);

        ProductAttributeSchema schema = ProductAttributeSchemaFixture.create(request.categoryId());
        when(productAttributeSchemaRegisterUseCase.register(
                        eq(request.categoryId()), eq(request.attributeDefinitions())))
                .thenReturn(schema);

        mockMvc.perform(
                        post("/api/v1/schema")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value(request.categoryId()));
    }

    @Test
    @DisplayName("스키마 등록 시 카테고리를 전달하지 않으면 오류가 발생한다")
    void registerFail() throws Exception {
        ProductAttributeSchemaRegisterRequest request =
                new ProductAttributeSchemaRegisterRequest(null, null);

        mockMvc.perform(
                        post("/api/v1/schema")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
