package cwchoiit.cleanecommerce.adapter.web.schema;

import cwchoiit.cleanecommerce.adapter.web.schema.request.AddDefinitionToSchemaRequest;
import cwchoiit.cleanecommerce.adapter.web.schema.request.ProductAttributeSchemaRegisterRequest;
import cwchoiit.cleanecommerce.adapter.web.schema.request.RemoveDefinitionToSchemaRequest;
import cwchoiit.cleanecommerce.adapter.web.schema.response.ProductAttributeSchemaResponse;
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
    public ProductAttributeSchemaResponse register(
            @RequestBody @Valid ProductAttributeSchemaRegisterRequest request) {
        ProductAttributeSchema schema =
                productAttributeSchemaRegisterUseCase.register(
                        request.categoryId(), request.attributeDefinitions());
        return ProductAttributeSchemaResponse.from(schema);
    }

    @PostMapping("/definitions")
    @ResponseStatus(HttpStatus.OK)
    public ProductAttributeSchemaResponse addDefinition(
            @RequestBody @Valid AddDefinitionToSchemaRequest request) {
        ProductAttributeSchema schema =
                productAttributeSchemaRegisterUseCase.addDefinition(
                        request.schemaId(), request.attributeDefinitions());
        return ProductAttributeSchemaResponse.from(schema);
    }

    @DeleteMapping("/definitions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDefinition(@RequestBody @Valid RemoveDefinitionToSchemaRequest request) {
        productAttributeSchemaRegisterUseCase.removeDefinition(
                request.schemaId(), request.attributeKey());
    }
}
