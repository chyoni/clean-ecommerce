package cwchoiit.cleanecommerce.adapter.web.product;

import cwchoiit.cleanecommerce.adapter.web.product.response.IssueImageUploadUrlResponse;
import cwchoiit.cleanecommerce.adapter.web.product.response.ProductRegisterResponse;
import cwchoiit.cleanecommerce.application.port.in.ProductRegisterUseCase;
import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlCommand;
import cwchoiit.cleanecommerce.application.port.in.images.IssueImageUploadUrlUseCase;
import cwchoiit.cleanecommerce.domain.product.Product;
import cwchoiit.cleanecommerce.domain.product.ProductRegisterPayload;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
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
}
