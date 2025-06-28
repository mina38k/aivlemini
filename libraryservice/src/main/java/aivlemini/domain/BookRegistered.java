package aivlemini.domain;

import aivlemini.domain.*;
import aivlemini.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class BookRegistered extends AbstractEvent {

    private Long id;
    private String bookName;
    private String category;
    private Boolean isBestSeller;
    private String authorName;
    private String image;
    private String bookContent;
    private String authorId;
    private Integer subscriptionCount;
    private String pdfPath;
    // private AuthorId authorId;

    public BookRegistered(Book aggregate) {
        super(aggregate);
    }

    public BookRegistered() {
        super();
    }
}
//>>> DDD / Domain Event
