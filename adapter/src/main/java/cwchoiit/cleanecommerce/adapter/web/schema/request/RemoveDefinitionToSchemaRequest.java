package cwchoiit.cleanecommerce.adapter.web.schema.request;

import jakarta.validation.constraints.NotNull;

public record RemoveDefinitionToSchemaRequest(
        @NotNull Long schemaId, @NotNull String attributeKey) {}
