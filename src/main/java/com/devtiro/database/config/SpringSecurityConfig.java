package com.devtiro.database.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // This class will allow configuring security elements.
@EnableMethodSecurity
public class SpringSecurityConfig {


    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     *
     * Spring Security applique automatiquement le préfixe ROLE_ uniquement lorsque vous utilisez hasRole("XXX") ou hasAnyRole("XXX").
     *
     * authorizeRequests.requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN") // ✅ Correct si vous stockez les rôles avec ROLE_
     *
     *  hasAnyRole("ROLE1", "ROLE2", ...)Vérifie si l'utilisateur possède au moins un des rôles donnés.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                authorizeRequests -> {
//                    authorizeRequests.requestMatchers("/public/**").permitAll();// Allow public access
                    authorizeRequests.requestMatchers("/admin").hasRole("ADMIN");
                    authorizeRequests.requestMatchers("/user").hasRole("USER");
                    authorizeRequests.anyRequest().authenticated();
                }).formLogin(Customizer.withDefaults());
        return http.build();

    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     *Cette méthode permet d’indiquer à Spring Security d’utiliser la classe CustomUserDetailsService pour authentifier des utilisateurs
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
        return authenticationManagerBuilder.build();
    }




    /* if no configuration is defined, Spring applies
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated()) // Protège toutes les URLs
            .formLogin(withDefaults())  // Active un formulaire de login par défaut
            .httpBasic(withDefaults()); // Active l'authentification HTTP Basic
    return http.build();
}
*/

//    @Bean
//    public UserDetailsService userDetailsService() {
//
//        UserDetails user = User.builder()
//                .username("user")
//                .password(passwordEncoder().encode("user"))
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordEncoder().encode("admin"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user, admin);
//
//    }

}



