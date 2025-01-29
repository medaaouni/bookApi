package com.devtiro.database.repositories;

import com.devtiro.database.domain.dto.AuthorDto;
import com.devtiro.database.domain.dto.BookDto;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.domain.entities.BookEntity;

public final class TestDataUtil {

    private TestDataUtil() {}

    public static AuthorEntity createTestAuthorA() {
        return AuthorEntity.builder()
                .name("ahmed")
                .age(30)
                .build();
    }

    public static AuthorEntity createTestAuthorB() {
        return AuthorEntity.builder()
                .name("Jess")
                .age(25)
                .build();
    }

    public static AuthorDto createTestAuthorDto() {
        return AuthorDto.builder()
                .name("TestAuthorDto")
                .age(60)
                .build();
    }


    public static BookEntity createTestBook(final AuthorEntity authorEntity) {
        return BookEntity
                .builder()
                .isbn("2")
                .title("title")
                .author(authorEntity)
                .build();
    }

    public static BookDto createTestBookDto(final AuthorDto authorDto) {
        return BookDto
                .builder()
                .isbn("2")
                .title("title")
                .author(authorDto)
                .build();
    }
}
