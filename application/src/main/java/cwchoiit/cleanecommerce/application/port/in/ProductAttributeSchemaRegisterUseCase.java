package cwchoiit.cleanecommerce.application.port.in;

import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeDefinitionPayload;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;

import java.util.List;

public interface ProductAttributeSchemaRegisterUseCase {
    ProductAttributeSchema register(Long categoryId, List<AttributeDefinitionPayload> payload);
}
