package edu.rutmiit.demo.demorest.assemblers;

import edu.rutmiit.demo.booksapicontract.dto.BookResponse;
import edu.rutmiit.demo.demorest.controllers.AuthorController;
import edu.rutmiit.demo.demorest.controllers.BookController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BookModelAssembler implements RepresentationModelAssembler<BookResponse, EntityModel<BookResponse>> {

    @Override
    public EntityModel<BookResponse> toModel(BookResponse book) {
        return EntityModel.of(book,
                linkTo(methodOn(BookController.class).getBookById(book.getId())).withSelfRel(),
                linkTo(methodOn(AuthorController.class).getAuthorById(book.getAuthor().getId())).withRel("author"),
                linkTo(methodOn(BookController.class).getAllBooks(null, 0, 10)).withRel("collection")
        );
    }
}
