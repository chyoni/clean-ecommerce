package cwchoiit.cleanecommerce.application.product;

import cwchoiit.cleanecommerce.application.port.in.ProductRegisterUseCase;
import cwchoiit.cleanecommerce.application.port.out.CategoryRepository;
import cwchoiit.cleanecommerce.application.port.out.MemberRepository;
import cwchoiit.cleanecommerce.application.port.out.ProductAttributeSchemaRepository;
import cwchoiit.cleanecommerce.application.port.out.ProductRepository;
import cwchoiit.cleanecommerce.domain.member.Member;
import cwchoiit.cleanecommerce.domain.product.Product;
import cwchoiit.cleanecommerce.domain.product.ProductRegisterPayload;
import cwchoiit.cleanecommerce.domain.product.category.Category;
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
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
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

        Category category =
                categoryRepository
                        .findByCategoryId(payload.categoryId())
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "존재하지 않는 카테고리입니다: " + payload.categoryId()));

        productAttributeSchemaRepository
                .findByCategoryId(payload.categoryId())
                .ifPresent(schema -> schema.validate(payload.attributes()));

        return productRepository.save(Product.register(payload, seller, category));
    }
}
