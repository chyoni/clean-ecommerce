package cwchoiit.cleanecommerce.domain;

import cwchoiit.cleanecommerce.domain.product.schema.AttributeType;
import cwchoiit.cleanecommerce.domain.product.schema.ProductAttributeSchema;

public class ProductAttributeSchemaFixture {

    public static ProductAttributeSchema create(Long categoryId) {
        ProductAttributeSchema schema = ProductAttributeSchema.create(categoryId);
        schema.addDefinition("screen_size", AttributeType.NUMBER, true, null);
        schema.addDefinition("storage", AttributeType.NUMBER, true, null);
        return schema;
    }
}
