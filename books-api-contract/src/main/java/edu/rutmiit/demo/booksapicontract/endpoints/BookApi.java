package edu.rutmiit.demo.booksapicontract.endpoints;

import edu.rutmiit.demo.booksapicontract.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "books", description = "API для работы с книгами")
@RequestMapping("/api/books")
public interface BookApi {

    @Operation(summary = "Получить книгу по ID")
    @ApiResponse(responseCode = "200", description = "Книга найдена")
    @ApiResponse(responseCode = "404", description = "Книга не найдена", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<BookResponse> getBookById(@PathVariable("id") Long id);

    @Operation(summary = "Получить список всех книг с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Список книг")
    @GetMapping
    PagedModel<EntityModel<BookResponse>> getAllBooks(
            @Parameter(description = "Фильтр по ID автора") @RequestParam(required = false) Long authorId,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "Создать новую книгу")
    @ApiResponse(responseCode = "201", description = "Книга успешно создана")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Книга с таким ISBN уже существует", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<BookResponse>> createBook(@Valid @RequestBody BookRequest request);

    @Operation(summary = "Обновить книгу по ID")
    @ApiResponse(responseCode = "200", description = "Книга успешно обновлена")
    @ApiResponse(responseCode = "404", description = "Книга не найдена", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Книга с таким ISBN уже существует", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<BookResponse> updateBook(@PathVariable Long id, @Valid @RequestBody UpdateBookRequest request);

    @Operation(summary = "Удалить книгу по ID")
    @ApiResponse(responseCode = "204", description = "Книга успешно удалена")
    @ApiResponse(responseCode = "404", description = "Книга не найдена")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteBook(@PathVariable Long id);
}