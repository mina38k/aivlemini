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
        "where(:id is null or book.id = :id) and (:bookName is null or book.bookName like %:bookName%) and (:category is null or book.category like %:category%) and (:isBestSeller is null or book.isBestSeller = :isBestSeller) and (:authorName is null or book.authorName like %:authorName%) and (:image is null or book.image like %:image%) and (:subscriptionCount is null or book.subscriptionCount = :subscriptionCount) and (:bookContent is null or book.bookContent like %:bookContent%) and (:authorId is null or book.authorId like %:authorId%) and (:pdfPath is null or book.pdfPath like %:pdfPath%)"
    )
    Book getBooks(
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
