package cwchoiit.cleanecommerce.application.port.out;

import cwchoiit.cleanecommerce.domain.product.category.Category;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface CategoryRepository extends Repository<Category, Long> {

    Optional<Category> findByCategoryId(Long categoryId);
}
