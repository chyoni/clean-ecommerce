package cwchoiit.cleanecommerce.application.port.in;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import cwchoiit.cleanecommerce.application.port.out.ProductRepository;
import cwchoiit.cleanecommerce.application.product.ProductRegisterService;
import cwchoiit.cleanecommerce.domain.ProductFixture;
import cwchoiit.cleanecommerce.domain.product.Product;
import cwchoiit.cleanecommerce.domain.product.ProductRegisterPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductRegisterUseCaseTest {

    @Mock ProductRepository productRepository;

    ProductRegisterUseCase productRegisterUseCase;

    @BeforeEach
    void setUp() {
        productRegisterUseCase = new ProductRegisterService(productRepository);
    }

    @Test
    @DisplayName("상품을 등록한다")
    void register() {
        ProductRegisterPayload payload = ProductFixture.getProductRegisterPayload();

        productRegisterUseCase.register(payload);

        verify(productRepository, times(1)).save(any(Product.class));
    }
}
