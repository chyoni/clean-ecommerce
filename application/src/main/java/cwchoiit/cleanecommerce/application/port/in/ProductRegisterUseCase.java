package cwchoiit.cleanecommerce.application.port.in;

import cwchoiit.cleanecommerce.domain.product.Product;
import cwchoiit.cleanecommerce.domain.product.ProductRegisterPayload;
import jakarta.validation.Valid;

public interface ProductRegisterUseCase {
    Product register(@Valid ProductRegisterPayload payload);
}
