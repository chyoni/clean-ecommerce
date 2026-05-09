package cwchoiit.cleanecommerce.application.catalog.schema;

import cwchoiit.cleanecommerce.application.port.in.CategoryQueryUseCase;
import cwchoiit.cleanecommerce.application.port.in.ProductAttributeSchemaRegisterUseCase;
import cwchoiit.cleanecommerce.application.port.out.ProductAttributeSchemaRepository;
import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeDefinitionPayload;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class ProductAttributeSchemaRegisterService
        implements ProductAttributeSchemaRegisterUseCase {

    private final ProductAttributeSchemaRepository productAttributeSchemaRepository;
    private final CategoryQueryUseCase categoryQueryUseCase;

    @Override
    public ProductAttributeSchema register(
            Long categoryId, @Valid List<AttributeDefinitionPayload> payload) {
        Category category = categoryQueryUseCase.findById(categoryId);

        checkDuplicateSchema(category.getCategoryId());

        ProductAttributeSchema schema = ProductAttributeSchema.create(categoryId, payload);

        return productAttributeSchemaRepository.save(schema);
    }

    private void checkDuplicateSchema(Long categoryId) {
        if (productAttributeSchemaRepository.findByCategoryId(categoryId).isPresent()) {
            throw new IllegalArgumentException("같은 카테고리에 대한 속성 스키마 정의가 이미 존재합니다");
        }
    }
}
