package cwchoiit.cleanecommerce.domain.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {
    SELLER("판매자"),
    NORMAL("일반 유저"),
    ADMIN("관리자"),
    ;

    private final String description;
}
