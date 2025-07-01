package ait.cohort5860.security;

import ait.cohort5860.accounting.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain getSecurityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/account/register", "/forum/posts/**")
                    .permitAll()
                .requestMatchers("/account/login/{login}/role/{role}", "/forum/post/{author}")
                    .hasRole(Role.ADMINISTRATOR.name())
                .requestMatchers(HttpMethod.PATCH, "/account/user/{login}", "/forum//post/{id}/comment/{author}")
                    .access(new WebExpressionAuthorizationManager("#login == authentication.name"))
                .requestMatchers(HttpMethod.DELETE, "/account/user/{login}")
                    .access(new WebExpressionAuthorizationManager("#login == authentication.name or hasRole('ADMINISTRATOR')"))
                .anyRequest()
                    .authenticated());
        return http.build();
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
