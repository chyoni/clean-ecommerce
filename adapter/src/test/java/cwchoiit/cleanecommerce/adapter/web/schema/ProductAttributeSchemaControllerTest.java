package cwchoiit.cleanecommerce.adapter.web.schema;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cwchoiit.cleanecommerce.adapter.web.schema.request.AddDefinitionToSchemaRequest;
import cwchoiit.cleanecommerce.adapter.web.schema.request.ProductAttributeSchemaRegisterRequest;
import cwchoiit.cleanecommerce.adapter.web.schema.request.RemoveDefinitionToSchemaRequest;
import cwchoiit.cleanecommerce.application.port.in.ProductAttributeSchemaRegisterUseCase;
import cwchoiit.cleanecommerce.domain.ProductAttributeSchemaFixture;
import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeDefinitionPayload;
import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeType;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import java.util.List;
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

    @Test
    @DisplayName("스키마에 속성을 추가한다")
    void addDefinition() throws Exception {
        ProductAttributeSchema schema = ProductAttributeSchemaFixture.createWithIds(1L, 2L);

        List<AttributeDefinitionPayload> attributes =
                List.of(
                        new AttributeDefinitionPayload(
                                "screen_size", AttributeType.NUMBER, true, null));
        AddDefinitionToSchemaRequest addDefinitionToSchemaRequest =
                new AddDefinitionToSchemaRequest(schema.getSchemaId(), attributes);

        when(productAttributeSchemaRegisterUseCase.addDefinition(
                        eq(schema.getSchemaId()), eq(attributes)))
                .thenReturn(schema);

        mockMvc.perform(
                        post("/api/v1/schema/definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(addDefinitionToSchemaRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.schemaId").value(schema.getSchemaId()));
    }

    @Test
    @DisplayName("스키마에 속성을 추가할때, 요청 바디값에 속성이 빈 값이면 400 응답을 반환한다.")
    void addDefinitionValidateDefs() throws Exception {
        ProductAttributeSchema schema = ProductAttributeSchemaFixture.createWithIds(1L, 2L);

        List<AttributeDefinitionPayload> attributes = List.of();
        AddDefinitionToSchemaRequest addDefinitionToSchemaRequest =
                new AddDefinitionToSchemaRequest(schema.getSchemaId(), attributes);

        mockMvc.perform(
                        post("/api/v1/schema/definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(addDefinitionToSchemaRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("스키마에 속성을 추가할때, 요청 바디값에 스키마 ID가 Null이면 400 응답을 반환한다.")
    void addDefinitionValidateSchemaId() throws Exception {
        ProductAttributeSchemaFixture.createWithIds(1L, 2L);

        List<AttributeDefinitionPayload> attributes =
                List.of(
                        new AttributeDefinitionPayload(
                                "screen_size", AttributeType.NUMBER, true, null));
        AddDefinitionToSchemaRequest addDefinitionToSchemaRequest =
                new AddDefinitionToSchemaRequest(null, attributes);

        mockMvc.perform(
                        post("/api/v1/schema/definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(addDefinitionToSchemaRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("스키마에 속성을 제거한다")
    void removeDefinition() throws Exception {
        RemoveDefinitionToSchemaRequest request =
                new RemoveDefinitionToSchemaRequest(1L, "screen_size");

        mockMvc.perform(
                        delete("/api/v1/schema/definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("스키마에 속성을 제거할때, 속성 키가 null이면 400 응답을 돌려받는다")
    void removeDefinitionValidateAttributeKey() throws Exception {
        RemoveDefinitionToSchemaRequest request = new RemoveDefinitionToSchemaRequest(1L, null);

        mockMvc.perform(
                        delete("/api/v1/schema/definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("스키마에 속성을 제거할때, 스키마 ID가 null이면 400 응답을 돌려받는다")
    void removeDefinitionValidateSchemaId() throws Exception {
        RemoveDefinitionToSchemaRequest request =
                new RemoveDefinitionToSchemaRequest(null, "screen_size");

        mockMvc.perform(
                        delete("/api/v1/schema/definitions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
