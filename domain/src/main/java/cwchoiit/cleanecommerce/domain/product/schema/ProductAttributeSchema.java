package cwchoiit.cleanecommerce.domain.product.schema;

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

@Entity
@Getter
@Table(name = "product_attribute_schema")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductAttributeSchema extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schemaId;

    private Long categoryId;

    @OneToMany(mappedBy = "schema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttributeDefinition> definitions = new ArrayList<>();

    public static ProductAttributeSchema create(Long categoryId) {
        ProductAttributeSchema schema = new ProductAttributeSchema();
        schema.categoryId = requireNonNull(categoryId);
        return schema;
    }

    public AttributeDefinition addDefinition(
            String attributeKey,
            AttributeType attributeType,
            boolean required,
            List<String> allowedValues) {
        AttributeDefinition def =
                AttributeDefinition.create(
                        this, attributeKey, attributeType, required, allowedValues);
        definitions.add(def);
        return def;
    }

    public void removeDefinition(String attributeKey) {
        requireNonNull(attributeKey);
        definitions.removeIf(d -> d.getAttributeKey().equals(attributeKey));
    }

    public void validate(Map<String, Object> attributes) {
        Map<String, Object> attrs = attributes != null ? attributes : Map.of();

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
