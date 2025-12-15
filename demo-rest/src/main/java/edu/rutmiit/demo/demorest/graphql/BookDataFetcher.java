package edu.rutmiit.demo.demorest.graphql;

import com.netflix.graphql.dgs.*;
import edu.rutmiit.demo.booksapicontract.dto.*;
import edu.rutmiit.demo.demorest.service.BookService;
import graphql.schema.DataFetchingEnvironment;

import java.util.Map;

@DgsComponent
public class BookDataFetcher {

    private final BookService bookService;

    public BookDataFetcher(BookService bookService) {
        this.bookService = bookService;
    }

    @DgsQuery
    public BookResponse bookById(@InputArgument Long id) {
        return bookService.findBookById(id);
    }

    @DgsQuery
    public PagedResponse<BookResponse> books(@InputArgument Long authorId, @InputArgument int page, @InputArgument int size) {
        return bookService.findAllBooks(authorId, page, size);
    }

    // Этот метод разрешает вложенное поле 'author' внутри типа 'Book'
    @DgsData(parentType = "Book", field = "author")
    public AuthorResponse author(DataFetchingEnvironment dfe) {
        BookResponse book = dfe.getSource();
        return book.getAuthor();
    }

    @DgsMutation
    public BookResponse createBook(@InputArgument("input") Map<String, Object> input) {
        BookRequest request = new BookRequest(
                (String) input.get("title"),
                (String) input.get("isbn"),
                Long.parseLong(input.get("authorId").toString())
        );
        return bookService.createBook(request);
    }

    @DgsMutation
    public BookResponse updateBook(@InputArgument Long id, @InputArgument("input") Map<String, String> input) {
        UpdateBookRequest request = new UpdateBookRequest(
                input.get("title"),
                input.get("isbn")
        );
        return bookService.updateBook(id, request);
    }

    @DgsMutation
    public Long deleteBook(@InputArgument Long id) {
        bookService.deleteBook(id);
        return id;
    }
}

