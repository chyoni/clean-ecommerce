package cwchoiit.cleanecommerce.domain.member.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;

@Embeddable
public record PhoneNumber(@Column(name = "phone_number") String value) {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^01[016789]-?\\d{3,4}-?\\d{4}$");

    public PhoneNumber {
        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("유효하지 않은 핸드폰 번호입니다");
        }
    }
}
