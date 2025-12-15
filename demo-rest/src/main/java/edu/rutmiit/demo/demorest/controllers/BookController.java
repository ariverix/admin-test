package edu.rutmiit.demo.demorest.controllers;

import edu.rutmiit.demo.booksapicontract.dto.BookRequest;
import edu.rutmiit.demo.booksapicontract.dto.BookResponse;
import edu.rutmiit.demo.booksapicontract.dto.PagedResponse;
import edu.rutmiit.demo.booksapicontract.dto.UpdateBookRequest;
import edu.rutmiit.demo.booksapicontract.endpoints.BookApi;
import edu.rutmiit.demo.demorest.assemblers.BookModelAssembler;
import edu.rutmiit.demo.demorest.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController implements BookApi {

    private final BookService bookService;
    private final BookModelAssembler bookModelAssembler;
    private final PagedResourcesAssembler<BookResponse> pagedResourcesAssembler;

    public BookController(BookService bookService, BookModelAssembler bookModelAssembler, PagedResourcesAssembler<BookResponse> pagedResourcesAssembler) {
        this.bookService = bookService;
        this.bookModelAssembler = bookModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    public EntityModel<BookResponse> getBookById(Long id) {
        BookResponse book = bookService.findBookById(id);
        return bookModelAssembler.toModel(book);
    }

    @Override
    public PagedModel<EntityModel<BookResponse>> getAllBooks(Long authorId, int page, int size) {
        // Наш сервис возвращает кастомный PagedResponse, а PagedResourcesAssembler ожидает Page из Spring Data, а у нас In Memory реализация.
        // Поэтому мы делаем простую адаптацию.
        PagedResponse<BookResponse> pagedResponse = bookService.findAllBooks(authorId, page, size);
        Page<BookResponse> bookPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );

        // PagedResourcesAssembler автоматически создаст PagedModel со всеми ссылками пагинации
        return pagedResourcesAssembler.toModel(bookPage, bookModelAssembler);
    }

    @Override
    public ResponseEntity<EntityModel<BookResponse>> createBook(BookRequest request) {
        BookResponse createdBook = bookService.createBook(request);
        EntityModel<BookResponse> entityModel = bookModelAssembler.toModel(createdBook);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<BookResponse> updateBook(Long id, UpdateBookRequest request) {
        BookResponse updatedBook = bookService.updateBook(id, request);
        return bookModelAssembler.toModel(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        bookService.deleteBook(id);
    }
}