package cwchoiit.cleanecommerce.domain.catalog.schema;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductAttributeSchemaTest {

    ProductAttributeSchema schema;

    @BeforeEach
    void setUp() {
        AttributeDefinitionPayload screenSizeDef =
                new AttributeDefinitionPayload("screen_size", AttributeType.NUMBER, true, null);
        AttributeDefinitionPayload storageDef =
                new AttributeDefinitionPayload("storage", AttributeType.NUMBER, false, null);

        schema = ProductAttributeSchema.create(1L, List.of(screenSizeDef, storageDef));
    }

    @Test
    @DisplayName("속성값이 없는 스키마 생성도 통과한다")
    void createWithoutDefinition() {
        ProductAttributeSchema schemaEmptyDef = ProductAttributeSchema.create(1L, List.of());
        ProductAttributeSchema schemaWithoutDef = ProductAttributeSchema.create(1L, null);

        assertThat(schemaEmptyDef.getCategoryId()).isEqualTo(1L);
        assertThat(schemaWithoutDef.getCategoryId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("필수 속성이 있으면 정상 통과한다")
    void validatePasses() {
        Map<String, Object> attrs = Map.of("screen_size", 6.2, "storage", 256);
        assertThatCode(() -> schema.validate(attrs)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("필수 속성이 누락되면 예외가 발생한다")
    void requiredMissingThrows() {
        Map<String, Object> attrs = Map.of("storage", 256);
        assertThatThrownBy(() -> schema.validate(attrs))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("screen_size");
    }

    @Test
    @DisplayName("선택 속성이 누락되면 정상 통과한다")
    void optionalMissingPasses() {
        Map<String, Object> attrs = Map.of("screen_size", 6.2);
        assertThatCode(() -> schema.validate(attrs)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("정의되지 않은 속성 키가 있으면 예외가 발생한다")
    void undefinedKeyThrows() {
        Map<String, Object> attrs = Map.of("screen_size", 6.2, "unknown_key", "value");
        assertThatThrownBy(() -> schema.validate(attrs))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unknown_key");
    }

    @Test
    @DisplayName("attributes가 null이면 필수 속성 누락으로 예외가 발생한다")
    void nullAttributesThrowsForRequired() {
        assertThatThrownBy(() -> schema.validate(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("screen_size");
    }

    @Test
    @DisplayName("속성 타입이 맞지 않으면 예외가 발생한다")
    void typeMismatchThrows() {
        Map<String, Object> attrs = new HashMap<>();
        attrs.put("screen_size", "six point two");
        assertThatThrownBy(() -> schema.validate(attrs))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NUMBER");
    }

    @Test
    @DisplayName("속성을 정상적으로 삭제할 수 있다")
    void removeDefinition() {
        assertThat(schema.getDefinitions().size()).isEqualTo(2);

        schema.removeDefinition("screen_size");

        assertThat(schema.getDefinitions().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("속성을 삭제할 때 속성키를 NULL로 전달하면 예외가 발생한다")
    void removeDefinitionFail() {
        assertThatThrownBy(() -> schema.removeDefinition(null))
                .isInstanceOf(NullPointerException.class);
    }
}
