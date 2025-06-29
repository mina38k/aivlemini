package aivlemini.domain;

import aivlemini.domain.*;
import aivlemini.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class ManuscriptEdited extends AbstractEvent {

    private Long id;
    private String contents;
    private String title;
    private String date;
    //private AuthorId authorId;
    private String authorId;
    private String status;
    private String authorName;

    public ManuscriptEdited(Manuscript aggregate) {
        super(aggregate);
    }

    public ManuscriptEdited() {
        super();
    }
}
//>>> DDD / Domain Event
