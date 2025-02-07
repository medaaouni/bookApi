package com.devtiro.database.repositories;

import com.devtiro.database.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * Spring Data JPA will generate a query for you based on the method name
     */
    public Optional<UserEntity> findByUsername(String username);


    // Custom query using native SQL if needed
    @Query(value = "SELECT * FROM users WHERE email = ?1 AND status = ?2", nativeQuery = true)
    UserEntity findByEmailAndStatusNative(String email, String status);

}