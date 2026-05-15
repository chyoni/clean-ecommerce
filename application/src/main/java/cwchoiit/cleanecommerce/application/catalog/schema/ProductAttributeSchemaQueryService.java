package cwchoiit.cleanecommerce.application.catalog.schema;

import cwchoiit.cleanecommerce.application.port.in.ProductAttributeSchemaQueryUseCase;
import cwchoiit.cleanecommerce.application.port.out.ProductAttributeSchemaRepository;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductAttributeSchemaQueryService implements ProductAttributeSchemaQueryUseCase {

    private final ProductAttributeSchemaRepository productAttributeSchemaRepository;

    @Override
    public ProductAttributeSchema findBySchemaId(Long schemaId) {
        return productAttributeSchemaRepository.findBySchemaId(schemaId).orElseThrow();
    }

    @Override
    public ProductAttributeSchema findByCategoryId(Long categoryId) {
        return productAttributeSchemaRepository.findByCategoryId(categoryId).orElseThrow();
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        return productAttributeSchemaRepository.findByCategoryId(categoryId).isPresent();
    }
}
