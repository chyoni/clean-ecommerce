package cwchoiit.cleanecommerce.domain.catalog.schema;

import static java.util.Objects.requireNonNull;

import cwchoiit.cleanecommerce.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "product_attribute_schema")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductAttributeSchema extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schemaId;

    private Long categoryId;

    @OneToMany(mappedBy = "schema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributeDefinition> definitions = new ArrayList<>();

    public static ProductAttributeSchema create(
            Long categoryId, List<AttributeDefinitionPayload> payload) {
        ProductAttributeSchema schema = new ProductAttributeSchema();
        schema.categoryId = requireNonNull(categoryId);

        if (payload != null && !payload.isEmpty()) {
            for (AttributeDefinitionPayload attributeDefinition : payload) {
                schema.addDefinition(attributeDefinition);
            }
        }

        return schema;
    }

    public AttributeDefinition addDefinition(AttributeDefinitionPayload payload) {
        AttributeDefinition def = AttributeDefinition.create(this, payload);
        definitions.add(def);
        return def;
    }

    public void removeDefinition(String attributeKey) {
        requireNonNull(attributeKey);
        definitions.removeIf(d -> d.getAttributeKey().equals(attributeKey));
    }

    /**
     * 상품의 속성을 파라미터로 받아, 해당 상품이 속한 카테고리의 스키마 정의에 유효한지 검사한다.
     *
     * @param attributes 상품의 속성. 예) "attributes": { "screen_size": 6.2, "storage": 256, "color":"BLACK" }
     */
    public void validate(Map<String, Object> attributes) {
        Map<String, Object> attrs = attributes != null ? attributes : Map.of();

        // 현재 스키마가 가진 속성 정의들을 순회
        for (AttributeDefinition def : definitions) {
            def.validateValue(attrs.get(def.getAttributeKey()));
        }

        Set<String> definedKeys =
                definitions.stream()
                        .map(AttributeDefinition::getAttributeKey)
                        .collect(Collectors.toSet());
        for (String key : attrs.keySet()) {
            if (!definedKeys.contains(key)) {
                throw new IllegalArgumentException("정의되지 않은 속성 키입니다: " + key);
            }
        }
    }
}
