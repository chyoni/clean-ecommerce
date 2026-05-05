package cwchoiit.cleanecommerce.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
    AVAILABLE("판매중"),
    OUT_OF_STOCK("품절"),
    DISCONTINUED("판매종료"),
    ;

    private final String description;
}
