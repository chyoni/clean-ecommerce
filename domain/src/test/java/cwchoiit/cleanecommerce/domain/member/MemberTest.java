package cwchoiit.cleanecommerce.domain.member;

import static cwchoiit.cleanecommerce.domain.member.MemberRole.ADMIN;
import static cwchoiit.cleanecommerce.domain.member.MemberRole.NORMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import cwchoiit.cleanecommerce.domain.MemberFixture;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class MemberTest {

    @Test
    @DisplayName("회원을 등록한다")
    void register() {
        MemberRegisterPayload payload = MemberFixture.getMemberRegisterPayload();

        Member member = Member.register(payload, MemberFixture.getFakePasswordEncoder());

        assertThat(member.getEmail()).isNotNull();
    }

    @Test
    @DisplayName("유효하지 않은 핸드폰번호를 입력하면 오류가 발생한다")
    void registerFailPhoneNumber() {
        MemberRegisterPayload payload = MemberFixture.builder().phoneNumber("1234").build();

        assertThatThrownBy(() -> MemberFixture.register(payload))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("유효하지 않은 이메일을 입력하면 오류가 발생한다")
    void registerFailEmail() {
        MemberRegisterPayload payload = MemberFixture.builder().email("invalid").build();

        assertThatThrownBy(() -> MemberFixture.register(payload))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 등록 시 회원 유형을 입력받지 않으면 기본으로 NORMAL로 지정된다")
    void registerDefaultMemberRole() {
        MemberRegisterPayload payload = MemberFixture.builder().role(null).build();

        Member member = MemberFixture.register(payload);

        assertThat(member.getRole()).isEqualTo(NORMAL);
    }

    @Test
    @DisplayName("회원의 이메일을 변경한다")
    void changeEmail() {
        Member member = MemberFixture.register(MemberFixture.getMemberRegisterPayload());

        String newEmail = "noreply2@example.com";
        member.changeEmail(newEmail);

        assertThat(member.getEmail().address()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("회원 이메일 변경 시 NULL값을 허용하지 않는다")
    void changeEmailFailNull() {
        Member member = MemberFixture.register(MemberFixture.getMemberRegisterPayload());

        assertThatThrownBy(() -> member.changeEmail(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("회원 이메일 변경 시 유효하지 않은 형식인 경우 오류가 발생한다")
    void changeEmailFailInvalid() {
        Member member = MemberFixture.register(MemberFixture.getMemberRegisterPayload());

        assertThatThrownBy(() -> member.changeEmail("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원의 핸드폰번호를 변경한다")
    void changePhoneNumber() {
        Member member = MemberFixture.register(MemberFixture.getMemberRegisterPayload());

        String newPhoneNumber = "010-1111-2222";
        member.changePhoneNumber(newPhoneNumber);

        assertThat(member.getPhoneNumber().value()).isEqualTo(newPhoneNumber);
    }

    @Test
    @DisplayName("회원 핸드폰번호 변경 시 NULL값을 허용하지 않는다")
    void changePhoneNumberFailNull() {
        Member member = MemberFixture.register(MemberFixture.getMemberRegisterPayload());

        assertThatThrownBy(() -> member.changePhoneNumber(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("회원 핸드폰번호 변경 시 유효하지 않은 형식인 경우 오류가 발생한다")
    void changePhoneNumberFailInvalid() {
        Member member = MemberFixture.register(MemberFixture.getMemberRegisterPayload());

        assertThatThrownBy(() -> member.changePhoneNumber("0101"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 유형을 변경한다")
    void changeMemberRole() {
        Member member = MemberFixture.register(MemberFixture.getMemberRegisterPayload());

        member.changeMemberRole(NORMAL);

        assertThat(member.getRole()).isEqualTo(NORMAL);
    }

    @Test
    @DisplayName("관리자 계정이 아닌 유저가 관리자로 역할을 변경하려고 시도하면 오류가 발생한다")
    void validateNonAdmin() {
        Member normalMember = MemberFixture.register(MemberFixture.getMemberRegisterPayload());

        assertThatThrownBy(() -> normalMember.changeMemberRole(ADMIN))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("관리자 계정의 유저는 관리자 역할로 변경이 가능하다")
    void validateAdmin() {
        Member adminMember = MemberFixture.register(MemberFixture.builder().role(ADMIN).build());

        adminMember.changeMemberRole(ADMIN);

        assertThat(adminMember.getRole()).isEqualTo(ADMIN);
    }

    @ParameterizedTest(name = "{1} 이 null이면 등록에 실패한다")
    @MethodSource("nullRequiredFieldPayloads")
    @DisplayName("필수 필드가 null이면 등록에 실패한다")
    void registerValidation(MemberRegisterPayload payload, String field) {
        assertThatThrownBy(() -> Member.register(payload, MemberFixture.getFakePasswordEncoder()))
                .isInstanceOf(NullPointerException.class);
    }

    private static Stream<Arguments> nullRequiredFieldPayloads() {
        return Stream.of(
                Arguments.of(MemberFixture.builder().name(null).build(), "name"),
                Arguments.of(MemberFixture.builder().rawPassword(null).build(), "rawPassword"),
                Arguments.of(MemberFixture.builder().email(null).build(), "email"),
                Arguments.of(MemberFixture.builder().phoneNumber(null).build(), "phoneNumber"));
    }
}
