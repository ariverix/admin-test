package edu.rutmiit.demo.demorest.storage;

import edu.rutmiit.demo.booksapicontract.dto.AuthorResponse;
import edu.rutmiit.demo.booksapicontract.dto.BookResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    public final Map<Long, AuthorResponse> authors = new ConcurrentHashMap<>();
    public final Map<Long, BookResponse> books = new ConcurrentHashMap<>();

    public final AtomicLong authorSequence = new AtomicLong(0);
    public final AtomicLong bookSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {
        // Создаем несколько авторов
        AuthorResponse author1 = new AuthorResponse(authorSequence.incrementAndGet(), "Лев", "Толстой");
        AuthorResponse author2 = new AuthorResponse(authorSequence.incrementAndGet(), "Федор", "Достоевский");
        authors.put(author1.getId(), author1);
        authors.put(author2.getId(), author2);

        // Создаем несколько книг
        long bookId1 = bookSequence.incrementAndGet();
        books.put(bookId1, new BookResponse(bookId1, "Война и мир", "978-5-389-06259-8", author1, LocalDateTime.now()));

        long bookId2 = bookSequence.incrementAndGet();
        books.put(bookId2, new BookResponse(bookId2, "Преступление и наказание", "978-5-389-06259-9", author2, LocalDateTime.now()));

        long bookId3 = bookSequence.incrementAndGet();
        books.put(bookId3, new BookResponse(bookId3, "Анна Каренина", "978-5-389-06259-7", author1, LocalDateTime.now()));
    }
}