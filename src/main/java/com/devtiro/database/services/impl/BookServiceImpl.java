package com.devtiro.database.services.impl;

import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.domain.entities.BookEntity;
import com.devtiro.database.repositories.BookRepository;
import com.devtiro.database.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class BookServiceImpl implements BookService {

    BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookEntity createUpdateBook(String isbn, BookEntity bookEntity) {
        bookEntity.setIsbn(isbn);
        return bookRepository.save(bookEntity);
    }

    @Override
    public List<BookEntity> findAllBooks() {
        return StreamSupport
                .stream(bookRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Page<BookEntity> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    public Optional<BookEntity> findOne(String isbn) {
        return bookRepository.findById(isbn);
    }

    @Override
    public boolean isExists(String isbn) {
        return bookRepository.existsById(isbn);
    }

    @Override
    public BookEntity patchBook(String isbn, BookEntity bookEntity) {
        return bookRepository.findById(isbn).map(existingBook -> {
            // Update book fields if provided in the patch
            Optional.ofNullable(bookEntity.getTitle()).ifPresent(existingBook::setTitle);

            // Handle nested author update functionally
            Optional.ofNullable(bookEntity.getAuthor()).ifPresent(newAuthor -> updateAuthor(existingBook, newAuthor));

            return bookRepository.save(existingBook);
        }).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    private void updateAuthor(BookEntity existingBook, AuthorEntity newAuthor) {
        System.out.println("called");
        Optional.ofNullable(existingBook.getAuthor()).ifPresentOrElse(existingAuthor -> {
            // Update existing author fields if provided
            Optional.ofNullable(newAuthor.getName()).ifPresent(existingAuthor::setName);
            Optional.ofNullable(newAuthor.getAge()).ifPresent(existingAuthor::setAge);
        }, () -> {
            // If no existing author, set the new one
            existingBook.setAuthor(newAuthor);
        });
    }

    private void updateAuthorMyVersion(BookEntity existingBook, AuthorEntity newAuthor) {
        Optional.ofNullable(existingBook.getAuthor()).map(existingAuthor -> {
            // Update existing author fields if provided
            Optional.ofNullable(newAuthor.getName()).ifPresent(existingAuthor::setName);
            Optional.ofNullable(newAuthor.getAge()).ifPresent(existingAuthor::setAge);
            return existingAuthor;
        }).orElseGet(() -> {
            // If no existing author, set the new one
            existingBook.setAuthor(newAuthor);
            return newAuthor;
        });
    }

    @Override
    public BookEntity patchBookSecondApproach(String isbn, BookEntity bookEntity) {
        bookEntity.setIsbn(isbn);
        return bookRepository.findById(isbn).map(existingBook -> {
                     /*
                       ensure that null values in the bookEntity parameter do not overwrite existing values
                      */
            Optional.ofNullable(bookEntity.getTitle()).ifPresent(existingBook::setTitle);
            Optional.ofNullable(bookEntity.getAuthor()).ifPresent(newAuthor -> {

                // Handle case where book might not have an existing author
                AuthorEntity existingAuthor = Optional.ofNullable(existingBook.getAuthor()).orElseGet(() -> {
                    AuthorEntity author = new AuthorEntity();
                    existingBook.setAuthor(author);
                    return author;
                });

                // Update existing author with new values if provided
                Optional.ofNullable(newAuthor.getName()).ifPresent(existingAuthor::setName);
                Optional.ofNullable(newAuthor.getAge()).ifPresent(existingAuthor::setAge);

            });
            return bookRepository.save(existingBook);
        }).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    @Override
    public void deleteBook(String isbn) {
        bookRepository.deleteById(isbn);
    }

}
