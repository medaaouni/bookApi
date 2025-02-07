package com.devtiro.database.domain.dto;


import com.devtiro.database.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDto {
    private int id;
    private String username;
    private String password;
    private Role role;

}


