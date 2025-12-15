package edu.rutmiit.demo.booksapicontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Ответ с пагинацией")
public record PagedResponse<T>(
        @Schema(description = "Содержимое страницы") List<T> content,
        @Schema(description = "Номер текущей страницы (начиная с 0)") int pageNumber,
        @Schema(description = "Размер страницы") int pageSize,
        @Schema(description = "Общее количество элементов") long totalElements,
        @Schema(description = "Общее количество страниц") int totalPages,
        @Schema(description = "Является ли страница последней") boolean last
) {}