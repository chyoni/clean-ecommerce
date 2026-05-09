package cwchoiit.cleanecommerce.domain;

import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeDefinitionPayload;
import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeType;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import java.util.List;

public class ProductAttributeSchemaFixture {

    public static ProductAttributeSchema create(Long categoryId) {
        AttributeDefinitionPayload screenSizeDef =
                new AttributeDefinitionPayload("screen_size", AttributeType.NUMBER, true, null);
        AttributeDefinitionPayload storageDef =
                new AttributeDefinitionPayload("storage", AttributeType.NUMBER, true, null);

        return ProductAttributeSchema.create(categoryId, List.of(screenSizeDef, storageDef));
    }
}
