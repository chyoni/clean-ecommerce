package cwchoiit.cleanecommerce.application.port.out;

import cwchoiit.cleanecommerce.domain.catalog.product.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface ProductRepository extends Repository<Product, Long> {
    Product save(Product product);

    Optional<Product> findByProductId(Long productId);

    @Query(
            """
        SELECT DISTINCT p
        FROM Product p
        LEFT JOIN FETCH p.skus
        WHERE p.productId = :productId
    """)
    Optional<Product> findByProductIdWithSkus(Long productId);
}
