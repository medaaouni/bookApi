package com.devtiro.database.domain.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    private String isbn;
    private String title;
    @ManyToOne(cascade = CascadeType.ALL) /*This means that any operation (like persist, merge, remove, etc.) on the BookEntity should cascade to the AuthorEntity.*/
    @JoinColumn(name = "author_id")
    private AuthorEntity authorEntity;
}
