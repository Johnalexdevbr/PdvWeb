package com.pdv.pdv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@SpringBootApplication
public class PdvApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdvApplication.class, args);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF para permitir requisições POST do seu JavaScript/Controller
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // Permite acesso aos arquivos estáticos e páginas básicas
                        .requestMatchers("/login", "/css/**", "/js/**", "/img/**").permitAll()
                        // Garante que a API de venda possa receber os dados do front-end
                        .requestMatchers("/venda/api/**").permitAll()
                        // Qualquer outra página exige login
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true) // Redireciona para o PDV após logar
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}