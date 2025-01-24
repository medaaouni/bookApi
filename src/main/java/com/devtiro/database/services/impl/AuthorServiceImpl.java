package com.devtiro.database.services.impl;

import com.devtiro.database.domain.dto.AuthorDto;
import com.devtiro.database.domain.entities.AuthorEntity;
import com.devtiro.database.repositories.AuthorRepository;
import com.devtiro.database.services.AuthorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class AuthorServiceImpl implements AuthorService {

    private AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorEntity saveAuthor(AuthorEntity authorEntity) {
        return authorRepository.save(authorEntity);
    }

    @Override
    public List<AuthorEntity> findAll() {
        Iterable<AuthorEntity> authorEntities = authorRepository.findAll();
        return StreamSupport.stream(
                        authorEntities.spliterator(),
                        false
                )
                .collect(Collectors.toList());
    }

    @Override
    public Optional<AuthorEntity> findOne(Long id) {
        return authorRepository.findById(id);
    }

    @Override
    public boolean isExists(Long id) {
        return authorRepository.existsById(id);
    }

    @Override
    public AuthorEntity patchAuthor(Long id, AuthorEntity authorEntity) {
        authorEntity.setId(id);
       return authorRepository.findById(id).map(author -> {
           Optional.ofNullable(authorEntity.getName()).ifPresent(author::setName);
           Optional.ofNullable(authorEntity.getAge()).ifPresent(author::setAge);
           return authorRepository.save(author);
       }).orElseThrow(() -> new RuntimeException("Error"));
    }

    // other way to do it
    public List<AuthorEntity> finAllAlternative() {
        List<AuthorEntity> authorEntities = new ArrayList<>();
        authorRepository.findAll().forEach(authorEntities::add);
        return authorEntities;
    }

}
