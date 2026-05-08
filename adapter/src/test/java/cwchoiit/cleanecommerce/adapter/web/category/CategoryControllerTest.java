package cwchoiit.cleanecommerce.adapter.web.category;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cwchoiit.cleanecommerce.adapter.web.category.request.CategoryCreateRequest;
import cwchoiit.cleanecommerce.application.port.in.CategoryCreateUseCase;
import cwchoiit.cleanecommerce.domain.CategoryFixture;
import cwchoiit.cleanecommerce.domain.catalog.category.Category;
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
class CategoryControllerTest {

    @Mock CategoryCreateUseCase categoryCreateUseCase;

    MockMvc mockMvc;
    ObjectMapper MAPPER;

    @BeforeEach
    void setUp() {
        MAPPER = new ObjectMapper();

        CategoryController controller = new CategoryController(categoryCreateUseCase);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("카테고리 생성 호출")
    void create() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest("Category-1", 1L);

        Category category =
                CategoryFixture.registerWith(request.name(), CategoryFixture.registerWithId(1L));

        when(categoryCreateUseCase.create(eq(request.name()), eq(request.parentCategoryId())))
                .thenReturn(category);

        mockMvc.perform(
                        post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Category-1"))
                .andExpect(jsonPath("$.parentCategoryId").value(1));
    }

    @Test
    @DisplayName("카테고리 생성 실패")
    void createFail() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest(null, 1L);

        mockMvc.perform(
                        post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(MAPPER.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
