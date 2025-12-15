package edu.rutmiit.demo.booksapicontract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank(message = "Название не может быть пустым")
        String title,
        @Size(min = 10, max = 13, message = "ISBN должен содержать от 10 до 13 символов")
        String isbn,
        @NotNull(message = "ID автора не может быть пустым")
        Long authorId
) {}