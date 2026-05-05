package cwchoiit.cleanecommerce.domain.member;

public record MemberRegisterPayload(
        String name, String rawPassword, String email, String phoneNumber, MemberRole role) {}
