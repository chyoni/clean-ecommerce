package cwchoiit.cleanecommerce.domain.catalog.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonAttributeConverterTest {

    JsonAttributeConverter converter = new JsonAttributeConverter();

    Map<String, Object> attribute = Map.of("color", "red");

    String json = "{\"color\":\"red\"}";

    @Test
    @DisplayName("직렬화")
    void convertToDatabaseColumn() {
        String converted = converter.convertToDatabaseColumn(attribute);

        assertThat(converted).isEqualTo(json);
    }

    @Test
    @DisplayName("직렬화 데이터가 NULL이면 NULL을 반환")
    void convertToDatabaseColumnNull() {
        String converted = converter.convertToDatabaseColumn(null);

        assertThat(converted).isNull();
    }

    @Test
    @DisplayName("직렬화 불가능한 데이터인 경우 오류가 발생한다")
    void convertToDatabaseColumnException() {
        AbstractMap<String, Object> boom =
                new AbstractMap<>() {
                    @Override
                    public Set<Entry<String, Object>> entrySet() {
                        throw new RuntimeException("boom");
                    }

                    @Override
                    public Object getOrDefault(Object key, Object defaultValue) {
                        throw new RuntimeException("boom");
                    }
                };

        assertThatThrownBy(() -> converter.convertToDatabaseColumn(boom))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("attributes JSON 직렬화 실패");
    }

    @Test
    @DisplayName("역직렬화")
    void convertToEntityAttribute() {
        Map<String, Object> map = converter.convertToEntityAttribute(json);

        assertThat(map.get("color")).isEqualTo("red");
    }

    @Test
    @DisplayName("역직렬화 데이터가 NULL인 경우, 역직렬화 시 NULL을 반환한다")
    void convertToEntityAttributeNull() {
        Map<String, Object> map = converter.convertToEntityAttribute(null);

        assertThat(map).isNull();
    }

    @Test
    @DisplayName("역직렬화 불가능한 데이터인 경우 오류가 발생한다")
    void convertToEntityAttributeException() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("isNotMap"))
                .isInstanceOf(IllegalStateException.class);
    }
}
