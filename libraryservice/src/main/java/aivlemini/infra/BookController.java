package aivlemini.infra;

import aivlemini.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/books")
@Transactional
public class BookController {

    private final BookRepository bookRepository;
    private final BookQueryService bookQueryService;

    public BookController(BookRepository bookRepository,BookQueryService bookQueryService){
        this.bookRepository = bookRepository;
        this.bookQueryService=bookQueryService;
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }
    
    @GetMapping
    public List<BookView> getBooks() {
        return bookQueryService.getAllBooks();
    }
      // 구독 수 증가 API 추가
    @PostMapping("/{id}/subscribe")
    public String subscribeBook(@PathVariable("id") Long id) {
        BookApply apply = new BookApply();
        apply.setBookId(id);

        Book.grantBestseller(apply);

        return "Subscription increased and bestseller status updated if applicable.";
    }
    // @Autowired
    // BookRepository bookRepository;
}
//>>> Clean Arch / Inbound Adaptor
