package cwchoiit.cleanecommerce.application.port.out;

import cwchoiit.cleanecommerce.domain.product.schema.ProductAttributeSchema;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ProductAttributeSchemaRepository extends Repository<ProductAttributeSchema, Long> {

    Optional<ProductAttributeSchema> findByCategoryId(Long categoryId);
}
