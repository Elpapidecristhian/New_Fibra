package com.example.gtics_ta.Config;

import com.example.gtics_ta.Repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Lazy
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Value("${security.rememberme.key}")
    private String rememberMeKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UsuarioRepository usuarioRepository) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // Permitir recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/front-end/**", "/scss/**", "/static/**").permitAll()
                // Permitir páginas de error
                .requestMatchers("/error").permitAll()
                .requestMatchers("/vecino/api/chatbot").permitAll()
                .requestMatchers("/vecino/**").hasAnyAuthority("Vecino", "Admin")
                .requestMatchers("/coordinador/**").hasAnyAuthority("Coordinador", "Admin", "SuperAdmin")
                .requestMatchers("/admin/**").hasAnyAuthority("Admin", "SuperAdmin")
                .requestMatchers("/SuperAdmin/**").hasAuthority("SuperAdmin")
                .requestMatchers("/login/**").permitAll()
                .requestMatchers("/signup/**").permitAll()
                .anyRequest().authenticated()
        );
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/vecino/api/chatbot")
        );
        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/procesar-login")
                .failureUrl("/login?errorcred=true")
                .successHandler((request, response, authentication) -> {
                    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
                    DefaultSavedRequest defaultSavedRequest =
                            (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");

                    HttpSession session = request.getSession();
                    session.setAttribute("usuario", usuarioRepository.findByCorreo(authentication.getName()));

                    if (defaultSavedRequest != null) {
                        String targetURL = defaultSavedRequest.getRedirectUrl();
                        redirectStrategy.sendRedirect(request, response, targetURL);
                        return;
                    }

                    // Redirigir según rol
                    for (GrantedAuthority authority : authentication.getAuthorities()) {
                        String rol = authority.getAuthority().toLowerCase(); // Normaliza a minúsculas

                        switch (rol) {
                            case "vecino":
                                redirectStrategy.sendRedirect(request, response, "/vecino");
                                return;
                            case "coordinador":
                                redirectStrategy.sendRedirect(request, response, "/coordinador/principal");
                                return;
                            case "admin":
                                redirectStrategy.sendRedirect(request, response, "/admin/");
                                return;
                            case "superadmin":
                                redirectStrategy.sendRedirect(request, response, "/SuperAdmin");
                                return;
                        }
                    }
                })
                .permitAll()
        );

        http.rememberMe(remember -> remember
                .key(rememberMeKey)
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .userDetailsService(userDetailsService)
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
        );

        // Configurar manejo de excepciones
        http.exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/login?error=access-denied")
        );

        // Configurar CSRF para formularios multipart
        http.csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository())
                .ignoringRequestMatchers("/vecino/guardarreserva","/coordinador/**")
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager users() {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(this.dataSource);
        users.setUsersByUsernameQuery("SELECT correo, contrasenia, activo FROM usuario WHERE correo = ? AND activo = 1");
        users.setAuthoritiesByUsernameQuery(
                "SELECT u.correo, r.nombre FROM usuario u " +
                        "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                        "WHERE u.correo = ? AND u.activo = 1"
        );
        return users;
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-CSRF-TOKEN");
        return repository;
    }


}