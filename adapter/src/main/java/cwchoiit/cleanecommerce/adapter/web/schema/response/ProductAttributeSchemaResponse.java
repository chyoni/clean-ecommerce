package cwchoiit.cleanecommerce.adapter.web.schema.response;

import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeDefinition;
import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeType;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import java.util.List;

public record ProductAttributeSchemaResponse(
        Long schemaId, Long categoryId, List<AttributeDefinitionResponse> attributeDefinitions) {

    public static ProductAttributeSchemaResponse from(ProductAttributeSchema schema) {
        return new ProductAttributeSchemaResponse(
                schema.getSchemaId(),
                schema.getCategoryId(),
                schema.getDefinitions().stream().map(AttributeDefinitionResponse::from).toList());
    }

    private record AttributeDefinitionResponse(
            Long definitionId,
            String attributeKey,
            AttributeType attributeType,
            boolean required,
            List<String> allowedValues) {

        private static AttributeDefinitionResponse from(AttributeDefinition definitions) {
            return new AttributeDefinitionResponse(
                    definitions.getDefinitionId(),
                    definitions.getAttributeKey(),
                    definitions.getAttributeType(),
                    definitions.isRequired(),
                    definitions.getAllowedValues());
        }
    }
}
