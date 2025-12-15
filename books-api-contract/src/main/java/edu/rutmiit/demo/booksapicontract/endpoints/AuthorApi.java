package edu.rutmiit.demo.booksapicontract.endpoints;

import edu.rutmiit.demo.booksapicontract.dto.AuthorRequest;
import edu.rutmiit.demo.booksapicontract.dto.AuthorResponse;
import edu.rutmiit.demo.booksapicontract.dto.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "authors", description = "API для работы с авторами")
@RequestMapping("/api/authors")
public interface AuthorApi {

    @Operation(summary = "Получить всех авторов")
    @ApiResponse(responseCode = "200", description = "Список авторов")
    @GetMapping
    CollectionModel<EntityModel<AuthorResponse>> getAllAuthors();

    @Operation(summary = "Получить автора по ID")
    @ApiResponse(responseCode = "200", description = "Автор найден")
    @ApiResponse(responseCode = "404", description = "Автор не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<AuthorResponse> getAuthorById(@PathVariable Long id);

    @Operation(summary = "Создать нового автора")
    @ApiResponse(responseCode = "201", description = "Автор успешно создан")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<AuthorResponse>> createAuthor(@Valid @RequestBody AuthorRequest request);

    @Operation(summary = "Обновить автора")
    @ApiResponse(responseCode = "200", description = "Автор обновлен")
    @ApiResponse(responseCode = "404", description = "Автор не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<AuthorResponse> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequest request);

    @Operation(summary = "Удалить автора")
    @ApiResponse(responseCode = "204", description = "Автор удален")
    @ApiResponse(responseCode = "404", description = "Автор не найден")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAuthor(@PathVariable Long id);
}
