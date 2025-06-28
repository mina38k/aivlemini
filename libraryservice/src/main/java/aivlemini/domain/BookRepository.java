package aivlemini.domain;

import aivlemini.domain.*;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "books", path = "books")
public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
    @Query(
        value = "select book " +
        "from Book book " +
        "where(:id is null or book.id = :id) and (:bookName is null or book.bookName like concat('%', :bookName, '%')) and (:category is null or book.category like concat('%', :category, '%')) and (:isBestSeller is null or book.isBestSeller = :isBestSeller) and (:authorName is null or book.authorName like concat('%', :authorName, '%')) and (:image is null or book.image like concat('%', :image, '%')) and (:subscriptionCount is null or book.subscriptionCount = :subscriptionCount) and (:bookContent is null or book.bookContent like concat('%', :bookContent, '%')) and (:authorId is null or book.authorId like concat('%', :authorId, '%')) and (:pdfPath is null or book.pdfPath like concat('%', :pdfPath, '%'))"
    )
    // Book getBooks(
    //     Long id,
    //     String bookName,
    //     String category,
    //     Boolean isBestSeller,
    //     String authorName,
    //     String image,
    //     Integer subscriptionCount,
    //     String bookContent,
    //     String authorId,
    //     String pdfPath
    // ); 
    List<Book> findBooks(
    Long id,
    String bookName,
    String category,
    Boolean isBestSeller,
    String authorName,
    String image,
    Integer subscriptionCount,
    String bookContent,
    String authorId,
    String pdfPath
);
}
