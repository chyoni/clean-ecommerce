package cwchoiit.cleanecommerce.adapter.web.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new TestController())
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
    }

    @Test
    @DisplayName("IllegalArgumentException은 400 ProblemDetail을 반환")
    void handleIllegalArgument() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Illegal Argument"))
                .andExpect(jsonPath("$.exception").value("IllegalArgumentException"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("IllegalStateException은 400 ProblemDetail을 반환")
    void handleIllegalState() throws Exception {
        mockMvc.perform(get("/test/illegal-state"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Illegal State"))
                .andExpect(jsonPath("$.exception").value("IllegalStateException"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("그 외 Exception은 500 ProblemDetail을 반환")
    void handleException() throws Exception {
        mockMvc.perform(get("/test/unknown"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("Runtime error"))
                .andExpect(jsonPath("$.exception").value("RuntimeException"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @RestController
    static class TestController {

        @GetMapping("/test/illegal-argument")
        void throwIllegalArgumentException() {
            throw new IllegalArgumentException("Illegal Argument");
        }

        @GetMapping("/test/illegal-state")
        void throwIllegalStateException() {
            throw new IllegalStateException("Illegal State");
        }

        @GetMapping("/test/unknown")
        void throwUnknownException() {
            throw new RuntimeException("Runtime error");
        }
    }
}
