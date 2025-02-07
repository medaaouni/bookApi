package com.devtiro.database.services;

import com.devtiro.database.domain.entities.UserEntity;

import java.util.Optional;

public interface UserService {
    public Optional<UserEntity> findByUsername(String username);
}
