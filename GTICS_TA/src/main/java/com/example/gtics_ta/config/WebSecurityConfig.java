package com.example.gtics_ta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests( auth -> auth
                .requestMatchers("/coordinador/**").hasAnyAuthority("coordinador","admin","superadmin")
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/Login/login")
                .loginProcessingUrl("/procesar-login")
                .successHandler((request, response, authentication) -> {
                    //una vez autenticado se redirecciona a una pagina segun el rol
                    if(authentication.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("coordinador"))) {
                        response.sendRedirect("/coordinador/principal");
                    }
                })
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)

        );
        return http.build();
    }
    //Encriptar contraseñas
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsManager users(DataSource dataSource){
        JdbcUserDetailsManager users =new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("SELECT correo, contrasenia, is_baneado FROM gtics.usuario where correo = ?");
        users.setAuthoritiesByUsernameQuery("SELECT u.correo, r.nombre FROM usuario u INNER JOIN roles r ON u.id_rol = r.id_rol WHERE u.correo = ? and u.is_baneado = 1");

        return users;
    }
}
