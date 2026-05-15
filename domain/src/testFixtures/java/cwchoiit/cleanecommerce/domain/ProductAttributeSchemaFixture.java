package cwchoiit.cleanecommerce.domain;

import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeDefinitionPayload;
import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeType;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import java.lang.reflect.Field;
import java.util.List;

public class ProductAttributeSchemaFixture {

    public static ProductAttributeSchema create(Long categoryId) {
        AttributeDefinitionPayload screenSizeDef =
                new AttributeDefinitionPayload("screen_size", AttributeType.NUMBER, true, null);
        AttributeDefinitionPayload storageDef =
                new AttributeDefinitionPayload("storage", AttributeType.NUMBER, true, null);

        return ProductAttributeSchema.create(categoryId, List.of(screenSizeDef, storageDef));
    }

    public static ProductAttributeSchema createWithIds(Long categoryId, Long schemaId) {
        ProductAttributeSchema schema = create(categoryId);

        setField(schema, "schemaId", schemaId);

        return schema;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
