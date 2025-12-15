package edu.rutmiit.demo.booksapicontract.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(
        @NotBlank(message = "Имя автора не может быть пустым") String firstName,
        @NotBlank(message = "Фамилия автора не может быть пустой") String lastName
) {}