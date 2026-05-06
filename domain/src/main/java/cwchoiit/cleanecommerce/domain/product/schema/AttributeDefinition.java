package cwchoiit.cleanecommerce.domain.product.schema;

import static java.util.Objects.requireNonNull;

import cwchoiit.cleanecommerce.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttributeDefinition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long definitionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schema_id")
    private ProductAttributeSchema schema;

    private String attributeKey;

    @Enumerated(EnumType.STRING)
    private AttributeType attributeType;

    private boolean required;

    @Convert(converter = JsonStringListConverter.class)
    @Column(name = "allowed_values", columnDefinition = "json")
    private List<String> allowedValues;

    static AttributeDefinition create(
            ProductAttributeSchema schema,
            String attributeKey,
            AttributeType attributeType,
            boolean required,
            List<String> allowedValues) {
        AttributeDefinition def = new AttributeDefinition();
        def.schema = requireNonNull(schema);
        def.attributeKey = requireNonNull(attributeKey);
        def.attributeType = requireNonNull(attributeType);
        def.required = required;
        def.allowedValues = allowedValues != null ? new ArrayList<>(allowedValues) : null;
        return def;
    }

    public void validateValue(Object value) {
        if (value == null) {
            if (required) {
                throw new IllegalArgumentException("필수 속성 '" + attributeKey + "'이 누락되었습니다");
            }
            return;
        }
        switch (attributeType) {
            case STRING -> {
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                            "속성 '" + attributeKey + "'은 STRING 타입이어야 합니다");
                }
            }
            case NUMBER -> {
                if (!(value instanceof Number)) {
                    throw new IllegalArgumentException(
                            "속성 '" + attributeKey + "'은 NUMBER 타입이어야 합니다");
                }
            }
            case DATE -> {
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                            "속성 '" + attributeKey + "'은 DATE(String ISO) 타입이어야 합니다");
                }
            }
            case BOOLEAN -> {
                if (!(value instanceof Boolean)) {
                    throw new IllegalArgumentException(
                            "속성 '" + attributeKey + "'은 BOOLEAN 타입이어야 합니다");
                }
            }
            case ENUM -> {
                if (!(value instanceof String)) {
                    throw new IllegalArgumentException(
                            "속성 '" + attributeKey + "'은 ENUM(String) 타입이어야 합니다");
                }
                if (allowedValues != null
                        && !allowedValues.isEmpty()
                        && !allowedValues.contains(value)) {
                    throw new IllegalArgumentException(
                            "속성 '" + attributeKey + "'의 허용되지 않는 값입니다: " + value);
                }
            }
        }
    }
}
