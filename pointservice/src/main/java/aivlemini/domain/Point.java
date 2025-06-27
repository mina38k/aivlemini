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
        //implement business logic here:

        /** Example 1:  new item 
        Point point = new Point();
        repository().save(point);

        RegisterPointGained registerPointGained = new RegisterPointGained(point);
        registerPointGained.publishAfterCommit();
        */

        /** Example 2:  finding and process
        

        repository().findById(userRegistered.get???()).ifPresent(point->{
            
            point // do something
            repository().save(point);

            RegisterPointGained registerPointGained = new RegisterPointGained(point);
            registerPointGained.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void decreasePoint(BookApply bookApply) {
        //implement business logic here:

        /** Example 1:  new item 
        Point point = new Point();
        repository().save(point);

        PointDecreased pointDecreased = new PointDecreased(point);
        pointDecreased.publishAfterCommit();
        OutOfPoint outOfPoint = new OutOfPoint(point);
        outOfPoint.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        // if bookApply.userInfoId exists, use it
        
        // ObjectMapper mapper = new ObjectMapper();
        // Map<Long, Object> applyingMap = mapper.convertValue(bookApply.getUserInfoId(), Map.class);

        repository().findById(bookApply.get???()).ifPresent(point->{
            
            point // do something
            repository().save(point);

            PointDecreased pointDecreased = new PointDecreased(point);
            pointDecreased.publishAfterCommit();
            OutOfPoint outOfPoint = new OutOfPoint(point);
            outOfPoint.publishAfterCommit();

         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
