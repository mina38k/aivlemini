package aivlemini.domain;

import aivlemini.MenuscriptserviceApplication;
import aivlemini.domain.ManuscriptEdited;
import aivlemini.domain.SavedManuscript;
import aivlemini.domain.TemporarilySaved;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Manuscript_table")
@Data
//<<< DDD / Aggregate Root
public class Manuscript {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String date; // 추가

    private String contents;

    private String title;

    private String status;

    private String authorName;

    // @Embedded // 얘를 주석하지 않으면, hibernate 오류 발생
    // private AuthorId authorId;
    private String authorId;

    @PostPersist
    public void onPostPersist() {
        SavedManuscript savedManuscript = new SavedManuscript(this);
        savedManuscript.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        TemporarilySaved temporarilySaved = new TemporarilySaved(this);
        temporarilySaved.publishAfterCommit();
    }

    @PreUpdate
    public void onPreUpdate() {
        ManuscriptEdited manuscriptEdited = new ManuscriptEdited(this);
        manuscriptEdited.publishAfterCommit();
    }

    public static ManuscriptRepository repository() {
        ManuscriptRepository manuscriptRepository = MenuscriptserviceApplication.applicationContext.getBean(
            ManuscriptRepository.class
        );
        return manuscriptRepository;
    }

    //<<< Clean Arch / Port Method
    public void requestPublish(RequestPublishCommand requestPublishCommand) {
        //implement business logic here:

        PublicationRequested publicationRequested = new PublicationRequested(
            this
        );
        publicationRequested.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method

    //<<< Clean Arch / Port Method
    public static void notifyStatus(PublishPrepared publishPrepared) {
        //implement business logic here:

        //Example 1:  new item 
        // Manuscript manuscript = new Manuscript();
        // repository().save(manuscript);

        /** Example 2:  finding and process
        
        // if publishPrepared.gptIdmanuscriptId exists, use it
        
        // ObjectMapper mapper = new ObjectMapper();
        // Map<, Object> publishingMap = mapper.convertValue(publishPrepared.getGptId(), Map.class);
        // Map<Long, Object> publishingMap = mapper.convertValue(publishPrepared.getManuscriptId(), Map.class);

        repository().findById(publishPrepared.get???()).ifPresent(manuscript->{
            
            manuscript // do something
            repository().save(manuscript);


         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
