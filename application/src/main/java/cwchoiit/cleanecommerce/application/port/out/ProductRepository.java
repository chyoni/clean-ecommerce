package cwchoiit.cleanecommerce.application.port.out;

import cwchoiit.cleanecommerce.domain.catalog.product.Product;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface ProductRepository extends Repository<Product, Long> {
    Product save(Product product);

    Optional<Product> findByProductId(Long productId);
}
