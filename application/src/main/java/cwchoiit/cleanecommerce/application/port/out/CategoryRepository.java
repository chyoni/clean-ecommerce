package cwchoiit.cleanecommerce.application.port.out;

import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface CategoryRepository extends Repository<Category, Long> {
    Category save(Category category);

    Optional<Category> findByCategoryId(Long categoryId);

    Optional<Category> findByName(String name);
}
