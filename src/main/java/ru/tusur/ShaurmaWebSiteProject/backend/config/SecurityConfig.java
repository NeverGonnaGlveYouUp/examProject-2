package ru.tusur.ShaurmaWebSiteProject.backend.config;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.tusur.ShaurmaWebSiteProject.frontend.LoginView;

@EnableWebSecurity
@Configuration
//@SpringBootApplication
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) throws Exception{
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password(passwordEncoder.encode("userPass"))
                        .roles("USER")
                        .build(),
                User.withUsername("admin")
                        .password(passwordEncoder.encode("adminPass"))
                        .roles("ADMIN", "USER")
                        .build()
        );
    }
}
