package cwchoiit.cleanecommerce.application.product;

import cwchoiit.cleanecommerce.application.port.in.ProductRegisterUseCase;
import cwchoiit.cleanecommerce.application.port.out.ProductRepository;
import cwchoiit.cleanecommerce.domain.product.Product;
import cwchoiit.cleanecommerce.domain.product.ProductRegisterPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class ProductRegisterService implements ProductRegisterUseCase {

    private final ProductRepository productRepository;

    @Override
    public Product register(@Valid ProductRegisterPayload payload) {
        Product product = Product.register(payload);

        return productRepository.save(product);
    }
}
