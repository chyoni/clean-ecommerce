package cwchoiit.cleanecommerce.adapter.web.product.request;

import jakarta.validation.constraints.NotNull;

public record ProductDeactivateSkuRequest(@NotNull String skuCode) {}
