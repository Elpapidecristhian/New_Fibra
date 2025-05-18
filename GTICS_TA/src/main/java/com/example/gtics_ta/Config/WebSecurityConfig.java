package com.example.gtics_ta.Config;

import com.example.gtics_ta.Entity.Usuario;
import com.example.gtics_ta.Repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final DataSource dataSource;

    public WebSecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UsuarioRepository usuarioRepository) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // Permitir recursos estáticos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/assets/**", "/front-ed/**", "/scss/**").permitAll()

                .requestMatchers("/vecino/**").hasAnyAuthority("Vecino", "admin", "SuperAdmin")
                .requestMatchers("/coordinador/**").hasAnyAuthority("Coordinador", "admin", "superadmin")
                .requestMatchers("/admin/**").hasAnyAuthority("admin", "superadmin")
                .requestMatchers("/SuperAdmin/**").hasAuthority("SuperAdmin")
                .requestMatchers("/login").permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/procesar-login")
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
                                redirectStrategy.sendRedirect(request, response, "/admin/dashboard");
                                return;
                            case "superadmin":
                                redirectStrategy.sendRedirect(request, response, "/superadmin");
                                return;
                        }
                    }
                })
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery("SELECT correo, contrasenia, activo FROM gtics.usuario WHERE correo = ?");
        users.setAuthoritiesByUsernameQuery(
                "SELECT u.correo, r.nombre FROM usuario u " +
                        "INNER JOIN roles r ON u.id_rol = r.id_rol " +
                        "WHERE u.correo = ? AND u.activo = 1"
        );
        return users;
    }
}
