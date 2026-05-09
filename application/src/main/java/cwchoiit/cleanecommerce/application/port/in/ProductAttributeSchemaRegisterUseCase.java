package cwchoiit.cleanecommerce.application.port.in;

import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeDefinitionPayload;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import jakarta.validation.Valid;
import java.util.List;

public interface ProductAttributeSchemaRegisterUseCase {
    ProductAttributeSchema register(
            Long categoryId, @Valid List<AttributeDefinitionPayload> payload);
}
