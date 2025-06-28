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
                .map(book -> new BookView(
                        book.getId(),
                        book.getBookName(),
                        book.getCategory(),
                        book.isBestSeller(),
                        book.getAuthorName(),
                        book.getImage(),
                        book.getSubscriptionCount(),
                        book.getBookContent(),
                        book.getAuthorId(),
                        book.getPdfPath()
                ))
                .collect(Collectors.toList());
    }
}
