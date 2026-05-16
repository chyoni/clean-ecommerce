package cwchoiit.cleanecommerce.application.catalog.product;

import cwchoiit.cleanecommerce.application.port.in.CategoryQueryUseCase;
import cwchoiit.cleanecommerce.application.port.in.ProductRegisterUseCase;
import cwchoiit.cleanecommerce.application.port.out.MemberRepository;
import cwchoiit.cleanecommerce.application.port.out.ProductAttributeSchemaRepository;
import cwchoiit.cleanecommerce.application.port.out.ProductRepository;
import cwchoiit.cleanecommerce.domain.catalog.category.Category;
import cwchoiit.cleanecommerce.domain.catalog.product.Product;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductRegisterPayload;
import cwchoiit.cleanecommerce.domain.catalog.product.ProductSku;
import cwchoiit.cleanecommerce.domain.catalog.product.SkuPayload;
import cwchoiit.cleanecommerce.domain.member.Member;
import jakarta.validation.Valid;
import java.util.List;
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
    private final MemberRepository memberRepository;
    private final CategoryQueryUseCase categoryQueryUseCase;
    private final ProductAttributeSchemaRepository productAttributeSchemaRepository;

    @Override
    public Product register(@Valid ProductRegisterPayload payload) {
        Member seller =
                memberRepository
                        .findByMemberId(payload.sellerId())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "존재하지 않는 판매자입니다: " + payload.sellerId()));

        Category category = categoryQueryUseCase.findById(payload.categoryId());

        productAttributeSchemaRepository
                .findByCategoryId(payload.categoryId())
                .ifPresent(schema -> schema.validate(payload.attributes()));

        return productRepository.save(Product.register(payload, seller, category));
    }

    @Override
    public List<ProductSku> addSkus(Long productId, @Valid List<SkuPayload> skus) {
        Product product = productRepository.findByProductId(productId).orElseThrow();

        List<ProductSku> updatedSkus = product.registerSkus(skus);

        productRepository.save(product);

        return updatedSkus;
    }
}
