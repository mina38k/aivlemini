package aivlemini.domain;

import aivlemini.domain.*;
import aivlemini.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class TemporarilySaved extends AbstractEvent {

    private Long id;
    private String date;
    private String contents;
    // private AuthorId authorId;
    private String authorId;
    private String title;
    private String status;
    private String authorName;

    public TemporarilySaved(Manuscript aggregate) {
        super(aggregate);
    }

    public TemporarilySaved() {
        super();
    }
}
//>>> DDD / Domain Event
