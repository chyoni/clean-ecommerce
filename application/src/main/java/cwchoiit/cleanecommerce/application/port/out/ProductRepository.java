package cwchoiit.cleanecommerce.application.port.out;

import cwchoiit.cleanecommerce.domain.product.Product;
import org.springframework.data.repository.Repository;

public interface ProductRepository extends Repository<Product, Long> {
    Product save(Product product);
}
