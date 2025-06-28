package aivlemini.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookView {

    private Long id;
    private String bookName;
    private String category;
    private boolean isBestSeller;
    private String authorName;
    private String image;
    private int subscriptionCount;
    private String bookContent;
    private Long authorId;
    private String pdfPath;
}
