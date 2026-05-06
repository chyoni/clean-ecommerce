package cwchoiit.cleanecommerce.domain.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductStatus {
    DRAFT("작성중"),
    PENDING_REVIEW("심사대기"),
    AVAILABLE("판매중"),
    OUT_OF_STOCK("품절"),
    DISCONTINUED("판매종료"),
    ;

    private final String description;
}
