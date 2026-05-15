package cwchoiit.cleanecommerce.adapter.web.schema.request;

import cwchoiit.cleanecommerce.domain.catalog.schema.AttributeDefinitionPayload;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AddDefinitionToSchemaRequest(
        @NotNull Long schemaId, @NotEmpty List<AttributeDefinitionPayload> attributeDefinitions) {}
