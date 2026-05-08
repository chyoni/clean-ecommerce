package cwchoiit.cleanecommerce.adapter.web.category.response;

import cwchoiit.cleanecommerce.domain.catalog.category.Category;

public record CategoryCreateResponse(Long categoryId, String name, Long parentCategoryId) {

    public static CategoryCreateResponse from(Category category) {
        return new CategoryCreateResponse(
                category.getCategoryId(), category.getName(), category.getParent().getCategoryId());
    }
}
