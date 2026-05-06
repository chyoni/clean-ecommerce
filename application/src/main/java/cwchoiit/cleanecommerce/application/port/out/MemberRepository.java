package cwchoiit.cleanecommerce.application.port.out;

import cwchoiit.cleanecommerce.domain.member.Member;
import java.util.Optional;
import org.springframework.data.repository.Repository;

public interface MemberRepository extends Repository<Member, Long> {

    Optional<Member> findByMemberId(Long memberId);
}
