package aivlemini.domain;

import aivlemini.domain.*;
import aivlemini.infra.AbstractEvent;
import java.util.*;
import lombok.*;

@Data
@ToString
public class PublishPrepared extends AbstractEvent {

    private Long id;
    private String coverImagePath;
    private String summaryContent;
    private String bookName;
    private String pdfPath;
    private String category;
    private String authorId;
    //private Object manuscriptId; // ai서비스에서 속성 없앰
    private Boolean notifyStatus;
    private String authorName;
}
