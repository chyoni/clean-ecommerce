package cwchoiit.cleanecommerce.domain;

import cwchoiit.cleanecommerce.domain.member.Member;
import cwchoiit.cleanecommerce.domain.member.MemberRegisterPayload;
import cwchoiit.cleanecommerce.domain.member.MemberRole;
import cwchoiit.cleanecommerce.domain.member.PasswordEncoder;
import org.jspecify.annotations.NonNull;

public class MemberFixture {

    public static @NonNull Member register(MemberRegisterPayload payload) {
        return Member.register(payload, getFakePasswordEncoder());
    }

    public static @NonNull PasswordEncoder getFakePasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(String rawPassword) {
                return rawPassword.toUpperCase();
            }

            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return encode(rawPassword).equals(encodedPassword);
            }
        };
    }

    public static @NonNull MemberRegisterPayload getMemberRegisterPayload() {
        return new Builder().build();
    }

    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name = "최치원";
        private String rawPassword = "Secret123";
        private String email = "noreply@example.com";
        private String phoneNumber = "010-1234-5678";
        private MemberRole role = MemberRole.SELLER;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder rawPassword(String rawPassword) {
            this.rawPassword = rawPassword;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder role(MemberRole role) {
            this.role = role;
            return this;
        }

        public MemberRegisterPayload build() {
            return new MemberRegisterPayload(name, rawPassword, email, phoneNumber, role);
        }
    }
}
