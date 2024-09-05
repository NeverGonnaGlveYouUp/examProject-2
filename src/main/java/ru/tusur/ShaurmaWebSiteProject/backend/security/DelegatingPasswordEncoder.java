package ru.tusur.ShaurmaWebSiteProject.backend.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@Configuration
public class DelegatingPasswordEncoder {
    @Bean
    @Qualifier("delegatingPasswordEncoder")
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        org.springframework.security.crypto.password.DelegatingPasswordEncoder x = (org.springframework.security.crypto.password.DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        x.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return x;
    }
}
