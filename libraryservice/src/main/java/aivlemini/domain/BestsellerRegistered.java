package aivlemini.domain;

import aivlemini.domain.*;
import aivlemini.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;
import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class BestsellerRegistered extends AbstractEvent {

    private Long id;
    private String bookName;
    private Boolean isBestSeller;
    private String authorName;
    private String image;
    private String bookContent;
    private String authorId;
    private Integer subscriptionCount;
    private String category;
    private String pdfPath;

    public BestsellerRegistered(Book aggregate) {
        super(aggregate);
        this.id = aggregate.getId();
        this.bookName = aggregate.getBookName();
        this.isBestSeller = aggregate.getIsBestSeller();
        this.authorName = aggregate.getAuthorName();
        this.image = aggregate.getImage();
        this.bookContent = aggregate.getBookContent();
        this.authorId = aggregate.getAuthorId();
        this.subscriptionCount = aggregate.getSubscriptionCount();
        this.category = aggregate.getCategory();
        this.pdfPath = aggregate.getPdfPath();
    }

    public BestsellerRegistered() {
        super();
    }
}
//>>> DDD / Domain Event
