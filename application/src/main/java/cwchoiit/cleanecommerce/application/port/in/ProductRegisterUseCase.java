package cwchoiit.cleanecommerce.application.port.in;

import cwchoiit.cleanecommerce.domain.catalog.product.Product;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductRegisterPayload;
import jakarta.validation.Valid;

public interface ProductRegisterUseCase {
    Product register(@Valid ProductRegisterPayload payload);
}
