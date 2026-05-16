package cwchoiit.cleanecommerce.adapter.web.product.request;

import cwchoiit.cleanecommerce.domain.catalog.product.SkuPayload;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProductAddSkuRequest(@NotNull @NotEmpty List<SkuPayload> skus) {}
