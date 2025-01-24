package com.devtiro.database.controllers;


import com.devtiro.database.domain.dto.AuthorDto;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.repositories.TestDataUtil;
import com.devtiro.database.services.AuthorService;
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

@SpringBootTest /*starts up the full Spring application context, allowing you to test all layers of your application*/
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) /*each test runs in isolation, and the context is reloaded*/
@AutoConfigureMockMvc  /*automatically set up a MockMvc instance to simulate HTTP requests to your controller.*/
public class AuthorControllerIntegrationTest {

    private MockMvc mockMvc; // This will simulate HTTP requests

    private AuthorService authorService;

    private ObjectMapper objectMapper;

    /**
     * Dependency Injection (DI) is a technique that implements Inversion of Control (IoC); we give the control to the spring context
     * The Spring context is the IoC (Inversion of Control) container that is responsible for:
     * Managing the beans
     * Configuration and environment management
     * Event handling
     */

    @Autowired
    public AuthorControllerIntegrationTest(MockMvc mockMvc, AuthorService authorService) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.authorService = authorService;
    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturnsHttp201Created() throws Exception {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        String authorJson = objectMapper.writeValueAsString(authorEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );

    }

    @Test
    public void testThatCreateAuthorSuccessfullyReturnsAndSaveAuthor() throws Exception {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        String authorJson = objectMapper.writeValueAsString(authorEntity);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(authorEntity.getName())
        )
        ;
    }

    @Test
    public void testThatListAuthorsReturnsHttp200Ok() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

    }

    @Test
    public void testThatListAuthorsReturnsListOfAuthors() throws Exception {

        // we need to create an author in the db cuz in each test  dirtyContext annotation completely clean down the database

        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        authorService.saveAuthor(authorEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").isString()
        )
        ;

    }

    @Test
    public void testThatGetAuthorReturnsHttp200Ok() throws Exception {

        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        authorService.saveAuthor(authorEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/" + authorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatGetAuthorReturnsHttp404WhenAuthorsNotExists() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatUpdateAuthorGetReturnHttp200OkWhenAuthorExists() throws Exception {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.saveAuthor(authorEntity);
        System.out.println(objectMapper.writeValueAsString(authorEntity));
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/authors/" + savedAuthor.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedAuthor))
                )
                .andExpect(MockMvcResultMatchers.status().isOk()
                );
    }

    @Test
    public void testThatUpdateAuthorUpdateExistingAuthor() throws Exception {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.saveAuthor(authorEntity);
        AuthorDto authorDto = TestDataUtil.createTestAuthorDto();
        authorDto.setId(authorDto.getId());

        mockMvc.perform(MockMvcRequestBuilders.put("/authors/" + savedAuthor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorDto)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedAuthor.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(authorDto.getAge()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(authorDto.getName()));
        ;
    }

    @Test
    public void testThatUpdateAuthorGetReturnHttp404kWhenAuthorDoesntExist() throws Exception {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.saveAuthor(authorEntity);
        mockMvc.perform(
                        MockMvcRequestBuilders.put("/authors/9991")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(savedAuthor))
                )
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatPatchAuthorReturnsHttp200OkWhenAuthorExists() throws Exception {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.saveAuthor(authorEntity);

        AuthorDto authorDto = TestDataUtil.createTestAuthorDto();
        authorDto.setName("PATCHED");

        String authorDtoJson = objectMapper.writeValueAsString(authorDto);
        // When: Perform a PATCH request to patch a new author
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/authors/" + authorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatPatchAuthorReturnsUpdatedAuthor() throws Exception {
        AuthorEntity authorEntity = TestDataUtil.createTestAuthorA();
        AuthorEntity savedAuthor = authorService.saveAuthor(authorEntity);

        AuthorDto authorDto = TestDataUtil.createTestAuthorDto();
        authorDto.setName("PATCHED");

        String authorDtoJson = objectMapper.writeValueAsString(authorDto);
        // When: Perform a PATCH request to patch a new author
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/authors/" + authorEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorDtoJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value("PATCHED")
        );

    }


}
