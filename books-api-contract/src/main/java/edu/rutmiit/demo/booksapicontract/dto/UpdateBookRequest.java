package edu.rutmiit.demo.booksapicontract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO для обновления. Не позволяем менять автора книги, только название и ISBN.
public record UpdateBookRequest(
        @NotBlank(message = "Название не может быть пустым")
        String title,
        @Size(min = 10, max = 13, message = "ISBN должен содержать от 10 до 13 символов")
        String isbn
) {}