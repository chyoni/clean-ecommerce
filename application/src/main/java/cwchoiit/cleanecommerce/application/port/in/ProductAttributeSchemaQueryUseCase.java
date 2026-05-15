package cwchoiit.cleanecommerce.application.port.in;

import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;

public interface ProductAttributeSchemaQueryUseCase {
    ProductAttributeSchema findBySchemaId(Long schemaId);

    ProductAttributeSchema findByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);
}
