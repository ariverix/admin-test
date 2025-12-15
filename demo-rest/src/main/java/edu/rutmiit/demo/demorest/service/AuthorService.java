package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.booksapicontract.dto.AuthorRequest;
import edu.rutmiit.demo.booksapicontract.dto.AuthorResponse;
import edu.rutmiit.demo.booksapicontract.exception.ResourceNotFoundException;
import edu.rutmiit.demo.demorest.storage.InMemoryStorage;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorService {
    private final InMemoryStorage storage;
    private final BookService bookService;

    public AuthorService(InMemoryStorage storage, @Lazy BookService bookService) {
        this.storage = storage;
        this.bookService = bookService;
    }

    public List<AuthorResponse> findAll() {
        return storage.authors.values().stream().toList();
    }

    public AuthorResponse findById(Long id) {
        return Optional.ofNullable(storage.authors.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Author", id));
    }

    public AuthorResponse create(AuthorRequest request) {
        long id = storage.authorSequence.incrementAndGet();
        AuthorResponse author = new AuthorResponse(id, request.firstName(), request.lastName());
        storage.authors.put(id, author);
        return author;
    }

    public AuthorResponse update(Long id, AuthorRequest request) {
        findById(id); // Проверяем, что автор существует
        AuthorResponse updatedAuthor = new AuthorResponse(id, request.firstName(), request.lastName());
        storage.authors.put(id, updatedAuthor);
        return updatedAuthor;
    }

    public void delete(Long id) {
        findById(id); // Проверяем, что автор существует

        // Перед удалением автора удаляем все связанные с ним книги
        bookService.deleteBooksByAuthorId(id);

        // Удаляем самого автора
        storage.authors.remove(id);
    }
}
