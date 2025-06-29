package aivlemini.domain;

import aivlemini.PointserviceApplication;
import aivlemini.domain.OutOfPoint;
import aivlemini.domain.PointBought;
import aivlemini.domain.PointDecreased;
import aivlemini.domain.RegisterPointGained;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Point_table")
@Data
//<<< DDD / Aggregate Root
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer point;

    @Embedded
    private ApplyingId applyingId;

    @Embedded
    private UserId userId;

    public static PointRepository repository() {
        PointRepository pointRepository = PointserviceApplication.applicationContext.getBean(
            PointRepository.class
        );
        return pointRepository;
    }

    //<<< Clean Arch / Port Method
    public static void gainRegisterPoint(UserRegistered userRegistered) {
        // userName 유효성 검사
        if (userRegistered.getUserName() == null){
            return;
        }

        // 포인트 객체 생성 
        Point point = new Point();
        
        // 포인트 지급 로직
        if (userRegistered.getuserName().toLowerCase().startsWith("kt-")){
            point.setPoint(6000); // userName이 "kt-" 인 경우 kt 고객으로 간주
        } else {
            point.setPoint(1000); // kt 고객이 아닌 경우 기본 포인트 지급
        }
        
        // UserId 맵핑
        UserId userId = new UserId(userRegistered.getId()); // 회원가입 때 입력(할당)된 Id와 맵핑
        point.setUserId(userId);

        // point 객체 저장
        repository().save(point);

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void decreasePoint(BookApply bookApply) {
        // UserId 로 포인트 조회
        UserId userId = new UserId(bookApply.getUserId());
        Point point = repository().findByUserId(userId);

        if (point == null) {
            System.out.println("포인트 정보 없음");
            return;
        }
        // 구독 여부 판단
        if (Boolean.TRUE.equals(bookApply.getIsPurchase())) {
            System.out.println("열람 성공");
            return;
        }
        // 포인트 부족 여부 판단
        if (point.getPoint() < bookApply.getPrice()) { // TODO. 열람 aggregate 에서 열람한 도서 aggregate의 도서 가격을 받아와야 함. 
            OutOfPoint event = new OutOfPoint(point);
            event.publishAfterCommit();
            return; // 포인트 차감되지 않음.
        }

        // 포인트 차감
        point.setPoint(point.getPoint() - bookApply.getPrice()); // TODO. 위와 동일
        repository().save(point);
        System.out.println("열람 성공");

    }

    // 포인트 구매 Command
    public void buyPoint(BuyPointCommand cmd) {
    this.point += cmd.getAmount(); // 포인트 증가
    }
}
//>>> DDD / Aggregate Root
