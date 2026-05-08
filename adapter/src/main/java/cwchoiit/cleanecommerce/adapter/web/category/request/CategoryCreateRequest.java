package cwchoiit.cleanecommerce.adapter.web.category.request;

import jakarta.validation.constraints.NotNull;

public record CategoryCreateRequest(@NotNull String name, Long parentCategoryId) {}
