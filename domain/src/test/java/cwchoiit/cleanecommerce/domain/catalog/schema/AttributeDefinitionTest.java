package cwchoiit.cleanecommerce.domain.catalog.schema;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AttributeDefinitionTest {

    private AttributeDefinition def(AttributeType type, boolean required) {
        AttributeDefinitionPayload payload =
                new AttributeDefinitionPayload("color", type, required, null);
        ProductAttributeSchema schema = ProductAttributeSchema.create(1L, List.of(payload));
        return schema.getDefinitions().getFirst();
    }

    private AttributeDefinition enumDef(List<String> allowedValues) {
        AttributeDefinitionPayload payload =
                new AttributeDefinitionPayload("color", AttributeType.ENUM, false, allowedValues);
        ProductAttributeSchema schema = ProductAttributeSchema.create(1L, List.of(payload));
        return schema.getDefinitions().getFirst();
    }

    @Test
    @DisplayName("필수 속성이 null이면 예외가 발생한다")
    void requiredNullThrows() {
        AttributeDefinition d = def(AttributeType.STRING, true);
        assertThatThrownBy(() -> d.validateValue(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("필수 속성");
    }

    @Test
    @DisplayName("선택 속성이 null이면 정상 통과한다")
    void optionalNullPasses() {
        AttributeDefinition d = def(AttributeType.STRING, false);
        assertThatCode(() -> d.validateValue(null)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("STRING 타입에 Number를 넣으면 예외가 발생한다")
    void stringTypeMismatch() {
        AttributeDefinition d = def(AttributeType.STRING, false);
        assertThatThrownBy(() -> d.validateValue(123))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("STRING");
    }

    @Test
    @DisplayName("STRING 타입에 올바른 값을 넣으면 통과한다")
    void stringTypePasses() {
        AttributeDefinition d = def(AttributeType.STRING, false);
        assertThatCode(() -> d.validateValue("RED")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NUMBER 타입에 올바른 값을 넣으면 통과한다")
    void numberTypePasses() {
        AttributeDefinition d = def(AttributeType.NUMBER, false);
        assertThatCode(() -> d.validateValue(6.2)).doesNotThrowAnyException();
        assertThatCode(() -> d.validateValue(256)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("NUMBER 타입에 String을 넣으면 예외가 발생한다")
    void numberTypeMismatch() {
        AttributeDefinition d = def(AttributeType.NUMBER, false);
        assertThatThrownBy(() -> d.validateValue("6.2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("NUMBER");
    }

    @Test
    @DisplayName("DATE 타입에 올바른 값을 넣으면 통과한다")
    void dateTypePasses() {
        AttributeDefinition d = def(AttributeType.DATE, false);
        assertThatCode(() -> d.validateValue("2026-05-08")).doesNotThrowAnyException();
        assertThatCode(() -> d.validateValue("2025-12-31")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("DATE 타입에 숫자를 넣으면 예외가 발생한다")
    void dateTypeMismatch() {
        AttributeDefinition d = def(AttributeType.DATE, false);
        assertThatThrownBy(() -> d.validateValue(6.2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DATE");
    }

    @Test
    @DisplayName("BOOLEAN 타입에 Boolean을 넣으면 통과한다")
    void booleanTypePasses() {
        AttributeDefinition d = def(AttributeType.BOOLEAN, false);
        assertThatCode(() -> d.validateValue(true)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("BOOLEAN 타입에 다른 타입을 넣으면 예외가 발생한다")
    void booleanTypeMismatch() {
        AttributeDefinition d = def(AttributeType.BOOLEAN, false);
        assertThatThrownBy(() -> d.validateValue("YELLOW"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("BOOLEAN");
    }

    @Test
    @DisplayName("ENUM 타입에 허용값이 아닌 값을 넣으면 예외가 발생한다")
    void enumNotAllowedValue() {
        AttributeDefinition d = enumDef(List.of("RED", "BLUE", "GREEN"));
        assertThatThrownBy(() -> d.validateValue("YELLOW"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않는 값");
    }

    @Test
    @DisplayName("ENUM 타입에 다른 타입을 넣으면 예외가 발생한다")
    void enumTypeMismatch() {
        AttributeDefinition d = enumDef(List.of("RED", "BLUE", "GREEN"));
        assertThatThrownBy(() -> d.validateValue(100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ENUM");
    }

    @Test
    @DisplayName("ENUM 타입에 허용값을 넣으면 통과한다")
    void enumAllowedValue() {
        AttributeDefinition d = enumDef(List.of("RED", "BLUE", "GREEN"));
        assertThatCode(() -> d.validateValue("RED")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ENUM 타입에 허용값이 따로 존재하지 않으면 모든값이 통과한다")
    void enumAllowedAnyValue() {
        AttributeDefinition d = enumDef(null);
        assertThatCode(() -> d.validateValue("RED")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("ENUM 타입에 허용값이 빈 리스트라면 모든값이 통과한다")
    void enumAllowedEmptyValue() {
        AttributeDefinition d = enumDef(List.of());
        assertThatCode(() -> d.validateValue("RED")).doesNotThrowAnyException();
    }
}
