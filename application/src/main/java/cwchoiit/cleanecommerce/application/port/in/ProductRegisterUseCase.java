package cwchoiit.cleanecommerce.application.port.in;

import cwchoiit.cleanecommerce.domain.catalog.product.Product;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductRegisterPayload;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductSku;
import cwchoiit.cleanecommerce.domain.catalog.product.SkuPayload;
import jakarta.validation.Valid;
import java.util.List;

public interface ProductRegisterUseCase {
    Product register(@Valid ProductRegisterPayload payload);

    List<ProductSku> addSkus(Long productId, @Valid List<SkuPayload> skus);
}
