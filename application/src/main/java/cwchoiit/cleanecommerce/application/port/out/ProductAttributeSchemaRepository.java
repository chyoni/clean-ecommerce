package cwchoiit.cleanecommerce.application.port.out;

import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ProductAttributeSchemaRepository extends Repository<ProductAttributeSchema, Long> {

    ProductAttributeSchema save(ProductAttributeSchema productAttributeSchema);

    Optional<ProductAttributeSchema> findByCategoryId(Long categoryId);

    Optional<ProductAttributeSchema> findBySchemaId(Long schemaId);
}
