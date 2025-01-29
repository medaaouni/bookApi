package com.devtiro.database.controllers;


import com.devtiro.database.domain.dto.BookDto;
import com.devtiro.database.domain.entities.BookEntity;
import com.devtiro.database.mappers.impl.BookMapper;
import com.devtiro.database.services.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class BookController {

    private BookService bookService;
    private BookMapper bookMapper;

    public BookController(BookService bookService, BookMapper bookMapper) {
        this.bookService = bookService;
        this.bookMapper = bookMapper;
    }

    /*
    the put mapping so we can specify the isbn every time we create a book
     */
    @PutMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> createUpdateBook(@PathVariable("isbn") String isbn, @RequestBody BookDto bookDto) {

        BookEntity bookEntity = bookMapper.mapFrom(bookDto);
        boolean bookExists = bookService.isExists(isbn);
        BookEntity savedBook = bookService.createUpdateBook(isbn, bookEntity);


        if (bookExists) {
            return new ResponseEntity<>(bookMapper.mapTo(savedBook), HttpStatus.OK);
        } else {
            return new ResponseEntity<BookDto>(bookMapper.mapTo(savedBook), HttpStatus.CREATED);
        }

    }

    /**
     * Why Use ResponseEntity?
     * However, ResponseEntity is useful when:
     * Custom HTTP Status Codes: If you want to return something other than 200 OK, like 201 Created or 404 Not Found.
     * Custom Headers: If you need to include specific headers in the response.
     * Flexibility: It gives you more control over the response, which can be useful in more complex scenarios.
     */
    @GetMapping(path = "/books")
    public List<BookDto> findAllBooks() {
        /*
        A Stream in Java is a sequence of elements that supports functional-style operations (e.g., map, filter, collect)
        If you didn't use stream(), you would need to manually iterate over the list and perform the transformation,
        Immutability:
            The stream() approach does not modify the original list. Instead, it creates a new list with the transformed objects.
            If you use .parallelStream() instead of .stream(), the stream operations can be executed in parallel,
        Parallel Streams:
            which can improve performance for CPU-bound tasks on large datasets.
            However, parallel streams come with their own overhead (e.g., thread management) and are not always faster,
            especially for small datasets or I/O-bound tasks.
         */
        return bookService.findAllBooks()
                .stream()
                .map(bookMapper::mapTo)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/books/page")
    public Page<BookDto> findAll(Pageable pageable) {
        return bookService.findAll(pageable)
                .map(bookMapper::mapTo);
    }

    @GetMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> getBook(@PathVariable("isbn") String isbn) {
        Optional<BookEntity> foundBookEntity = bookService.findOne(isbn);
        return foundBookEntity.map(
                        bookEntity -> new ResponseEntity<>(bookMapper.mapTo(bookEntity), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

//        return foundBookEntity.map(bookEntity -> {
//            BookDto bookDto = bookMapper.mapTo(bookEntity);
//            return new ResponseEntity<>(bookDto, HttpStatus.OK);
//        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping(path = "/books/{isbn}")
    public ResponseEntity<BookDto> patchBook(@PathVariable("isbn") String isbn, @RequestBody BookDto bookDto) {

        if (!bookService.isExists(isbn)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        System.out.println("patch book called");
        System.out.println(bookDto.toString());
        System.out.println(bookMapper.mapFrom(bookDto));

        BookEntity bookEntity = bookService.patchBook(isbn, bookMapper.mapFrom(bookDto));

        return new ResponseEntity<>(bookMapper.mapTo(bookEntity), HttpStatus.OK);

    }

    @DeleteMapping(path = "books/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable("isbn") String isbn) {
        bookService.deleteBook(isbn);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }


//    @PutMapping(path = "/books/{isbn}")
//    public ResponseEntity<BookDto> updateBook(@PathVariable("isbn") String isbn, @RequestBody BookDto bookDto) {
//        Optional<BookEntity> foundBookEntity = bookService.findOne(isbn);
//
//        if (foundBookEntity.isEmpty()) {
//            BookEntity bookEntity = bookService.createUpdateBook(isbn, bookMapper.mapFrom(bookDto));
//            return new ResponseEntity<>(bookMapper.mapTo(bookEntity), HttpStatus.CREATED);
//        } else {
//            BookEntity bookEntity = bookService.createUpdateBook(isbn,  bookMapper.mapFrom(bookDto));
//            return new ResponseEntity<>(bookMapper.mapTo(bookEntity), HttpStatus.OK);
//        }
//    }


}
