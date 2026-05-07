package cwchoiit.cleanecommerce.domain.member;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.util.Assert.state;

import cwchoiit.cleanecommerce.domain.BaseEntity;
import cwchoiit.cleanecommerce.domain.member.vo.Email;
import cwchoiit.cleanecommerce.domain.member.vo.PhoneNumber;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String name;
    private String encodedPassword;

    @Embedded private Email email;

    @Embedded private PhoneNumber phoneNumber;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    public static Member register(MemberRegisterPayload payload, PasswordEncoder passwordEncoder) {
        Member member = new Member();

        member.name = requireNonNull(payload.name());
        member.encodedPassword = passwordEncoder.encode(requireNonNull(payload.rawPassword()));
        member.email = new Email(requireNonNull(payload.email()));
        member.phoneNumber = new PhoneNumber(requireNonNull(payload.phoneNumber()));
        member.role = requireNonNullElse(payload.role(), MemberRole.NORMAL);

        return member;
    }

    public void changeEmail(String email) {
        this.email = new Email(requireNonNull(email));
    }

    public void changePhoneNumber(String phoneNumber) {
        this.phoneNumber = new PhoneNumber(requireNonNull(phoneNumber));
    }

    public void changeMemberRole(MemberRole role) {
        validateAdminRole(role);

        this.role = requireNonNull(role);
    }

    public boolean isSeller() {
        return role == MemberRole.SELLER;
    }

    private void validateAdminRole(MemberRole role) {
        state(role != MemberRole.ADMIN || this.role == MemberRole.ADMIN, "잘못된 접근입니다");
    }
}
