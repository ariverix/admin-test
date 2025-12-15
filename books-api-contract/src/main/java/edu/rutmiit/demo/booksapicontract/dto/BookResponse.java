package edu.rutmiit.demo.booksapicontract.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Objects;

@Relation(collectionRelation = "books", itemRelation = "book")
public class BookResponse extends RepresentationModel<BookResponse> {

    private final Long id;
    private final String title;
    private final String isbn;
    private final AuthorResponse author;
    private final LocalDateTime createdAt;

    public BookResponse(Long id, String title, String isbn, AuthorResponse author, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public AuthorResponse getAuthor() {
        return author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BookResponse that = (BookResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(isbn, that.isbn) && Objects.equals(author, that.author) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, title, isbn, author, createdAt);
    }
}
