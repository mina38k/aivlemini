package aivlemini.domain;

import javax.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserId {
    // 회원관리 BC의 User 를 식별하기 위한 식별자. DDD 원칙에 충실한 방법으로 도메인 명시성이 높음.
    private Long value;
}