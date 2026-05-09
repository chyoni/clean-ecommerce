package cwchoiit.cleanecommerce.domain.catalog.schema;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AttributeDefinitionPayload(
        @NotNull String attributeKey,
        @NotNull AttributeType attributeType,
        boolean isRequired,
        List<String> allowedValues) {}
