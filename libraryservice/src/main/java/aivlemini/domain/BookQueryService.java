package aivlemini.domain;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BookQueryService {

    private final BookRepository bookRepository;

    public BookQueryService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

   public List<BookView> getAllBooks() {
        return StreamSupport.stream(bookRepository.findAll().spliterator(), false)
                .map(book -> {
                    boolean isBestSeller = Boolean.TRUE.equals(book.getIsBestSeller());
                    int subscriptionCount = book.getSubscriptionCount() != null ? book.getSubscriptionCount() : 0;

                    return new BookView(
                            book.getId(),
                            book.getBookName(),
                            book.getCategory(),
                            isBestSeller,
                            book.getAuthorName(),
                            book.getImage(),
                            subscriptionCount,
                            book.getBookContent(),
                            book.getAuthorId(),
                            book.getPdfPath()
                    );
                })
                .collect(Collectors.toList());
    }
}
