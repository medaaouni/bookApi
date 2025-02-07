package com.devtiro.database.config;

import com.devtiro.database.domain.entities.UserEntity;
import com.devtiro.database.domain.enums.Role;
import com.devtiro.database.repositories.UserRepository;
import com.devtiro.database.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired     // In Spring 4.3+, if you have only one constructor, Spring automatically treats it as an autowired constructor and injects the dependencies, even if you don’t explicitly annotate it with @Autowired.
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userService.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                getGrantedAuthorityList(userEntity.getRole())
        );
    }

    public List<GrantedAuthority> getGrantedAuthorityList(Role role){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;

        // Convert roles to authorities in case roles is a collection in separate table
//        var authorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
//                .collect(Collectors.toList());
    }
}
