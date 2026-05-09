package cwchoiit.cleanecommerce.adapter.web.schema;

import cwchoiit.cleanecommerce.adapter.web.schema.request.ProductAttributeSchemaRegisterRequest;
import cwchoiit.cleanecommerce.adapter.web.schema.response.ProductAttributeSchemaRegisterResponse;
import cwchoiit.cleanecommerce.application.port.in.ProductAttributeSchemaRegisterUseCase;
import cwchoiit.cleanecommerce.domain.catalog.schema.ProductAttributeSchema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/schema")
public class ProductAttributeSchemaController {

    private final ProductAttributeSchemaRegisterUseCase productAttributeSchemaRegisterUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductAttributeSchemaRegisterResponse register(
            @RequestBody @Valid ProductAttributeSchemaRegisterRequest request) {
        ProductAttributeSchema schema =
                productAttributeSchemaRegisterUseCase.register(
                        request.categoryId(), request.attributeDefinitions());
        return ProductAttributeSchemaRegisterResponse.from(schema);
    }
}
