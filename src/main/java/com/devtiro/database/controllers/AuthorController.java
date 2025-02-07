package com.devtiro.database.controllers;


import com.devtiro.database.domain.dto.AuthorDto;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.mappers.Mapper;
import com.devtiro.database.mappers.impl.AuthorMapper;
import com.devtiro.database.services.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class AuthorController {

    private final AuthorService authorService;

    Mapper<AuthorEntity, AuthorDto> authorMapper;

    public AuthorController(AuthorService authorService, AuthorMapper authorMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
    }

    @PostMapping(path = "/authors")
    public ResponseEntity<AuthorDto> createAuthor(@RequestBody AuthorDto authorDto) {

        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        AuthorEntity savedEntity = authorService.saveAuthor(authorEntity);

        return new ResponseEntity<AuthorDto>(authorMapper.mapTo(savedEntity), HttpStatus.CREATED);
    }


    /**
     *@PreAuthorize permet d'utiliser hasRole(), hasAnyRole(), hasAuthority(), hasAnyAuthority() directement dans le contrôleur.
     *
     *@Secured est une alternative à @PreAuthorize, mais elle ne supporte que hasRole() (et non hasAuthority()).
     *
     *@RolesAllowed fonctionne comme @Secured, mais elle est standard JEE.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/authors")
    public List<AuthorDto> listAuthors() {
        List<AuthorEntity> authors = authorService.findAll();
        return authors
                .stream()
                .map(authorMapper::mapTo)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> getAuthor(@PathVariable("id") Long id) {

        Optional<AuthorEntity> foundAuthorEntity = authorService.findOne(id);
        return foundAuthorEntity.map(authorEntity -> {
            AuthorDto authorDto = authorMapper.mapTo(authorEntity);
            return new ResponseEntity<>(authorDto, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable("id") Long id, @RequestBody AuthorDto authorDto) {

        if (!authorService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // this should be in the service
        authorDto.setId(id);
        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        AuthorEntity savedAuthorEntity = authorService.saveAuthor(authorEntity);

        return new ResponseEntity<>(authorMapper.mapTo(savedAuthorEntity), HttpStatus.OK);

    }

    /*
     Another way to handle the Not found exception but with raising an Exception and not sending an HTTP response
     */
    @PutMapping(path = "/authors/{id}/2")
    public ResponseEntity<AuthorDto> updateAuthor2(@PathVariable("id") Long id, @RequestBody AuthorDto authorDto) {


        AuthorEntity author = authorService.findOne(id)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + id));

        authorDto.setId(id);
        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        AuthorEntity savedAuthorEntity = authorService.saveAuthor(authorEntity);

        return new ResponseEntity<>(authorMapper.mapTo(savedAuthorEntity), HttpStatus.OK);

    }

    @PatchMapping(path = "/authors/{id}")
    public ResponseEntity<AuthorDto> partialUpdateAuthor(@PathVariable("id") Long id, @RequestBody AuthorDto authorDto) {
        if (!authorService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        AuthorEntity authorEntity = authorMapper.mapFrom(authorDto);
        AuthorEntity updatedAuthor = authorService.patchAuthor(id, authorEntity);
        return new ResponseEntity<>(
                authorMapper.mapTo(updatedAuthor),
                HttpStatus.OK);

    }

    @DeleteMapping(path = "/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable("id") Long id) {
        authorService.deleteAuthor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

