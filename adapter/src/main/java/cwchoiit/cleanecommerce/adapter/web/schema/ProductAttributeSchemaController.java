package cwchoiit.cleanecommerce.adapter.web.schema;

import cwchoiit.cleanecommerce.adapter.web.schema.request.ProductAttributeSchemaRegisterRequest;
import cwchoiit.cleanecommerce.adapter.web.schema.response.ProductAttributeSchemaRegisterResponse;
import cwchoiit.cleanecommerce.application.port.in.ProductAttributeSchemaRegisterUseCase;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/schema")
public class ProductAttributeSchemaController {

    private final ProductAttributeSchemaRegisterUseCase productAttributeSchemaRegisterUseCase;

    @PostMapping
    public ProductAttributeSchemaRegisterResponse register(
            @RequestBody @Valid ProductAttributeSchemaRegisterRequest request) {
        ProductAttributeSchema schema =
                productAttributeSchemaRegisterUseCase.register(
                        request.categoryId(), request.attributeDefinitions());
        return ProductAttributeSchemaRegisterResponse.from(schema);
    }
}
