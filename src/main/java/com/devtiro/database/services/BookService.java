package com.devtiro.database.services;

import com.devtiro.database.domain.entities.BookEntity;

import java.util.List;
import java.util.Optional;

public interface BookService {
    public BookEntity createUpdateBook(String isbn, BookEntity bookEntity);

    public List<BookEntity> findAllBooks();

    public Optional<BookEntity> findOne(String isbn);

    boolean isExists(String isbn);

    BookEntity patchBook(String isbn, BookEntity bookEntity);

    BookEntity patchBookSecondApproach(String isbn, BookEntity bookEntity);
}
