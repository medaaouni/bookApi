package com.devtiro.database.controllers;


import com.devtiro.database.domain.dto.AuthorDto;
import com.devtiro.database.domain.dto.BookDto;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.domain.entities.BookEntity;
import com.devtiro.database.repositories.TestDataUtil;
import com.devtiro.database.services.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.awt.print.Book;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) /*to clean up the database after each test to avoid side effects.*/
@AutoConfigureMockMvc
public class BookControllerIntegrationTests {

    private final ObjectMapper objectMapper; //Make dependencies final to ensure they are immutable.
    private final MockMvc mockMvc;
    private final BookService bookService;


    @Autowired
    public BookControllerIntegrationTests(ObjectMapper objectMapper, MockMvc mockMvc, BookService bookService) {
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.bookService = bookService;
    }

    @Test
    public void tesThatSaveBookSuccessfullyReturnHttp201() throws Exception {

        BookDto bookDto = TestDataUtil.createTestBookDto(null);

        String bookJson = objectMapper.writeValueAsString(bookDto);
        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/" + bookDto.getIsbn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)

        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isbn").value(bookDto.getIsbn())
        );


    }


    @Test
    public void testThatBooksCanBeRetrieved() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatBooksReturnListOfBooks() throws Exception {

        BookEntity bookEntity = TestDataUtil.createTestBook(null);

        bookService.createUpdateBook("isbn", bookEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/books")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isbn").value("isbn")
        );
    }

    @Test
    public void testThatGetBookReturns200OkWhenBookExists() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBook(null);
        bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);
        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookEntity.getIsbn())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

    }

    @Test
    public void testThatGetBookReturns404NotFoundWhenBookDoesNotExist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/books/99999")
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    public void testThatUpdateBookReturnsHttpStatus200ok() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBook(null);
        BookEntity savedBookEntity = bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);
        String bookJson = objectMapper.writeValueAsString(savedBookEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/books/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatUpdateBookReturnsUpdatedBook() throws Exception {
        BookEntity bookEntity = TestDataUtil.createTestBook(null);
        BookEntity savedBookEntity = bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);


        BookDto testBookDto = TestDataUtil.createTestBookDto(null);
        testBookDto.setIsbn(savedBookEntity.getIsbn());
        testBookDto.setTitle("Updated Title");

        String bookJson = objectMapper.writeValueAsString(testBookDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.put("/books/" + savedBookEntity.getIsbn())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bookJson)
                ).andExpect(
                        MockMvcResultMatchers.jsonPath("$.isbn").value(savedBookEntity.getIsbn())
                )
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Title")
                );
    }

    @Test
    public void testThatPatchBookReturnsThePatchedBook() throws Exception {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        BookEntity existingBookEntity = TestDataUtil.createTestBook(null);
        BookEntity newBookEntity = TestDataUtil.createTestBook(authorEntity);
        newBookEntity.setIsbn(existingBookEntity.getIsbn());

        bookService.createUpdateBook(existingBookEntity.getIsbn(), existingBookEntity);
        bookService.patchBook(existingBookEntity.getIsbn(), newBookEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/books/" + existingBookEntity.getIsbn())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isbn").value(existingBookEntity.getIsbn())
        );

    }

    @Test
    public void testThatPatchBookShouldUpdateBookWhenExists() throws Exception {
        //Arrange
        BookEntity bookEntity = TestDataUtil.createTestBook(null);
        bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);

        BookDto bookDto = TestDataUtil.createTestBookDto(null);
        bookDto.setIsbn(bookEntity.getIsbn());
        bookDto.setTitle("Updated Title");
        //Act and Assert

        mockMvc.perform(
                /*
                This line initiates an HTTP request using MockMvc.
                 */
                MockMvcRequestBuilders.patch("/books/" + bookEntity.getIsbn(), bookDto)
                        /*
                        Sets the Content-Type header of the HTTP request to application/json
                         */
                        .contentType(MediaType.APPLICATION_JSON)
                        /*
                        Sets the request body for the HTTP request.
                        objectMapper.writeValueAsString(bookDto),
                        Converts the bookDto object into a JSON string.
                         */
                        .content(objectMapper.writeValueAsString(bookDto))
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Title"))
        ;

    }

    @Test
    public void testThatPatchBookShouldSetAuthorWhenDoesntExists() throws Exception {
        //Arrange
        AuthorDto authorDto = TestDataUtil.createTestAuthorDto();
        BookEntity bookEntity = TestDataUtil.createTestBook(null);
        bookService.createUpdateBook(bookEntity.getIsbn(), bookEntity);

        BookDto bookDto = TestDataUtil.createTestBookDto(authorDto);
        bookDto.setIsbn(bookEntity.getIsbn());
        //Act and Assert

        mockMvc.perform(
                /*
                This line initiates an HTTP request using MockMvc.
                 */
                MockMvcRequestBuilders.patch("/books/" + bookEntity.getIsbn(),bookDto)
                        /*
                        Sets the Content-Type header of the HTTP request to application/json
                         */
                        .contentType(MediaType.APPLICATION_JSON)
                        /*
                        Sets the request body for the HTTP request.
                        objectMapper.writeValueAsString(bookDto),
                        Converts the bookDto object into a JSON string.
                         */
                        .content(objectMapper.writeValueAsString(bookDto))
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(MockMvcResultMatchers.jsonPath("$.author.name").value(authorDto.getName()))
        ;

    }



}
