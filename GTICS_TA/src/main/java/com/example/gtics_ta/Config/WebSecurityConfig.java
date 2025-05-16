package com.example.gtics_ta.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    final DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        String sql1 = "SELECT correo, contrasenia, activo FROM usuario WHERE correo = ?";
        String sql2 = "SELECT u.correo, r.nombre FROM usuario u "
                        + "INNER JOIN roles r ON (u.id_rol = r.id_rol) "
                        + "WHERE u.correo = ? and u.activo=1";
        users.setUsersByUsernameQuery(sql1);
        users.setAuthoritiesByUsernameQuery(sql2);
        return users;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/processLogin")
                .successHandler((request, response, authentication) -> {

                    String rol = "";
                    for (GrantedAuthority role : authentication.getAuthorities()) {
                        rol = role.getAuthority();
                        break;
                    }
                    if (rol.equals("Vecino")) {
                        response.sendRedirect("/vecino");
                    } else {
                        response.sendRedirect("/vecino");
                    }
                });

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/");

        http.authorizeHttpRequests()
                .requestMatchers("/vecino/", "/vecino/**").hasAuthority("Vecino")
                .anyRequest().permitAll();

        return http.build();
    }
}
