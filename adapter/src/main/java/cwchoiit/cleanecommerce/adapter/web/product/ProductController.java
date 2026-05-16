package cwchoiit.cleanecommerce.adapter.web.product;

import cwchoiit.cleanecommerce.adapter.web.product.request.ProductAddSkuRequest;
import cwchoiit.cleanecommerce.adapter.web.product.response.IssueImageUploadUrlResponse;
import cwchoiit.cleanecommerce.adapter.web.product.response.ProductRegisterResponse;
import cwchoiit.cleanecommerce.adapter.web.product.response.ProductSkusResponse;
import cwchoiit.cleanecommerce.application.port.in.ProductRegisterUseCase;
import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlCommand;
import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlUseCase;
import cwchoiit.cleanecommerce.domain.catalog.product.Product;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductRegisterPayload;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductSku;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final IssueImageUploadUrlUseCase issueImageUploadUrlUseCase;
    private final ProductRegisterUseCase productRegisterUseCase;

    @PostMapping("/images/upload-url")
    @ResponseStatus(HttpStatus.CREATED)
    public IssueImageUploadUrlResponse issueImageUploadUrl(
            @RequestBody @Valid IssueImageUploadUrlCommand command) {
        return IssueImageUploadUrlResponse.from(issueImageUploadUrlUseCase.issue(command));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductRegisterResponse registerProduct(
            @RequestBody @Valid ProductRegisterPayload payload) {
        Product product = productRegisterUseCase.register(payload);
        return new ProductRegisterResponse(product.getProductId());
    }

    @PostMapping("/{productId}/skus")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductSkusResponse addSkus(
            @PathVariable("productId") Long productId,
            @RequestBody @Valid ProductAddSkuRequest request) {
        List<ProductSku> productSkus = productRegisterUseCase.addSkus(productId, request.skus());
        return ProductSkusResponse.from(productId, productSkus);
    }
}
