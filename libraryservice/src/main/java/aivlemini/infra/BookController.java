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

    // @Autowired
    // BookRepository bookRepository;
}
//>>> Clean Arch / Inbound Adaptor
