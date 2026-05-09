package cwchoiit.cleanecommerce.domain.catalog.schema;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.AbstractList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JsonStringListConverterTest {

    JsonStringListConverter converter = new JsonStringListConverter();

    List<String> list = List.of("a", "b", "c");

    String json = "[\"a\",\"b\",\"c\"]";

    @Test
    @DisplayName("리스트를 데이터베이스 컬럼형태에 맞게 Json으로 직렬화한다")
    void convertToDatabaseColumn() {
        String converted = converter.convertToDatabaseColumn(list);

        assertThat(converted).isEqualTo(json);
    }

    @Test
    @DisplayName("직렬화 불가능한 리스트를 변환 시 IllegalStateException이 발생한다")
    void convertToDatabaseColumnException() {
        List<String> bomb =
                new AbstractList<>() {
                    @Override
                    public String get(int index) {
                        throw new RuntimeException("serialization boom");
                    }

                    @Override
                    public int size() {
                        return 1;
                    }
                };

        assertThatThrownBy(() -> converter.convertToDatabaseColumn(bomb))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("allowedValues JSON 직렬화 실패");
    }

    @Test
    @DisplayName("직렬화 데이터가 NULL이면 NULL을 반환한다")
    void convertToDatabaseColumnNull() {
        String converted = converter.convertToDatabaseColumn(null);

        assertThat(converted).isNull();
    }

    @Test
    @DisplayName("데이터베이스 컬럼의 Json 데이터를 리스트로 역직렬화한다")
    void convertToEntityAttribute() {
        List<String> converted = converter.convertToEntityAttribute(json);

        assertThat(converted).isEqualTo(list);
    }

    @Test
    @DisplayName("역직렬화 불가능한 데이터베이스 컬럼의 Json 데이터를 리스트로 역직렬화 시 오류가 발생한다")
    void convertToEntityAttributeException() {
        assertThatThrownBy(() -> converter.convertToEntityAttribute("isNotList"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("역직렬화 데이터가 NULL이면 NULL을 반환한다")
    void convertToEntityAttributeNull() {
        List<String> converted = converter.convertToEntityAttribute(null);

        assertThat(converted).isNull();
    }
}
