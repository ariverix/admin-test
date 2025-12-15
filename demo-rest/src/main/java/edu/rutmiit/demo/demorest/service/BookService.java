package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.booksapicontract.dto.*;
import edu.rutmiit.demo.booksapicontract.exception.IsbnAlreadyExistsException;
import edu.rutmiit.demo.booksapicontract.exception.ResourceNotFoundException;
import edu.rutmiit.demo.demorest.config.RabbitMQConfig;
import edu.rutmiit.demo.demorest.storage.InMemoryStorage;
import edu.rutmiit.demo.events.BookCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class BookService {

    private final InMemoryStorage storage;
    private final AuthorService authorService;
    private final RabbitTemplate rabbitTemplate; // Внедряем RabbitTemplate

    public BookService(InMemoryStorage storage, @Lazy AuthorService authorService, RabbitTemplate rabbitTemplate) {
        this.storage = storage;
        this.authorService = authorService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public BookResponse findBookById(Long id) {
        return Optional.ofNullable(storage.books.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Book", id));
    }

    public PagedResponse<BookResponse> findAllBooks(Long authorId, int page, int size) {
        // Получаем стрим всех книг
        Stream<BookResponse> booksStream = storage.books.values().stream()
                .sorted((b1, b2) -> b1.getId().compareTo(b2.getId())); // Сортируем для консистентности

        // Фильтруем, если указан authorId
        if (authorId != null) {
            booksStream = booksStream.filter(book -> book.getAuthor() != null && book.getAuthor().getId().equals(authorId));
        }

        List<BookResponse> allBooks = booksStream.toList();

        // Выполняем пагинацию
        int totalElements = allBooks.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<BookResponse> pageContent = (fromIndex > toIndex) ? List.of() : allBooks.subList(fromIndex, toIndex);

        return new PagedResponse<>(pageContent, page, size, totalElements, totalPages, page >= totalPages - 1);
    }

    public BookResponse createBook(BookRequest request) {
        // Проверка на существующий ISBN
        validateIsbn(request.isbn(), null);

        // Находим автора, если не найден - будет исключение
        AuthorResponse author = authorService.findById(request.authorId());

        long id = storage.bookSequence.incrementAndGet();
        var book = new BookResponse(
                id,
                request.title(),
                request.isbn(),
                author,
                LocalDateTime.now()
        );
        storage.books.put(id, book);

        // Тут публикуем событие

        BookCreatedEvent event = new BookCreatedEvent(
                book.getId(),
                book.getTitle(),
                author.getFirstName() + " " + author.getLastName()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_BOOK_CREATED, event);


        return book;
    }

    public BookResponse updateBook(Long id, UpdateBookRequest request) {
        BookResponse existingBook = findBookById(id); // Проверяем, что книга существует
        validateIsbn(request.isbn(), id); // Проверяем ISBN, исключая текущую книгу

        var updatedBook = new BookResponse(
                id,
                request.title(),
                request.isbn(),
                existingBook.getAuthor(), // Автор не меняется
                existingBook.getCreatedAt() // Дата создания не меняется
        );
        storage.books.put(id, updatedBook);
        return updatedBook;
    }

    public void deleteBook(Long id) {
        findBookById(id); // Проверяем, что книга существует
        storage.books.remove(id);
    }

    public void deleteBooksByAuthorId(Long authorId) {
        // Находим ID всех книг, которые нужно удалить
        List<Long> bookIdsToDelete = storage.books.values().stream()
                .filter(book -> book.getAuthor() != null && book.getAuthor().getId().equals(authorId))
                .map(BookResponse::getId)
                .toList();

        // Удаляем их из хранилища
        bookIdsToDelete.forEach(storage.books::remove);
    }

    private void validateIsbn(String isbn, Long currentBookId) {
        storage.books.values().stream()
                .filter(book -> book.getIsbn().equalsIgnoreCase(isbn))
                .filter(book -> !book.getId().equals(currentBookId)) // Игнорируем книгу, которую обновляем
                .findAny()
                .ifPresent(book -> {
                    throw new IsbnAlreadyExistsException(isbn);
                });
    }
}
