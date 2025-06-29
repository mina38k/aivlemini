package aivlemini.domain;

import aivlemini.domain.*;
import aivlemini.infra.AbstractEvent;
import java.time.LocalDate;
import java.util.*;

import javax.persistence.Lob;

import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class PublishPrepared extends AbstractEvent {

    private Long id;

    @Lob
    private String coverImagePath;

    @Lob
    private String summaryContent;

    private String bookName;

    @Lob
    private String pdfPath;

    private String category;

    private String authorId;
    
    // // ManuscriptId 속성을 찾을 수 없는 오류 -> 주석 이유
    //private ManuscriptId manuscriptId;
    private Boolean notifyStatus;
    
    private String authorName;

    public PublishPrepared(Publishing aggregate) {
        super(aggregate);
    }

    public PublishPrepared() {
        super();
    }
}
//>>> DDD / Domain Event
