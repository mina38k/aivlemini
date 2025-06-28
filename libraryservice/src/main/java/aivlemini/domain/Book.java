package aivlemini.domain;

import aivlemini.LibraryserviceApplication;
import aivlemini.domain.BestsellerRegistered;
import aivlemini.domain.BookRegistered;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Book_table")
@Data
//<<< DDD / Aggregate Root
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String bookName;

    private String category;

    private Boolean isBestSeller;

    private String authorName;

    private String image;

    private Integer subscriptionCount;

    private String bookContent;

    private String pdfPath;

    public static BookRepository repository() {
        BookRepository bookRepository = LibraryserviceApplication.applicationContext.getBean(
            BookRepository.class
        );
        return bookRepository;
    }

    //<<< Clean Arch / Port Method
    public static void registerBook(PublishPrepared publishPrepared) {
        Book book = new Book();
        book.setBookName(publishPrepared.getBookName());
        book.setCategory(publishPrepared.getCategory());
        book.setIsBestSeller(false);
        book.setAuthorName(publishPrepared.getAuthorName());
        book.setImage(publishPrepared.getCoverImagePath());
        book.setSubscriptionCount(0);
        book.setBookContent(publishPrepared.getSummaryContent());
        book.setPdfPath(publishPrepared.getPdfPath());
        book.setAuthorId(publishPrepared.getAuthorId());

        // Sample Logic //
        Book.registerBook.save(book);

        BookRegistered event = new BookRegistered(book);
        event.publishAfterCommit();

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void grantBestseller(BookApply bookApply) {
        Long bookId = Long.valueOf(bookApply.getBookId().toString());

        repository().findById(bookId).ifPresent(book -> {
        
            if (book.getSubscriptionCount() == null) {
            book.setSubscriptionCount(0);
            }

            book.setSubscriptionCount(book.getSubscriptionCount() + 1);

            if (!Boolean.TRUE.equals(book.getIsBestSeller()) && book.getSubscriptionCount() >= 5) {
            book.setIsBestSeller(true);

            BestsellerRegistered event = new BestsellerRegistered(book);
            event.publishAfterCommit();
            }

            repository().save(book);
            });

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
