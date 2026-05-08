package cwchoiit.cleanecommerce.application.port.in;

import cwchoiit.cleanecommerce.domain.catalog.category.Category;

public interface CategoryQueryUseCase {
    Category findById(Long categoryId);

    boolean existsByName(String name);
}
