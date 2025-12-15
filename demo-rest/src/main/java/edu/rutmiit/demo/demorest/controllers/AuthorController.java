package edu.rutmiit.demo.demorest.controllers;

import edu.rutmiit.demo.booksapicontract.dto.AuthorRequest;
import edu.rutmiit.demo.booksapicontract.dto.AuthorResponse;
import edu.rutmiit.demo.booksapicontract.endpoints.AuthorApi;
import edu.rutmiit.demo.demorest.assemblers.AuthorModelAssembler;
import edu.rutmiit.demo.demorest.service.AuthorService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthorController implements AuthorApi {

    private final AuthorService authorService;
    private final AuthorModelAssembler authorModelAssembler;

    public AuthorController(AuthorService authorService, AuthorModelAssembler authorModelAssembler) {
        this.authorService = authorService;
        this.authorModelAssembler = authorModelAssembler;
    }

    @Override
    public CollectionModel<EntityModel<AuthorResponse>> getAllAuthors() {
        List<AuthorResponse> authors = authorService.findAll();
        return authorModelAssembler.toCollectionModel(authors);
    }

    @Override
    public EntityModel<AuthorResponse> getAuthorById(Long id) {
        AuthorResponse author = authorService.findById(id);
        return authorModelAssembler.toModel(author);
    }

    @Override
    public ResponseEntity<EntityModel<AuthorResponse>> createAuthor(AuthorRequest request) {
        AuthorResponse createdAuthor = authorService.create(request);
        EntityModel<AuthorResponse> entityModel = authorModelAssembler.toModel(createdAuthor);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<AuthorResponse> updateAuthor(Long id, AuthorRequest request) {
        AuthorResponse updatedAuthor = authorService.update(id, request);
        return authorModelAssembler.toModel(updatedAuthor);
    }

    @Override
    public void deleteAuthor(Long id) {
        authorService.delete(id);
    }
}
