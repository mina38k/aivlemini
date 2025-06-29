package aivlemini.domain;

import aivlemini.domain.*;
import aivlemini.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PublicationRequested extends AbstractEvent {

    private Long id;
    private String date;
    private String contents;
    //private AuthorId authorId;
    private String authorId;
    private String title;
    private String status;
    private String authorName;

    public PublicationRequested(Manuscript aggregate) {
        super(aggregate);
    }

    public PublicationRequested() {
        super();
    }
}
//>>> DDD / Domain Event
