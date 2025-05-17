package com.example.gtics_ta.config;

import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.repository.UsuarioRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public WebSecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return correo -> {
            Usuario usuario = usuarioRepository.findByCorreo(correo)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            if (usuario.isBaneado()) {
                throw new UsernameNotFoundException("Usuario baneado");
            }

            return User.builder()
                    .username(usuario.getCorreo())
                    .password(usuario.getContrasenia()) // debe estar cifrada
                    .authorities("ROLE_" + usuario.getRol().getNombre().toUpperCase())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        //permitir recursos estaticos
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/front-ed/**", "/scss/**").permitAll()

                        .requestMatchers("/vecino/**").hasAnyAuthority("vecino", "admin", "superadmin")
                        .requestMatchers("/coordinador/**").hasAnyAuthority("coordinador","admin","superadmin")
                        .requestMatchers("/admin/**").hasAnyAuthority("admin","superadmin")
                        .requestMatchers("/SuperAdmin/**").hasRole("SUPERADMIN")
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/SuperAdmin/Dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout.permitAll())
                .exceptionHandling(handler -> handler.accessDeniedPage("/access-denied"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

