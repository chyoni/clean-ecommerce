package cwchoiit.cleanecommerce.application.port.in;

import cwchoiit.cleanecommerce.domain.catalog.category.Category;

public interface CategoryCreateUseCase {
    Category create(String name, Long parentCategoryId);
}
