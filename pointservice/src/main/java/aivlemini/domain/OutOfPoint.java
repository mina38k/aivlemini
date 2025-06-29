package aivlemini.domain;

import aivlemini.domain.*;
import aivlemini.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class OutOfPoint extends AbstractEvent {

    private Long id;
    private Integer point;
    private ApplyingId applyingId;
    private UserId userId;

    public OutOfPoint(Point aggregate) {
        super(aggregate);
        this.id = aggregate.getId();
        this.point = aggregate.getPoint();
        this.applyingId = aggregate.getApplyingId();
        this.userId = aggregate.getUserId();
    }

    public OutOfPoint() {
        super();
    }
}
//>>> DDD / Domain Event
